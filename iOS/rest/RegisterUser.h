//
//  RegisterUser.h
//  kikback
//
//  Created by Ian Barile on 12/9/12.
//  Copyright (c) 2012 Ian Barile. All rights reserved.
//

#import <Foundation/Foundation.h>

@class PostRequest;

@interface RegisterUser : NSObject
{
  PostRequest* request;
}

-(void)makeRequest:(NSDictionary*)requestData;

@end

