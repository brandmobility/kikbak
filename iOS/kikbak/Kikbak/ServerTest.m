//
//  ServerTest.m
//  kikback
//
//  Created by Ian Barile on 12/11/12.
//  Copyright (c) 2012 Ian Barile. All rights reserved.
//

#import "ServerTest.h"
#import "ShareExperienceRequest.h"
#import "RewardRequest.h"
#import "RedeemGiftRequest.h"
#import "RedeemKikbakRequest.h"

@implementation ServerTest

-(void)testShareExperience{
  ShareExperienceRequest* request = [[ShareExperienceRequest alloc]init];
  
  NSMutableDictionary* data = [[NSMutableDictionary alloc]initWithCapacity:4];
  [data setObject:[NSNumber numberWithInt:1] forKey:@"locationId"];
  [data setObject:[NSNumber numberWithInt:1] forKey:@"merchantId"];
  [data setObject:[NSNumber numberWithInt:1] forKey:@"offerId"];
  [data setObject:[NSNumber numberWithInt:1] forKey:@"friendFacebookId"];
  
  [request makeRequest:data];
}

-(void)testReward{
  RewardRequest* request = [[RewardRequest alloc]init];
  [request makeRequest:[[NSDictionary alloc]init]];
}

-(void)testRedeemGift{
  RedeemGiftRequest* request = [[RedeemGiftRequest alloc]init];
  NSMutableDictionary* data = [[NSMutableDictionary alloc] initWithCapacity:3];
  [data setObject:[NSNumber numberWithInt:1] forKey:@"id"];
  [data setObject:[NSNumber numberWithInt:1] forKey:@"locationId"];
  [data setObject:@"verification" forKey:@"verificationCode"];

  [request makeRequest:data];
  
}

-(void)testRedeemKikbak{
  RedeemKikbakRequest* request = [[RedeemKikbakRequest alloc]init];
  NSMutableDictionary* data = [[NSMutableDictionary alloc] initWithCapacity:4];
  [data setObject:[NSNumber numberWithInt:1] forKey:@"id"];
  [data setObject:[NSNumber numberWithInt:1] forKey:@"locationId"];
  [data setObject:[NSNumber numberWithDouble:32.2] forKey:@"amount"];
  [data setObject:@"verification" forKey:@"verificationCode"];
  
  [request makeRequest:data];
}

@end
