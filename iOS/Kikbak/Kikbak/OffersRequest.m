//
//  OffersRequest.m
//  kikback
//
//  Created by Ian Barile on 12/11/12.
//  Copyright (c) 2012 Ian Barile. All rights reserved.
//

#import "OffersRequest.h"
#import "KikbakConstants.h"
#import "SBJson.h"

static NSString* resource = @"user/offer";

@interface OffersRequest()
-(NSDictionary*)formatRequest:(id)requestData;
@end

@implementation OffersRequest


-(void)makeRequest:(NSDictionary*)requestData{
  
  NSUserDefaults* prefs = [NSUserDefaults standardUserDefaults];
  
  request = [[PostRequest alloc]init];
  request.resource = [NSString stringWithFormat:@"%@/%@/", resource, [prefs objectForKey:KIKBAK_USER_ID] ];
  
  
  NSString* body = [[self formatRequest:requestData] JSONRepresentation];
  request.body = body;
  request.restDelegate = self;
  [request makeSyncRequest];
  
}

-(NSDictionary*)formatRequest:(id)requestData{
  NSMutableDictionary* result = [[NSMutableDictionary alloc]initWithCapacity:1];
  NSMutableDictionary* user =[[NSMutableDictionary alloc]initWithCapacity:1];
  
  [user setObject:requestData forKey:@"userLocation"];
  [result setObject:user forKey:@"GetUserOffersRequest"];
  return result;
}


-(void)receivedData:(NSData*)data{
  
}

@end
