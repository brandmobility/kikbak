//
//  RegisterUser.h
//  kikback
//
//  Created by Ian Barile on 12/9/12.
//  Copyright (c) 2012 Ian Barile. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "ResponseHandlerProtocol.h"
#import "RestProtocol.h"

@class HttpRequest;

@interface RegisterUserRequest : NSObject<RestProtocol, ResponseHandlerProtocol>
{
  HttpRequest* request;
}


@end

