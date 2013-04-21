//
//  LocationLoader.h
//  Kikbak
//
//  Created by Ian Barile on 4/7/13.
//  Copyright (c) 2013 Ian Barile. All rights reserved.
//

#import <Foundation/Foundation.h>

@class Location;

@interface LocationService : NSObject

+(Location*)findById:(NSNumber*)locationId;

@end
