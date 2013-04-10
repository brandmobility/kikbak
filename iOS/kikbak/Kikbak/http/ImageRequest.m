//
//  ImageRequest.m
//  Kikbak
//
//  Created by Ian Barile on 4/8/13.
//  Copyright (c) 2013 Ian Barile. All rights reserved.
//

#import "ImageRequest.h"
#import "ImagePersistor.h"
#import "AppDelegate.h"


@implementation ImageRequest

-(void)requestMerchangeImage:(NSString*)imageUrl forMerchantId:(NSNumber*)merchantId {

    _merchantId = merchantId;
    request = [[HttpRequest alloc]init];
    request.resource = imageUrl;
    
    
    request.restDelegate = self;
    [request httpGetRequest];
    
}



-(void)parseResponse:(NSData*)data{
    [ImagePersistor persistMerchantImage:data forMerchantId:_merchantId];
    [[NSNotificationCenter defaultCenter]postNotificationName:kKikbakOfferUpdate object:nil];
}
@end
