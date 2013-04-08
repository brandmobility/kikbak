//
//  LocationManager.m
//  kikback
//
//  Created by Ian Barile on 12/11/12.
//  Copyright (c) 2012 Ian Barile. All rights reserved.
//

#import "LocationManager.h"
#import "OffersRequest.h"
#import "KikbakConstants.h"
#import "Distance.h"
#import "AppDelegate.h"

@interface LocationManager()
-(void)onForeground;
-(void)onBackground;
@end

@implementation LocationManager

@synthesize currentLocation;

-(id)init{
  self = [super init];
  if (self != nil)
  {
    if( [CLLocationManager locationServicesEnabled] ){
      locationMgr = [[CLLocationManager alloc]init];
      locationMgr.delegate = self;
      locationMgr.desiredAccuracy = kCLLocationAccuracyNearestTenMeters;
      
      // Set a movement threshold for new events.
      locationMgr.distanceFilter = 500;
      
      
      [locationMgr startUpdatingLocation];
      
      [[NSNotificationCenter defaultCenter]addObserver:self selector:@selector(onBackground) name:UIApplicationWillResignActiveNotification object:nil];
      [[NSNotificationCenter defaultCenter]addObserver:self selector:@selector(onForeground) name:UIApplicationDidBecomeActiveNotification object:nil];
      request = [[OffersRequest alloc]init];
    }
  }
  
  return self;
}

-(void)onForeground{
  [locationMgr startUpdatingLocation];
}

-(void)onBackground{
  [locationMgr stopUpdatingLocation];
}

-(void)startUpdating{
  [locationMgr startUpdatingLocation];
}

-(void)stopUpdating{
  [locationMgr stopUpdatingLocation];  
}

/*
 *  locationManager:didUpdateLocations:
 *
 *  Discussion:
 *    Invoked when new locations are available.  Required for delivery of
 *    deferred locations.  If implemented, updates will
 *    not be delivered to locationManager:didUpdateToLocation:fromLocation:
 *
 *    locations is an array of CLLocation objects in chronological order.
 */
- (void)locationManager:(CLLocationManager *)manager didUpdateLocations:(NSArray *)locations {
  
    self.currentLocation = [locations lastObject];
    NSDate* eventDate = self.currentLocation.timestamp;
    NSTimeInterval howRecent = [eventDate timeIntervalSinceNow];
    if (abs(howRecent) < 60.0) {
        //we don't need to get location again until next foreground
        [locationMgr stopUpdatingLocation];
        NSUserDefaults* prefs = [NSUserDefaults standardUserDefaults];
        if( [prefs objectForKey:KIKBAK_USER_ID] != nil ){
            NSMutableDictionary* data = [[NSMutableDictionary alloc]initWithCapacity:2];
        //      [data setObject:[NSNumber numberWithDouble:self.currentLocation.coordinate.latitude ] forKey:@"latitude"];
        //      [data setObject:[NSNumber numberWithDouble:self.currentLocation.coordinate.longitude ] forKey:@"longitude"];
            [data setObject:[NSNumber numberWithDouble:37.4207480 ] forKey:@"latitude"];
            [data setObject:[NSNumber numberWithDouble:-122.1303430 ] forKey:@"longitude"];
        //        [data setObject:[NSNumber numberWithDouble:37.77025720 ] forKey:@"latitude"];
        //        [data setObject:[NSNumber numberWithDouble:-122.40204120 ] forKey:@"longitude"];

            [request makeRequest:data];
        }    
    }

    [[NSNotificationCenter defaultCenter]postNotificationName:kKikbakLocationUpdate object:nil];

  NSString* distance = [Distance distanceToInMiles:[[CLLocation alloc]initWithLatitude:37.42706140 longitude:-122.1444120]];
  NSLog(@"Distance: %@", distance);
}

/*
 *  locationManager:didUpdateHeading:
 *
 *  Discussion:
 *    Invoked when a new heading is available.
 */
- (void)locationManager:(CLLocationManager *)manager didUpdateHeading:(CLHeading *)newHeading{
  
}


/*
 *  locationManagerShouldDisplayHeadingCalibration:
 *
 *  Discussion:
 *    Invoked when a new heading is available. Return YES to display heading calibration info. The display
 *    will remain until heading is calibrated, unless dismissed early via dismissHeadingCalibrationDisplay.
 */
- (BOOL)locationManagerShouldDisplayHeadingCalibration:(CLLocationManager *)manager{
  
  return NO;
}

/*
 *  locationManager:didEnterRegion:
 *
 *  Discussion:
 *    Invoked when the user enters a monitored region.  This callback will be invoked for every allocated
 *    CLLocationManager instance with a non-nil delegate that implements this method.
 */
- (void)locationManager:(CLLocationManager *)manager didEnterRegion:(CLRegion *)region{
  
}

/*
 *  locationManager:didExitRegion:
 *
 *  Discussion:
 *    Invoked when the user exits a monitored region.  This callback will be invoked for every allocated
 *    CLLocationManager instance with a non-nil delegate that implements this method.
 */
- (void)locationManager:(CLLocationManager *)manager didExitRegion:(CLRegion *)region{
  
}


/*
 *  locationManager:didFailWithError:
 *
 *  Discussion:
 *    Invoked when an error has occurred. Error types are defined in "CLError.h".
 */
- (void)locationManager:(CLLocationManager *)manager didFailWithError:(NSError *)error{
  
}

/*
 *  locationManager:monitoringDidFailForRegion:withError:
 *
 *  Discussion:
 *    Invoked when a region monitoring error has occurred. Error types are defined in "CLError.h".
 */
- (void)locationManager:(CLLocationManager *)manager monitoringDidFailForRegion:(CLRegion *)region withError:(NSError *)error {
  
}


/*
 *  locationManager:didChangeAuthorizationStatus:
 *
 *  Discussion:
 *    Invoked when the authorization status changes for this application.
 */
- (void)locationManager:(CLLocationManager *)manager didChangeAuthorizationStatus:(CLAuthorizationStatus)status{
  
}


/*
 *  locationManager:didStartMonitoringForRegion:
 *
 *  Discussion:
 *    Invoked when a monitoring for a region started successfully.
 */
- (void)locationManager:(CLLocationManager *)manager didStartMonitoringForRegion:(CLRegion *)region {
  
}


/*
 *  Discussion:
 *    Invoked when location updates are automatically paused.
 */
- (void)locationManagerDidPauseLocationUpdates:(CLLocationManager *)manager{
  
}

/*
 *  Discussion:
 *    Invoked when location updates are automatically resumed.
 *
 *    In the event that your application is terminated while suspended, you will
 *	  not receive this notification.
 */
- (void)locationManagerDidResumeLocationUpdates:(CLLocationManager *)manager{
  
}


/*
 *  locationManager:didFinishDeferredUpdatesWithError:
 *
 *  Discussion:
 *    Invoked when deferred updates will no longer be delivered. Stopping
 *    location, disallowing deferred updates, and meeting a specified criterion
 *    are all possible reasons for finishing deferred updates.
 *
 *    An error will be returned if deferred updates end before the specified
 *    criteria are met (see CLError).
 */
- (void)locationManager:(CLLocationManager *)manager didFinishDeferredUpdatesWithError:(NSError *)error {
  
}

@end
