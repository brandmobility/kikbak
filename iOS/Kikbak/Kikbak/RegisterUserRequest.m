//
//  RegisterUser.m
//  kikback
//
//  Created by Ian Barile on 12/9/12.
//  Copyright (c) 2012 Ian Barile. All rights reserved.
//

#import "RegisterUserRequest.h"
#import "PostRequest.h"
#import "SBJson.h"
#import "KikbakConstants.h"
#import "LocationManager.h"
#import "AppDelegate.h"
#import "DeviceTokenRequest.h"
#import "FBQuery.h"

static NSString* resource = @"user/register/fb/";

@interface RegisterUserRequest()
-(NSDictionary*)formatRequest:(NSDictionary*)requestData;
@end

@implementation RegisterUserRequest


-(void)makeRequest:(NSDictionary*)requestData{
  
  request = [[PostRequest alloc]init];
  request.resource = [NSString stringWithFormat:@"%@", resource];
  
  
  NSString* body = [[self formatRequest:requestData] JSONRepresentation];
  request.body = body;
  request.restDelegate = self;
  [request makeSyncRequest];
  
}

-(NSDictionary*)formatRequest:(NSDictionary*)requestData{
  NSMutableDictionary* result = [[NSMutableDictionary alloc]initWithCapacity:1];
  NSMutableDictionary* user =[[NSMutableDictionary alloc]initWithCapacity:1];
  [user setObject:requestData forKey:@"user"];
  [result setObject:user forKey:@"RegisterUserRequest"];
  return result;
}


-(void)receivedData:(NSData*)data{
    NSString* json = [[NSString alloc]initWithData:data encoding:NSUTF8StringEncoding];
    NSDictionary* dict = [json JSONValue];
    if( dict ){
        NSDictionary* registerResponse = [dict objectForKey:@"registerUserResponse"];
        if ( registerResponse ) {
            NSDictionary* user = [registerResponse objectForKey:@"userId"];
            NSLog(@"registerResponse: %@, \nkeys: %@", registerResponse, [registerResponse allKeys]);
            if( user ){
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
                    [tokenRequest makeRequest:tokenDict];
                }
            }
        }
    }
}

@end
