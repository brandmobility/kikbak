//
//  NSURL+QueryString.m
//  Kikbak
//
//  Created by Ian Barile on 7/26/13.
//  Copyright (c) 2013 Ian Barile. All rights reserved.
//

#import "NSURL+QueryString.h"
#import "NSString+UrlEncode.h"

@implementation NSURL (QueryString)

+(NSURL*)URLwithQueryString:(NSString*)host withQueryParams:(NSDictionary*)params{

    NSMutableString* query = [[NSMutableString alloc]init];
    for( id key in params ){
        [query appendFormat:@"%@=%@&", key, [NSString encodeURL:[params objectForKey:key]]];
                               
    }
    
    NSString* encodedUrl = [NSString stringWithFormat:@"%@?%@", host, query];
    return [NSURL URLWithString:encodedUrl];
}

@end
