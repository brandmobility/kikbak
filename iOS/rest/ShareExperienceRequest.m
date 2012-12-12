//
//  ShareExperienceRequest.m
//  kikback
//
//  Created by Ian Barile on 12/11/12.
//  Copyright (c) 2012 Ian Barile. All rights reserved.
//

#import "ShareExperienceRequest.h"
#import "SBJson.h"
#import "KikbakConstants.h"

static NSString* resource = @"ShareExperience";

@interface ShareExperienceRequest()
-(NSDictionary*)formatRequest:(id)requestData;
@end

@implementation ShareExperienceRequest

-(void)makeRequest:(NSDictionary*)requestData{
  
  NSUserDefaults* prefs = [NSUserDefaults standardUserDefaults];
  
  request = [[PostRequest alloc]init];
  request.resource = [NSString stringWithFormat:@"%@/%@/", resource, [prefs objectForKey:KIKBAK_USER_ID] ];
  
  
  NSString* body = [[self formatRequest:requestData] JSONRepresentation];
  NSLog(@"Body: %@",body);
  request.body = body;
  request.restDelegate = self;
  [request makeSyncRequest];
  
}

-(NSDictionary*)formatRequest:(id)requestData{
  NSMutableDictionary* result = [[NSMutableDictionary alloc]initWithCapacity:1];
  NSMutableDictionary* user =[[NSMutableDictionary alloc]initWithCapacity:1];
  
  [user setObject:requestData forKey:@"experience"];
  [result setObject:user forKey:@"ShareExperienceRequest"];
  return result;
}


-(void)receivedData:(NSData*)data{
  
}

@end
