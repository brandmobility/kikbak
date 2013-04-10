//
//  SubmitFriendsRequest.h
//  kikback
//
//  Created by Ian Barile on 12/10/12.
//  Copyright (c) 2012 Ian Barile. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "RestDataDelegate.h"
#import "HttpRequest.h"
#import "RestRequestProtocol.h"

@interface SubmitFriendsRequest : NSObject<RestDataDelegate, RestRequestProtocol>
{
  HttpRequest* request;
}

@end
