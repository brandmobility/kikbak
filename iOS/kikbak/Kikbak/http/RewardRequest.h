//
//  RewardRequest.h
//  kikback
//
//  Created by Ian Barile on 12/11/12.
//  Copyright (c) 2012 Ian Barile. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "HttpRequest.h"
#import "RestProtocol.h"
#import "ResponseHandlerProtocol.h"

@interface RewardRequest : NSObject<RestProtocol, ResponseHandlerProtocol>
{
  HttpRequest* request;
}

@end
