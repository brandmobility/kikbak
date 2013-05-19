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
#import "OfferParser.h"
#import "AppDelegate.h"
#import "NotificationContstants.h"

static NSString* resource = @"user/offer";

@interface OffersRequest()
@property(strong,nonatomic)NSMutableData* data;

-(NSDictionary*)formatRequest:(id)requestData;
@end

@implementation OffersRequest


-(void)restRequest:(NSDictionary*)requestData{
  
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
  
  [user setObject:requestData forKey:@"userLocation"];
  [result setObject:user forKey:@"GetUserOffersRequest"];
  return result;
}


-(void)parseResponse:(NSData*)data{

    NSString* json = [[NSString alloc]initWithData:data encoding:NSUTF8StringEncoding];
    NSLog(@"**** offersRequest: %@", json);
    id dict = [json JSONValue];
    if( dict != [NSNull null] ){
        NSDictionary* getUserOffersResponse = [dict objectForKey:@"getUserOffersResponse"];
        if( getUserOffersResponse){
            OfferParser* parser = [[OfferParser alloc]init];
            NSArray* offers = [getUserOffersResponse objectForKey:@"offers"];
            for(id offer in offers){
                [parser parse:offer];
            }
            [parser resolveDiff];
        }
    }
    
    [[NSNotificationCenter defaultCenter]postNotificationName:kKikbakImageDownloaded object:nil];
}

-(void)handleError:(NSInteger)statusCode withData:(NSData*)data{
    
}

@end
