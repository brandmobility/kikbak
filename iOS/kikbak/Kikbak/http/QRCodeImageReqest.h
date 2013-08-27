//
//  QRCodeImageReqest.h
//  Kikbak
//
//  Created by Ian Barile on 8/26/13.
//  Copyright (c) 2013 Ian Barile. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "ResponseHandlerProtocol.h"
#import "HttpRequest.h"
#import "KikbakConstants.h"


@interface QRCodeImageReqest : NSObject <ResponseHandlerProtocol>
{
    HttpRequest* request;
}

-(void)requestQRCode;

@property (nonatomic, strong) NSString* type;
@property (nonatomic, strong) NSString* code;
@property (nonatomic, strong) NSNumber* fileId;

@end
