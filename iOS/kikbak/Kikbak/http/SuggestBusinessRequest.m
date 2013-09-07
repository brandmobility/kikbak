//
//  SuggestBusinessRequest.m
//  Kikbak
//
//  Created by Ian Barile on 9/1/13.
//  Copyright (c) 2013 Ian Barile. All rights reserved.
//

#import "SuggestBusinessRequest.h"
#import "SBJson.h"
#import "KikbakConstants.h"
#import "NotificationContstants.h"

static NSString* resource = @"suggest/business";

@interface SuggestBusinessRequest()
-(NSDictionary*)formatRequest:(id)requestData;
@end


@implementation SuggestBusinessRequest

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
    NSMutableDictionary* business =[[NSMutableDictionary alloc]initWithCapacity:1];
    
    [business setObject:requestData forKey:@"business"];
    [result setObject:business forKey:@"SuggestBusinessRequest"];
    
    return result;
}


-(void)parseResponse:(NSData*)data{
//    NSString* json = [[NSString alloc]initWithData:data encoding:NSUTF8StringEncoding];
//    NSLog(@"Suggest Business Request: %@", json);
    
    [[NSNotificationCenter defaultCenter]postNotificationName:kKikbakSuggestSuccess object:nil];
}

-(void)handleError:(NSInteger)statusCode withData:(NSData*)data{
    [[NSNotificationCenter defaultCenter]postNotificationName:kKikbakSuggestError object:nil];
}

@end
