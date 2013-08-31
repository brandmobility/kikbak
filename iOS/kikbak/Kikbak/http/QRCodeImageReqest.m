//
//  QRCodeImageReqest.m
//  Kikbak
//
//  Created by Ian Barile on 8/26/13.
//  Copyright (c) 2013 Ian Barile. All rights reserved.
//

#import "QRCodeImageReqest.h"
#import "ImagePersistor.h"
#import "NotificationContstants.h"
#import "HTTPConstants.h"

static NSString* resource = @"rewards/generateQrcode";

@implementation QRCodeImageReqest

-(void)requestQRCode{
    
    NSUserDefaults* prefs = [NSUserDefaults standardUserDefaults];
    NSString* userId = [prefs objectForKey:KIKBAK_USER_ID];
    assert(userId != nil);
    if( userId == nil){
        return;
    }
    request = [[HttpRequest alloc]init];
    request.resource = [NSString stringWithFormat:@"http://%s/%s/%@/%@/%d/", service_host, kikbak_service, resource, self.code, 250 ];
    
    
    request.restDelegate = self;
    [request httpGetRequest];
    
}



-(void)parseResponse:(NSData*)data{
    ImageType type = QRCODE_GIFT_IMAGE_TYPE;
    if( [self.code compare:@"credit"] == NSOrderedSame ){
        type = QRCODE_CREDIT_IMAGE_TYPE;
    }
    NSString* imagePath = [ImagePersistor persisttImage:data fileId:self.fileId imageType:type];
    NSMutableDictionary* dict = [[NSMutableDictionary alloc]initWithCapacity:2];
    [dict setObject:imagePath forKey:@"imagePath"];
    [dict setObject:self.code forKey:@"authorizationCode"];
    
    if( type == QRCODE_GIFT_IMAGE_TYPE ){
        [[NSNotificationCenter defaultCenter]postNotificationName:kKikbakRedeemGiftSuccess object:dict];
    }
    else{
        [[NSNotificationCenter defaultCenter]postNotificationName:kKikbakRedeemCreditSuccess object:dict];
    }
}

-(void)handleError:(NSInteger)statusCode withData:(NSData*)data{
    
    [[NSNotificationCenter defaultCenter]postNotificationName:kKikbakQrcodeError object:nil];
}


@end
