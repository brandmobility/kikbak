//
//  ShareExperienceRequest.h
//  kikback
//
//  Created by Ian Barile on 12/11/12.
//  Copyright (c) 2012 Ian Barile. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "HttpRequest.h"
#import "ResponseHandlerProtocol.h"
#import "RestProtocol.h"

@interface ShareExperienceRequest : NSObject<ResponseHandlerProtocol, RestProtocol>
{
  HttpRequest* request;
}

@end
