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
#import "KikbakParser.h"
#import "AppDelegate.h"
#import "GiftService.h"
#import "KikbakService.h"
#import "NotificationContstants.h"

static NSString* resource = @"rewards/request";

@interface RewardRequest()
-(NSDictionary*)formatRequest:(id)requestData;
@end

@implementation RewardRequest

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
    [result setObject:requestData forKey:@"RewardsRequest"];
    return result;
}


-(void)parseResponse:(NSData*)data{
    NSString* json = [[NSString alloc]initWithData:data encoding:NSUTF8StringEncoding];
    NSLog(@"Reward Request: %@", json);
    id dict = [json JSONValue];
    KikbakParser* kikbakParser = [[KikbakParser alloc]init];
    GiftParser* giftParser = [[GiftParser alloc]init];
    if( dict != [NSNull null] ){
        id rewardsResponse = [dict objectForKey:@"rewardsResponse"];
        if( rewardsResponse != [NSNull null]){
            NSArray* gifts = [rewardsResponse objectForKey:@"gifts"];
            for(id gift in gifts){
                [giftParser parse:gift];
            }
            NSArray* kikbaks = [rewardsResponse objectForKey:@"kikbaks"];
            for(id kikbak in kikbaks){
                [kikbakParser parse:kikbak];
            }
        }
    }
    
    [kikbakParser resolveDiff];
    [giftParser resolveDiff];
    
    [[NSNotificationCenter defaultCenter]postNotificationName:kKikbakRewardUpdate object:nil];
}

-(void)handleError:(NSInteger)statusCode withData:(NSData*)data{
    
}

@end
