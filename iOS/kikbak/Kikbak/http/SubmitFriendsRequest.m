//
//  SubmitFriendsRequest.m
//  kikback
//
//  Created by Ian Barile on 12/10/12.
//  Copyright (c) 2012 Ian Barile. All rights reserved.
//

#import "SubmitFriendsRequest.h"
#import "KikbakConstants.h"
#import "SBJson.h"

static NSString* resource = @"user/friends/fb";

@interface SubmitFriendsRequest()
-(NSDictionary*)formatRequest:(NSDictionary*)requestData;
@end

@implementation SubmitFriendsRequest

-(void)restRequest:(NSDictionary*)requestData{
  
    NSUserDefaults* prefs = [NSUserDefaults standardUserDefaults];

    NSString* userId = [prefs objectForKey:KIKBAK_USER_ID];
    assert(userId != nil);
    if( userId == nil){
        return;
    }
    request = [[HttpRequest alloc]init];
    request.resource = [NSString stringWithFormat:@"%@/%@/", resource, userId];

    NSString* body = [[self formatRequest:requestData] JSONRepresentation];
    request.body = body;
    request.restDelegate = self;
    [request restPostRequest];
}

-(NSDictionary*)formatRequest:(id)requestData{
  NSMutableDictionary* result = [[NSMutableDictionary alloc]initWithCapacity:1];
  [result setObject:requestData forKey:@"UpdateFriendsRequest"];
  return result;
}


-(void)parseResponse:(NSData*)data{
  
}

-(void)handleError:(NSInteger)statusCode withData:(NSData*)data{
    
}

@end
