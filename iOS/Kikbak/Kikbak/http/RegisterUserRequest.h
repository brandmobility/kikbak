//
//  RegisterUser.h
//  kikback
//
//  Created by Ian Barile on 12/9/12.
//  Copyright (c) 2012 Ian Barile. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "RestDataDelegate.h"
#import "RestRequestProtocol.h"

@class HttpRequest;

@interface RegisterUserRequest : NSObject<RestDataDelegate, RestRequestProtocol>
{
  HttpRequest* request;
}


@end

