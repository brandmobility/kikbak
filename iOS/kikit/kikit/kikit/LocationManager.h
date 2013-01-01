//
//  LocationManager.h
//  kikback
//
//  Created by Ian Barile on 12/11/12.
//  Copyright (c) 2012 Ian Barile. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <CoreLocation/CoreLocation.h>

@class OffersRequest;

@interface LocationManager : NSObject<CLLocationManagerDelegate>
{
  CLLocationManager* locationMgr;
  OffersRequest* request;
}

-(void)startUpdating;
-(void)stopUpdating;

@property (strong, nonatomic) CLLocation* currentLocation;

@end
