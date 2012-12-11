//
//  RegisterUser.h
//  kikback
//
//  Created by Ian Barile on 12/9/12.
//  Copyright (c) 2012 Ian Barile. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "RestDataDelegate.h"

@class PostRequest;

@interface RegisterUserRequest : NSObject<RestDataDelegate>
{
  PostRequest* request;
}

-(void)makeRequest:(NSDictionary*)requestData;

@end

