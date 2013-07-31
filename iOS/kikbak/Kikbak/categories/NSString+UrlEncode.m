//
//  NSString+UrlEncode.m
//  Kikbak
//
//  Created by Ian Barile on 7/26/13.
//  Copyright (c) 2013 Ian Barile. All rights reserved.
//

#import "NSString+UrlEncode.h"

@implementation NSString (UrlEncode)

+ (NSString*)encodeURL:(NSString *)string
{
    NSString *newString = (__bridge NSString *)CFURLCreateStringByAddingPercentEscapes(kCFAllocatorDefault, (__bridge CFStringRef)string, NULL, CFSTR(":/?#[]@!$ &'()*+,;=\"<>%{}|\\^~`"), CFStringConvertNSStringEncodingToEncoding(NSUTF8StringEncoding));
    
    if (newString)
    {
        return newString;
    }
    
    return @"";
}

@end
