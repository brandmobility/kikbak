//
//  AllocateBarcode.m
//  Kikbak
//
//  Created by Ian Barile on 11/5/13.
//  Copyright (c) 2013 Ian Barile. All rights reserved.
//

#import "AllocateBarcode.h"
#import "BarcodeImageRequest.h"
#import "NotificationContstants.h"
#import "HTTPConstants.h"
#import "SBJson.h"

static NSString* resource = @"rewards/allocateBarcode";

@implementation AllocateBarcode

-(void)allocateBarcode{
    
    NSUserDefaults* prefs = [NSUserDefaults standardUserDefaults];
    NSString* userId = [prefs objectForKey:KIKBAK_USER_ID];
    assert(userId != nil);
    if( userId == nil){
        return;
    }
    request = [[HttpRequest alloc]init];
    request.resource = [NSString stringWithFormat:@"http://%s/%s/%@/%@/%@/", service_host, kikbak_service, resource, userId, self.allocatedGiftId];
    
    
    request.restDelegate = self;
    [request httpGetRequest];
    
}



-(void)parseResponse:(NSData*)data{
    BarcodeImageRequest* imageRequest = [[BarcodeImageRequest alloc]init];
    imageRequest.allocatedGiftId = self.allocatedGiftId;
    
    NSString* json = [[NSString alloc]initWithData:data encoding:NSUTF8StringEncoding];
//    NSLog(@"AllocateBarcode: %@", json);
    id dict = [json JSONValue];
    id response = [dict objectForKey:@"barcodeResponse"];
    if( response != [NSNull null]){
        imageRequest.code = [response objectForKey:@"code"];
    }
    
    [imageRequest requestBarcode];

}

-(void)handleError:(NSInteger)statusCode withData:(NSData*)data{
    
    [[NSNotificationCenter defaultCenter]postNotificationName:kKikbakBarcodeError object:nil];
}

@end
