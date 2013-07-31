//
//  NSURL+QueryString.h
//  Kikbak
//
//  Created by Ian Barile on 7/26/13.
//  Copyright (c) 2013 Ian Barile. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface NSURL (QueryString)
+(NSURL*)URLwithQueryString:(NSString*)host withQueryParams:(NSDictionary*)params;
@end
