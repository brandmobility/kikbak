//
//  Distance.h
//  kikit
//
//  Created by Ian Barile on 12/30/12.
//  Copyright (c) 2012 Ian Barile. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <CoreLocation/CoreLocation.h>

@interface Distance : NSObject

//returns distance in miles
+(NSString*)distanceToInMiles:(CLLocation*)location;
+(double)distanceToInFeet:(CLLocation*)location;

@end
