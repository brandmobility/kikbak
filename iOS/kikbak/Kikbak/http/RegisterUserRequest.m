//
//  RegisterUser.m
//  kikback
//
//  Created by Ian Barile on 12/9/12.
//  Copyright (c) 2012 Ian Barile. All rights reserved.
//

#import "RegisterUserRequest.h"
#import "HttpRequest.h"
#import "SBJson.h"
#import "KikbakConstants.h"
#import "LocationManager.h"
#import "AppDelegate.h"
#import "DeviceTokenRequest.h"
#import "FBQuery.h"
#import <objc/runtime.h>
#import "RewardRequest.h"

static NSString* resource = @"user/register/fb/";

@interface RegisterUserRequest()
-(NSDictionary*)formatRequest:(NSDictionary*)requestData;
@end

@implementation RegisterUserRequest


-(void)restRequest:(NSDictionary*)requestData{
  
  request = [[HttpRequest alloc]init];
  request.resource = [NSString stringWithFormat:@"%@", resource];
  
  
  NSString* body = [[self formatRequest:requestData] JSONRepresentation];
  request.body = body;
  request.restDelegate = self;
  [request restPostRequest];
  
}

-(NSDictionary*)formatRequest:(NSDictionary*)requestData{
  NSMutableDictionary* result = [[NSMutableDictionary alloc]initWithCapacity:1];
  NSMutableDictionary* user =[[NSMutableDictionary alloc]initWithCapacity:1];
  [user setObject:requestData forKey:@"user"];
  [result setObject:user forKey:@"RegisterUserRequest"];
  return result;
}


-(void)parseResponse:(NSData*)data{
    NSString* json = [[NSString alloc]initWithData:data encoding:NSUTF8StringEncoding];
//    NSLog(@"Register User Request: %@", json);
    id dict = [json JSONValue];
    if( dict != [NSNull null] ){
        id registerResponse = [dict objectForKey:@"registerUserResponse"];
        if ( registerResponse != [NSNull null]) {
            id user = [registerResponse objectForKey:@"userId"];
            if( user != [NSNull null]){
                NSString* userId = [[NSString alloc]initWithFormat:@"%@",[user objectForKey:@"userId"] ];
                NSUserDefaults* prefs = [NSUserDefaults standardUserDefaults];
                [prefs setValue:userId forKeyPath:KIKBAK_USER_ID];
                [prefs synchronize];
          
                [FBQuery fbFriends];
       
                //once they log in start location mgr
                [((AppDelegate*)[UIApplication sharedApplication].delegate).locationMgr startUpdating];
                if( [prefs objectForKey:DEVICE_TOKEN_KEY] != nil ){
                    DeviceTokenRequest* tokenRequest = [[DeviceTokenRequest alloc]init];
                    NSMutableDictionary* tokenDict = [[NSMutableDictionary alloc]initWithCapacity:2];
                    [tokenDict setObject:[prefs objectForKey:DEVICE_TOKEN_KEY] forKey:@"token"];
                    [tokenDict setObject:[NSNumber numberWithInt:0] forKey:@"platform_id"];
                    [tokenRequest restRequest:tokenDict];
                }
                
                RewardRequest* rewaredRequest = [[RewardRequest alloc]init];
                [rewaredRequest restRequest:[[NSDictionary alloc]init]];
                
                // get the app delegate, so that we can reference the session property
                AppDelegate *appDelegate = [[UIApplication sharedApplication]delegate];
                if (appDelegate.session.isOpen) {
                    // valid account UI is shown whenever the session is open
                    UIStoryboard* mainBoard = [UIStoryboard storyboardWithName:@"MainStoryboard" bundle:nil];
                    UIViewController* postView = [mainBoard instantiateViewControllerWithIdentifier:@"RootViewController"];
                    appDelegate.window.rootViewController = postView;
                }
            }
            else{
                NSString* status = [registerResponse objectForKey:@"status"];
                if( [status compare:@"TOO_FEW_FRIENDS"] == NSOrderedSame ){
                    [[FBSession activeSession]closeAndClearTokenInformation];
                    UIAlertView* alert = [[UIAlertView alloc]initWithTitle:nil message:NSLocalizedString(@"qualify", nil) delegate:nil cancelButtonTitle:@"Ok" otherButtonTitles:nil];
                    [alert show];
                }
            }
        }
    }
}

-(void)handleError:(NSInteger)statusCode withData:(NSData*)data{

    [[FBSession activeSession]closeAndClearTokenInformation];
    UIAlertView* alert = [[UIAlertView alloc]initWithTitle:@"Hmmm..." message:NSLocalizedString(@"Unreachable", nil) delegate:nil cancelButtonTitle:@"Ok" otherButtonTitles:nil];
    [alert show];
}

@end
