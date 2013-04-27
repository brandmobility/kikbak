//
//  RedeemGiftRequest.h
//  kikback
//
//  Created by Ian Barile on 12/12/12.
//  Copyright (c) 2012 Ian Barile. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "HttpRequest.h"
#import "ResponseHandlerProtocol.h"
#import "RestProtocol.h"

@class Gift;

@interface RedeemGiftRequest : NSObject<RestProtocol, ResponseHandlerProtocol>{
  HttpRequest* request;
}

@property (nonatomic, strong) Gift* gift;

@end
