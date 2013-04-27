//
//  RedeemKikbakRequest.h
//  kikback
//
//  Created by Ian Barile on 12/12/12.
//  Copyright (c) 2012 Ian Barile. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "HttpRequest.h"
#import "ResponseHandlerProtocol.h"
#import "RestProtocol.h"

@class Kikbak;

@interface RedeemKikbakRequest : NSObject<RestProtocol, ResponseHandlerProtocol>{
  HttpRequest* request;
}

@property (nonatomic, strong) Kikbak* kikbak;

@end
