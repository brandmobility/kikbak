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
#import "NotificationContstants.h"
#import "Kikbak.h"

static NSString* resource = @"rewards/redeem/kikbak";

@interface RedeemKikbakRequest()
-(NSDictionary*)formatRequest:(id)requestData;
@end

@implementation RedeemKikbakRequest

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
  
  [user setObject:requestData forKey:@"kikbak"];
  [result setObject:user forKey:@"RedeemKikbakRequest"];
  return result;
}


-(void)parseResponse:(NSData*)data{
    NSString* json = [[NSString alloc]initWithData:data encoding:NSUTF8StringEncoding];
    NSLog(@"RedeemKikbakRequest: %@", json);

    NSManagedObjectContext* context = self.kikbak.managedObjectContext;

    NSString* authorizationCode;
    id dict = [json JSONValue];
    if( dict) {
        id kikbakResponse = [dict objectForKey:@"redeemKikbakResponse"];
        if(kikbakResponse){
            id response = [kikbakResponse objectForKey:@"response"];
            if( response ){
                authorizationCode = [response objectForKey:@"authorizationCode"];
                NSNumber* balance = [response objectForKey:@"balance"];
                if( [balance integerValue ] == 0){
                    self.kikbak.location = nil;
                    [context deleteObject:self.kikbak];
                }
                else{
                    self.kikbak.value = balance;
                }
            }
        }
    }
    
    NSError* error;
    [context save:&error];
    
    if(error){
        NSLog(@"Unresolved error %@, %@", error, [error userInfo]);
    }


    [[NSNotificationCenter defaultCenter]postNotificationName:kKikbakRedeemKikbakSuccess object:authorizationCode];
}

-(void)handleError:(NSInteger)statusCode withData:(NSData*)data{
    [[NSNotificationCenter defaultCenter]postNotificationName:kKikbakRedeemKikbakError object:nil];
}

@end
