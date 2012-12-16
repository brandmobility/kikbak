//
//  PostRequest.h
//  kikback
//
//  Created by Ian Barile on 12/8/12.
//  Copyright (c) 2012 Ian Barile. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "RestDataDelegate.h"

@interface PostRequest : NSObject<NSURLConnectionDelegate, NSURLConnectionDataDelegate>
{
}

@property(nonatomic, strong) NSString* resource;
@property(nonatomic, strong) NSString* body;
@property(nonatomic, strong) NSMutableData* receivedData;
@property(nonatomic, strong) id<RestDataDelegate> restDelegate;

-(void)makeSyncRequest;

@end
