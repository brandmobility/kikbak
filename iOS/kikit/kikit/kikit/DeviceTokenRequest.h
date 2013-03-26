//
//  DeviceTokenRequest.h
//  kikback
//
//  Created by Ian Barile on 12/11/12.
//  Copyright (c) 2012 Ian Barile. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "RestDataDelegate.h"
#import "RestRequestProtocol.h"
#import "PostRequest.h"


@interface DeviceTokenRequest : NSObject <RestDataDelegate, RestRequestProtocol>
{
  PostRequest* request;
}

@end