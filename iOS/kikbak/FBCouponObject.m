//
//  FBObject.m
//  Kikbak
//
//  Created by Ian Barile on 8/6/13.
//  Copyright (c) 2013 Ian Barile. All rights reserved.
//

#import "FBCouponObject.h"
#import <FacebookSDK/FacebookSDK.h>
#import "SBJson.h"
#import "NotificationContstants.h"
#import "Location.h"
#import "LocationService.h"
#import "FBConstants.h"

@implementation FBCouponObject

-(void)postCoupon:(NSString*)url{
  
    NSString* title = [NSString stringWithFormat:NSLocalizedString(@"Fb coupon title", nil), self.gift, self.merchant];
    
    FBRequestConnection *connection = [[FBRequestConnection alloc] init];
    NSMutableDictionary* gift = [[NSMutableDictionary alloc]initWithCapacity:5];
    [gift setObject:title forKey:@"title"];
    
    //only do shared from if near story
    NSString* body = [NSString stringWithFormat:@"%@ %@.", self.gift, self.detailedDescription];
    
    [gift setObject:body forKey:@"description"];
    [gift setObject:url forKey:@"image"];
    [gift setObject:self.landingUrl forKey:@"url"];
    
    Location* location = [LocationService findById:self.locationId];
    if( location ){
        NSUserDefaults* prefs = [NSUserDefaults standardUserDefaults];
        NSString* first = [prefs objectForKey:@(FB_FIRST_NAME_KEY)];
        NSMutableDictionary* data = [[NSMutableDictionary alloc]initWithCapacity:1];
        NSString* merchant = [NSString stringWithFormat:@"%@ was at %@ at %@ in %@, %@", first, self.merchant, location.address, location.city, location.state];
        [data setObject:merchant forKey:@"second_line"];
        [gift setObject:data forKey:@"data"];
    }
    
    
  
    NSDictionary *request1Params = [[NSDictionary alloc]
                                  initWithObjectsAndKeys:
                                  [gift JSONRepresentation], @"object",
                                  nil];
    FBRequest *request1 =  [FBRequest requestWithGraphPath:@"me/objects/referredlabs:coupon"
                                              parameters:request1Params
                                              HTTPMethod:@"POST"];
    [connection addRequest:request1
          completionHandler:
         ^(FBRequestConnection *connection, id result, NSError *error) {
             //todo: what todo with errors
           if (!error) {
           }else{
           }
         }
          batchEntryName:@"status-post"
    ];
  
    NSMutableDictionary* actionParams = [[NSMutableDictionary alloc]initWithCapacity:3];
    [actionParams setObject:@"{result=status-post:$.id}" forKey:@"coupon"];
    [actionParams setObject:@"true" forKey:@"fb:explicitly_shared"];
    [actionParams setObject:@"true" forKey:@"user_generated"];
    [actionParams setObject:self.caption forKey:@"message"];
    
    FBRequest* request2 = [FBRequest requestWithGraphPath:@"me/referredlabs:share"
                                               parameters:actionParams
                                               HTTPMethod:@"POST"];
    [connection addRequest:request2
         completionHandler:
     ^(FBRequestConnection *connection, id result, NSError *error) {
       if (!error) {
           [[NSNotificationCenter defaultCenter]postNotificationName:@"kKikbakFBStoryPostSuccess" object:nil];
       }else{
           [[NSNotificationCenter defaultCenter]postNotificationName:@"kKikbakFBStoryPostError" object:nil];
       }
     }
     ];
    
    [connection start];
}

@end
