//
//  ImageRequest.h
//  Kikbak
//
//  Created by Ian Barile on 4/8/13.
//  Copyright (c) 2013 Ian Barile. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "ResponseHandlerProtocol.h"
#import "HttpRequest.h"


@interface ImageRequest : NSObject <ResponseHandlerProtocol>
{
    HttpRequest* request;
    NSNumber* _merchantId;
}

-(void)requestMerchangeImage:(NSString*)imageUrl forMerchantId:(NSNumber*)offerid;


@end
