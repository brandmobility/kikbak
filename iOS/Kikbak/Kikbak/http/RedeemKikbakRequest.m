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

    self.kikbak.location = nil;
    NSManagedObjectContext* context = self.kikbak.managedObjectContext;
    [context deleteObject:self.kikbak];
    
    NSError* error;
    [context save:&error];
    
    if(error){
        NSLog(@"Unresolved error %@, %@", error, [error userInfo]);
    }
    
    [[NSNotificationCenter defaultCenter]postNotificationName:kKikbakRedeemKikbakSuccess object:nil];
}

-(void)handleError:(NSInteger)statusCode withData:(NSData*)data{
    [[NSNotificationCenter defaultCenter]postNotificationName:kKikbakRedeemKikbakError object:nil];
}

@end
