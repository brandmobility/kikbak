//
//  SMSMessageBodyRequest.h
//  Kikbak
//
//  Created by Ian Barile on 7/26/13.
//  Copyright (c) 2013 Ian Barile. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "ResponseHandlerProtocol.h"
#import "HttpRequest.h"
#import "KikbakConstants.h"


@interface SMSMessageBodyRequest : NSObject <ResponseHandlerProtocol>
{
    HttpRequest* request;
}

-(void)requestSMSBody;

@property (nonatomic, strong) NSString* name;
@property (nonatomic, strong) NSString* code;
@property (nonatomic, strong) NSString* description;

@end
