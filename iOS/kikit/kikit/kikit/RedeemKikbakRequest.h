//
//  RedeemKikbakRequest.h
//  kikback
//
//  Created by Ian Barile on 12/12/12.
//  Copyright (c) 2012 Ian Barile. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "PostRequest.h"
#import "RestDataDelegate.h"
#import "RestRequestProtocol.h"

@interface RedeemKikbakRequest : NSObject<RestDataDelegate, RestRequestProtocol>{
  PostRequest* request;
}

@end
