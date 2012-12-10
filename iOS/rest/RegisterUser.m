//
//  RegisterUser.m
//  kikback
//
//  Created by Ian Barile on 12/9/12.
//  Copyright (c) 2012 Ian Barile. All rights reserved.
//

#import "RegisterUser.h"
#import "PostRequest.h"
#import "SBJson.h"

NSString* resource = @"user/register/fb/";

@interface RegisterUser()
-(NSDictionary*)formatRequest:(NSDictionary*)requestData;
@end

@implementation RegisterUser


-(void)makeRequest:(NSDictionary*)requestData{
  
  request = [[PostRequest alloc]init];
  request.resource = [NSString stringWithFormat:@"%@", resource];
  
  
  NSString* body = [[self formatRequest:requestData] JSONRepresentation];
  request.body = body;
  [request makeSyncRequest];
  
}

-(NSDictionary*)formatRequest:(NSDictionary*)requestData{
  NSMutableDictionary* result = [[NSMutableDictionary alloc]initWithCapacity:1];
  NSMutableDictionary* user =[[NSMutableDictionary alloc]initWithCapacity:1];
  [user setObject:requestData forKey:@"user"];
  [result setObject:user forKey:@"RegisterUserRequest"];
  return result;
}

@end
