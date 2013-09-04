//
//  SuggestBusinessRequest.h
//  Kikbak
//
//  Created by Ian Barile on 9/1/13.
//  Copyright (c) 2013 Ian Barile. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "HttpRequest.h"
#import "ResponseHandlerProtocol.h"
#import "RestProtocol.h"


@interface SuggestBusinessRequest : NSObject<ResponseHandlerProtocol, RestProtocol>
{
    HttpRequest* request;
}
@end
