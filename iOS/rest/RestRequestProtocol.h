//
//  RestRequestProtocol.h
//  kikback
//
//  Created by Ian Barile on 12/11/12.
//  Copyright (c) 2012 Ian Barile. All rights reserved.
//

#import <Foundation/Foundation.h>

@protocol RestRequestProtocol <NSObject>

-(void)makeRequest:(NSDictionary*)requestData;

@end
