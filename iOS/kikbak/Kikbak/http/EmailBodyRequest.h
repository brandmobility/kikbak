//
//  EmailBodyRequest.h
//  Kikbak
//
//  Created by Ian Barile on 7/30/13.
//  Copyright (c) 2013 Ian Barile. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "ResponseHandlerProtocol.h"
#import "HttpRequest.h"
#import "KikbakConstants.h"

@interface EmailBodyRequest : NSObject <ResponseHandlerProtocol>
{
    HttpRequest* request;
}

-(void)requestEmailBody;

@property (nonatomic, strong) NSString* name;
@property (nonatomic, strong) NSString* code;
@property (nonatomic, strong) NSString* description;

@end
