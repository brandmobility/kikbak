//
//  ClaimCreditRequest.m
//  Kikbak
//
//  Created by Ian Barile on 7/15/13.
//  Copyright (c) 2013 Ian Barile. All rights reserved.
//

#import "ClaimCreditRequest.h"
#import "KikbakConstants.h"
#import "NotificationContstants.h"
#import "SBJson.h"


static NSString* resource = @"rewards/claim";

@interface ClaimCreditRequest()
-(NSDictionary*)formatRequest:(id)requestData;
@end

@implementation ClaimCreditRequest

-(void)restRequest:(NSDictionary*)requestData{
    
    NSUserDefaults* prefs = [NSUserDefaults standardUserDefaults];
    
    NSString* userId = [prefs objectForKey:KIKBAK_USER_ID];
    assert(userId != nil);
    if( userId == nil){
        return;
    }
    request = [[HttpRequest alloc]init];
    request.resource = [NSString stringWithFormat:@"%@/%@/", resource,  userId];
    
    
    NSString* body = [[self formatRequest:requestData] JSONRepresentation];
    NSLog(@"Body: %@", body);
    request.body = body;
    request.restDelegate = self;
    [request restPostRequest];
    
}

-(NSDictionary*)formatRequest:(id)requestData{
    NSMutableDictionary* result = [[NSMutableDictionary alloc]initWithCapacity:1];
    NSMutableDictionary* claim =[[NSMutableDictionary alloc]initWithCapacity:1];
    [claim setObject:requestData forKey:@"claim"];
    [result setObject:claim forKey:@"ClaimCreditRequest"];
    return result;
}


-(void)parseResponse:(NSData*)data{
    
    [[NSNotificationCenter defaultCenter]postNotificationName:kKikbakClaimSuccess object:nil];
}


-(void)handleError:(NSInteger)statusCode withData:(NSData*)data{

    [[NSNotificationCenter defaultCenter]postNotificationName:kKikbakClaimError object:nil];
}

@end
