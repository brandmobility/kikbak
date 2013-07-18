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
#import "NotificationContstants.h"

static NSString* resource = @"ShareExperience";

@interface ShareExperienceRequest()
-(NSDictionary*)formatRequest:(id)requestData;
@end

@implementation ShareExperienceRequest

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
    NSMutableDictionary* user =[[NSMutableDictionary alloc]initWithCapacity:1];

    [user setObject:requestData forKey:@"experience"];
    [result setObject:user forKey:@"ShareExperienceRequest"];

    return result;
}


-(void)parseResponse:(NSData*)data{
    NSString* json = [[NSString alloc]initWithData:data encoding:NSUTF8StringEncoding];
    NSLog(@"Share Experience Request: %@", json);
    id dict = [json JSONValue];
    NSString* refCode = [[dict objectForKey:@"shareExperienceResponse"] objectForKey:@"referrerCode"];
    [[NSNotificationCenter defaultCenter]postNotificationName:kKikbakShareSuccess object:refCode];
}

-(void)handleError:(NSInteger)statusCode withData:(NSData*)data{
    [[NSNotificationCenter defaultCenter]postNotificationName:kKikbakShareError object:nil];
}

@end
