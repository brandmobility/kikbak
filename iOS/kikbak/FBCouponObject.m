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
#import "ShareData.h"

@implementation FBCouponObject

-(void)postCoupon:(NSString*)url{
  
    NSString* title = [NSString stringWithFormat:@"%@: %@", self.merchant, self.gift];
    
    FBRequestConnection *connection = [[FBRequestConnection alloc] init];
    NSMutableDictionary* gift = [[NSMutableDictionary alloc]initWithCapacity:5];
    [gift setObject:title forKey:@"title"];
    if( self.detailedDescription){
        [gift setObject:self.detailedDescription forKey:@"description"];
    }
    [gift setObject:url forKey:@"image"];
    [gift setObject:@"http://test.kikbak.me/m/index.html" forKey:@"url"];
    
  
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
           ShareData* data = [[ShareData alloc]init];
           data.locationId = self.locationId;
           data.employeeName = self.employeeName;
           [[NSNotificationCenter defaultCenter]postNotificationName:@"kKikbakFBStoryPostSuccess" object:data];
       }else{
           [[NSNotificationCenter defaultCenter]postNotificationName:@"kKikbakFBStoryPostError" object:nil];
       }
     }
     ];
    
    [connection start];
}

@end
