//
//  AllocateBarcode.h
//  Kikbak
//
//  Created by Ian Barile on 11/5/13.
//  Copyright (c) 2013 Ian Barile. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "ResponseHandlerProtocol.h"
#import "HttpRequest.h"
#import "KikbakConstants.h"

@interface AllocateBarcode : NSObject <ResponseHandlerProtocol>
{
    HttpRequest* request;
}

-(void)allocateBarcode;

@property (nonatomic, strong) NSNumber* allocatedGiftId;


@end
