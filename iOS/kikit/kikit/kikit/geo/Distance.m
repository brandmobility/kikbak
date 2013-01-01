//
//  Distance.m
//  kikit
//
//  Created by Ian Barile on 12/30/12.
//  Copyright (c) 2012 Ian Barile. All rights reserved.
//

#import "Distance.h"
#import "AppDelegate.h"
#import "LocationManager.h"

const double km_to_miles_conversion = 0.62137;

@implementation Distance

+(NSString*)distanceToInMiles:(CLLocation*)location{
  
  CLLocation* currentLocation = ((AppDelegate*)[UIApplication sharedApplication].delegate).locationMgr.currentLocation;
  double distanceInMiles = ([currentLocation distanceFromLocation:location]/1000.0) * km_to_miles_conversion;
  return [NSString stringWithFormat:@"%.1f", distanceInMiles];
}

@end
