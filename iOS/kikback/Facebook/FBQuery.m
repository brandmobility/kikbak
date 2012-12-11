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
      
      
      [FBSession openActiveSessionWithPermissions:permissions
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
      [request makeRequest:[result objectForKey:@"data"]];
      
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
      if( [prefs objectForKey:KIKBAK_USER_ID] == nil ){
        RegisterUserRequest* request = [[RegisterUserRequest alloc] init];
        [request makeRequest:result];
      }
      
      [prefs setValue:[delegate.userInfo.me objectForKey:FBUSERID_KEY] forKeyPath:FBUSERID_KEY];
      [prefs synchronize];
      [Flurry logEvent:@"MeRequestEvent" timed:YES];
      
      [FBQuery fbFriends];
    }
    else{
      [Flurry logEvent:@"MeFailedRequestEvent" timed:YES];
      NSLog(@"Submit Error: %@", error);
    }
  }];
}

@end
