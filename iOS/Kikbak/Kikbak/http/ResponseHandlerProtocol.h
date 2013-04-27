//
//  RestDataDelegate.h
//  kikback
//
//  Created by Ian Barile on 12/10/12.
//  Copyright (c) 2012 Ian Barile. All rights reserved.
//

#import <Foundation/Foundation.h>

@protocol ResponseHandlerProtocol <NSObject>

@required

-(void)parseResponse:(NSData*)data;
-(void)handleError:(NSInteger)statusCode withData:(NSData*)data;

@end
