//
//  RedeemKikbakRequest.m
//  kikback
//
//  Created by Ian Barile on 12/12/12.
//  Copyright (c) 2012 Ian Barile. All rights reserved.
//

#import "RedeemKikbakRequest.h"
#import "KikbakConstants.h"
#import "SBJson.h"

static NSString* resource = @"rewards/redeem/kikbak";

@interface RedeemKikbakRequest()
-(NSDictionary*)formatRequest:(id)requestData;
@end

@implementation RedeemKikbakRequest

-(void)makeRequest:(NSDictionary*)requestData{
  
    NSUserDefaults* prefs = [NSUserDefaults standardUserDefaults];

    NSString* userId = [prefs objectForKey:KIKBAK_USER_ID];
    assert(userId != nil);
    if( userId == nil){
        return;
    }
    request = [[PostRequest alloc]init];
    request.resource = [NSString stringWithFormat:@"%@/%@/", resource, userId ];


    NSString* body = [[self formatRequest:requestData] JSONRepresentation];
    request.body = body;
    request.restDelegate = self;
    [request makeSyncRequest];
}

-(NSDictionary*)formatRequest:(id)requestData{
  NSMutableDictionary* result = [[NSMutableDictionary alloc]initWithCapacity:1];
  NSMutableDictionary* user =[[NSMutableDictionary alloc]initWithCapacity:1];
  
  [user setObject:requestData forKey:@"kikbak"];
  [result setObject:user forKey:@"RedeemKikbakRequest"];
  return result;
}


-(void)parseResponse:(NSData*)data{
  
}

@end
