//
//  UIDevice+OSVersion.m
//  Kikbak
//
//  Created by Ian Barile on 10/2/13.
//  Copyright (c) 2013 Ian Barile. All rights reserved.
//

#import "UIDevice+OSVersion.h"

@implementation UIDevice (OSVersion)

+ (BOOL)osVersion7orGreater{
    return [UIDevice getDeviceSystemMajorVersion] >= 7;
}

+(NSUInteger) versionAtIndex:(NSUInteger) index {
    return [[[[[UIDevice currentDevice] systemVersion]componentsSeparatedByString:@"."] objectAtIndex:index] intValue];
}

+ (NSUInteger) getDeviceSystemMajorVersion {
    static NSUInteger _deviceSystemMajorVersion = -1;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        _deviceSystemMajorVersion = [UIDevice versionAtIndex:0];
    });
    return _deviceSystemMajorVersion;
}

@end
