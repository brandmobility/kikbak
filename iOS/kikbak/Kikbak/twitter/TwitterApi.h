//
//  TwitterApi.h
//  Kikbak
//
//  Created by Ian Barile on 11/9/13.
//  Copyright (c) 2013 Ian Barile. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface TwitterApi : NSObject

- (BOOL)userHasAccessToTwitter;
- (void)postToTimeline:(NSString*)post;

@end
