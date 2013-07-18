//
//  ClaimCreditRequest.h
//  Kikbak
//
//  Created by Ian Barile on 7/15/13.
//  Copyright (c) 2013 Ian Barile. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "RestProtocol.h"
#import "ResponseHandlerProtocol.h"
#import "HttpRequest.h"

@interface ClaimCreditRequest : NSObject <RestProtocol, ResponseHandlerProtocol>
{
    HttpRequest* request;
}

@end
