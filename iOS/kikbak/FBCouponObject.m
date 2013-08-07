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

@implementation FBCouponObject

-(void)postCoupon:(NSString*)url{
  
    FBRequestConnection *connection = [[FBRequestConnection alloc] init];
    NSMutableDictionary* gift = [[NSMutableDictionary alloc]initWithCapacity:5];
    [gift setObject:@"test" forKey:@"title"];
    [gift setObject:@"description" forKey:@"description"];
    [gift setObject:url forKey:@"image"];
    [gift setObject:@"http://test.kikbak.me/m/index.html" forKey:@"url"];
    
    NSMutableDictionary* data = [[NSMutableDictionary alloc]initWithCapacity:2];
    [data setObject:@"att" forKey:@"merchant_name"];
    [data setObject:@"12345 32313" forKey:@"detailed_desc"];
//  [gift setObject:[data JSONRepresentation] forKey:@"data"];
  
  
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
           if (!error) {
             NSLog(@"Success");
           }else{
             NSLog(@"Error");
           }
         }
          batchEntryName:@"status-post"
    ];
  
    NSMutableDictionary* actionParams = [[NSMutableDictionary alloc]initWithCapacity:3];
    [actionParams setObject:@"{result=status-post:$.id}" forKey:@"coupon"];
    [actionParams setObject:@"true" forKey:@"fb:explicitly_shared"];
    [actionParams setObject:@"true" forKey:@"user_generated"];
    [actionParams setObject:@"testing this" forKey:@"message"];
    
    FBRequest* request2 = [FBRequest requestWithGraphPath:@"me/referredlabs:share"
                                               parameters:actionParams
                                               HTTPMethod:@"POST"];
    [connection addRequest:request2
         completionHandler:
     ^(FBRequestConnection *connection, id result, NSError *error) {
       if (!error) {
         NSLog(@"Success **** 2");
       }else{
         NSLog(@"Error **** 2");
       }
     }
     ];
    
    [connection start];
}

@end
