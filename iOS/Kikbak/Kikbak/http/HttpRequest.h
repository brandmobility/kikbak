//
//  PostRequest.h
//  kikback
//
//  Created by Ian Barile on 12/8/12.
//  Copyright (c) 2012 Ian Barile. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "RestDataDelegate.h"
#import "assert.h"

@interface HttpRequest : NSObject<NSURLConnectionDelegate, NSURLConnectionDataDelegate>{
    NSInteger statusCode;
}

@property(nonatomic, strong) NSString* resource;
@property(nonatomic, strong) NSString* body;
@property(nonatomic, strong) NSMutableData* receivedData;
@property(nonatomic, strong) id<RestDataDelegate> restDelegate;

-(void)restPostRequest;
-(void)httpGetRequest;

@end
