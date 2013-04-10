//
//  RedeemGiftRequest.m
//  kikback
//
//  Created by Ian Barile on 12/12/12.
//  Copyright (c) 2012 Ian Barile. All rights reserved.
//

#import "RedeemGiftRequest.h"
#import "KikbakConstants.h"
#import "SBJson.h"

static NSString* resource = @"rewards/redeem/gift";


@implementation RedeemGiftRequest

-(void)makeRequest:(NSDictionary*)requestData{
  
    NSUserDefaults* prefs = [NSUserDefaults standardUserDefaults];
  
    NSString* userId = [prefs objectForKey:KIKBAK_USER_ID];
    assert(userId != nil);
    if( userId == nil){
        return;
    }
    request = [[HttpRequest alloc]init];
    request.resource = [NSString stringWithFormat:@"%@/%@/", resource, userId ];


    NSString* body = [[self formatRequest:requestData] JSONRepresentation];
    request.body = body;
    request.restDelegate = self;
    [request restPostRequest];
  
}

-(NSDictionary*)formatRequest:(id)requestData{
  NSMutableDictionary* result = [[NSMutableDictionary alloc]initWithCapacity:1];
  NSMutableDictionary* user =[[NSMutableDictionary alloc]initWithCapacity:1];
  
  [user setObject:requestData forKey:@"gift"];
  [result setObject:user forKey:@"RedeemGiftRequest"];
  return result;
}


-(void)parseResponse:(NSData*)data{
  
}

@end
