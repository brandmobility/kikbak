//
//  BarcodeImageRequest.h
//  Kikbak
//
//  Created by Ian Barile on 7/17/13.
//  Copyright (c) 2013 Ian Barile. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "ResponseHandlerProtocol.h"
#import "HttpRequest.h"
#import "KikbakConstants.h"

@interface BarcodeImageRequest : NSObject <ResponseHandlerProtocol>
{
    HttpRequest* request;
}

-(void)requestBarcode;

@property (nonatomic, strong) NSNumber* allocatedGiftId;

@end
