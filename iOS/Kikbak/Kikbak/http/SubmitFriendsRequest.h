//
//  SubmitFriendsRequest.h
//  kikback
//
//  Created by Ian Barile on 12/10/12.
//  Copyright (c) 2012 Ian Barile. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "ResponseHandlerProtocol.h"
#import "RestProtocol.h"
#import "HttpRequest.h"

@interface SubmitFriendsRequest : NSObject<ResponseHandlerProtocol, RestProtocol>
{
  HttpRequest* request;
}

@end
