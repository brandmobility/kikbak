//
//  RewardRequest.m
//  kikback
//
//  Created by Ian Barile on 12/11/12.
//  Copyright (c) 2012 Ian Barile. All rights reserved.
//

#import "RewardRequest.h"
#import "SBJson.h"
#import "KikbakConstants.h"
#import "GiftParser.h"
#import "AppDelegate.h"

static NSString* resource = @"rewards/request";

@interface RewardRequest()
-(NSDictionary*)formatRequest:(id)requestData;
@end

@implementation RewardRequest

-(void)makeRequest:(NSDictionary*)requestData{
  
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
    [result setObject:requestData forKey:@"RewardsRequest"];
    return result;
}


-(void)parseResponse:(NSData*)data{
    NSString* json = [[NSString alloc]initWithData:data encoding:NSUTF8StringEncoding];
    NSLog(@"Reward Request: %@", json);
    id dict = [json JSONValue];
    if( dict != [NSNull null] ){
        id rewardsResponse = [dict objectForKey:@"rewardsResponse"];
        if( rewardsResponse != [NSNull null]){
            NSArray* gifts = [rewardsResponse objectForKey:@"gifts"];
            for(id gift in gifts){
                [GiftParser parse:gift];
            }
        }
    }
    
    [[NSNotificationCenter defaultCenter]postNotificationName:kKikbakGiftUpdate object:nil];
}

@end
