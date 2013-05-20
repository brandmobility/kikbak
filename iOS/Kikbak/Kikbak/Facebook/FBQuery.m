//
//  FBQuery.m
//  kikback
//
//  Created by Ian Barile on 9/30/12.
//  Copyright (c) 2012 Ian Barile. All rights reserved.
//

#import "FBQuery.h"
#import "Flurry.h"
#import "AppDelegate.h"
#import "SBJson.h"
#import "FBUserInfo.h"
#import "FBConstants.h"
#import "KikbakConstants.h"
#import "RegisterUserRequest.h"
#import "SubmitFriendsRequest.h"
#import "ImageRequest.h"
#import "ImagePersistor.h"

@implementation FBQuery

+(void)createFBSession
{
  AppDelegate* delegate = [UIApplication sharedApplication].delegate;
  if (!delegate.session.isOpen) {
    NSArray* permissions = [self FBPermissions];
    
    
    // if we don't have a cached token, a call to open here would cause UX for login to
    // occur; we don't want that to happen unless the user clicks the login button, and so
    // we check here to make sure we have a token before calling open
    if (delegate.session.state != FBSessionStateOpen) {
      // even though we had a cached token, we need to login to make the session usable
      
      
      [FBSession openActiveSessionWithPublishPermissions:permissions
                                         defaultAudience:FBSessionDefaultAudienceEveryone
                                     allowLoginUI:NO
                                completionHandler:^(FBSession *session,
                                                    FBSessionState state,
                                                    NSError *error) {
                                  delegate.session = [FBSession setActiveSession:session];
                                }];
    }
  }
  
}

+(NSArray*)FBPermissions
{
  return [[NSArray alloc]initWithObjects:@"email",@"publish_actions",@"publish_stream",@"publish_checkins",nil];
}


+(void)fbFriends{
  FBRequestConnection* connection = [[FBRequestConnection alloc]initWithTimeout:30];
  
  FBRequest* request = [FBRequest requestForMyFriends];
  [connection addRequest:request completionHandler:^(FBRequestConnection *connection, id result, NSError *error) {
    if(error == nil){
      AppDelegate* delegate =[UIApplication sharedApplication].delegate;
      delegate.userInfo.friends = result;
      SubmitFriendsRequest* request = [[SubmitFriendsRequest alloc]init];
      [request restRequest:[result objectForKey:@"data"]];
      
      [Flurry logEvent:@"FriendRequestEvent" timed:YES];
    }
    else{
      [Flurry logEvent:@"FriendFailedRequestEvent" timed:YES];
      NSLog(@"Submit Error: %@", error);
    }
  }];
  
  [connection start];
}


+(void)fbMe{
  
    //  id json;
    [FBRequestConnection startForMeWithCompletionHandler:^(FBRequestConnection *connection,
                                                         id result,
                                                         NSError *error){
    if(error == nil){
        AppDelegate* delegate =[UIApplication sharedApplication].delegate;
        delegate.userInfo.me = result;
        NSUserDefaults* prefs = [NSUserDefaults standardUserDefaults];
        NSString* testUser = @"24502071";
        if( [prefs objectForKey:KIKBAK_USER_ID] == nil ){
            RegisterUserRequest* request = [[RegisterUserRequest alloc] init];
            NSMutableDictionary* dict = [[NSMutableDictionary alloc]initWithCapacity:12];
            [dict setObject:[result objectForKey:@"email"] forKey:@"email"];
            [dict setObject:[result objectForKey:@"first_name"] forKey:@"first_name"];
            [dict setObject:[result objectForKey:@"id"] forKey:@"id"];
            [dict setObject:testUser forKey:@"id"];
            [dict setObject:[result objectForKey:@"last_name"] forKey:@"last_name"];
            [dict setObject:[result objectForKey:@"link"] forKey:@"link"];
            [dict setObject:[result objectForKey:@"locale"] forKey:@"locale"];
            [dict setObject:[result objectForKey:@"name"] forKey:@"name"];
            [dict setObject:[result objectForKey:@"timezone"] forKey:@"timezone"];
            [dict setObject:[result objectForKey:@"updated_time"] forKey:@"updated_time"];
            [dict setObject:[result objectForKey:@"username"] forKey:@"username"];
            [dict setObject:[result objectForKey:@"verified"] forKey:@"verified"];
            [dict setObject:[result objectForKey:@"gender"] forKey:@"gender"];
            [request restRequest:dict];
        }

        [prefs setValue:[delegate.userInfo.me objectForKey:@(FB_USER_ID_KEY)] forKeyPath:@(FB_USER_ID_KEY)];
        [prefs setValue:testUser forKeyPath:@(FB_USER_ID_KEY)];
        [prefs setValue:[delegate.userInfo.me objectForKey:@(FB_USERNAME_KEY)] forKeyPath:@(FB_USERNAME_KEY)];
        [prefs synchronize];
        [Flurry logEvent:@"MeRequestEvent" timed:YES];

        if( [prefs objectForKey:KIKBAK_USER_ID] ){
            [FBQuery fbFriends];
        }
    }
    else{
        [Flurry logEvent:@"MeFailedRequestEvent" timed:YES];
        NSLog(@"Submit Error: %@", error);
    }
  }];
}

+(void)resolveImageUrl:(NSNumber*)fbPictureId{
    FBRequestConnection* connection = [[FBRequestConnection alloc]initWithTimeout:30];
    
    FBRequest* request = [FBRequest requestForGraphPath:[NSString stringWithFormat:@"%@?fields=source", fbPictureId]];
    [connection addRequest:request completionHandler:^(FBRequestConnection *connection, id result, NSError *error) {
        if(error == nil){
//            NSLog(@"resolveImageUrl: %@", result);
            if(![ImagePersistor imageFileExists:[result objectForKey:@"id"] imageType:GIVE_IMAGE_TYPE]) {
                ImageRequest* requestor = [[ImageRequest alloc]init];
                requestor.url = [result objectForKey:@"source"];
                requestor.type = GIVE_IMAGE_TYPE;
                requestor.fileId = [result objectForKey:@"id"];
                [requestor requestImage];
            }
        }
        else{
            NSLog(@"Submit Error: %@", error);
        }
    }];
    
    [connection start];
}


+(void)requestProfileImage:(NSNumber*)fbUserId{
    FBRequestConnection* connection = [[FBRequestConnection alloc]initWithTimeout:30];
    
    FBRequest* request = [FBRequest requestForGraphPath:[NSString stringWithFormat:@"%@?fields=picture", fbUserId]];
    [connection addRequest:request completionHandler:^(FBRequestConnection *connection, id result, NSError *error) {
        if(error == nil){
            //            NSLog(@"resolveImageUrl: %@", result);
            if(![ImagePersistor imageFileExists:fbUserId imageType:FRIEND_IMAGE_TYPE]) {
                ImageRequest* requestor = [[ImageRequest alloc]init];
                id data = [[result objectForKey:@"picture"]objectForKey:@"data"];
                if( data ){
                    requestor.url = [data objectForKey:@"url"];
                    requestor.type = FRIEND_IMAGE_TYPE;
                    requestor.fileId = fbUserId;
                    [requestor requestImage];
                }
            }
        }
        else{
            NSLog(@"Submit Error: %@", error);
        }
    }];
    
    [connection start];
    
}

@end
