//
//  RestDataDelegate.h
//  kikback
//
//  Created by Ian Barile on 12/10/12.
//  Copyright (c) 2012 Ian Barile. All rights reserved.
//

#import <Foundation/Foundation.h>

@protocol RestDataDelegate <NSObject>

-(void)receivedData:(NSData*)data;

@end
