//
//  LocationParser.m
//  Kikbak
//
//  Created by Ian Barile on 4/6/13.
//  Copyright (c) 2013 Ian Barile. All rights reserved.
//

#import "LocationParser.h"
#import "Location.h"
#import "LocationService.h"


@implementation LocationParser


-(Location*)parse:(NSDictionary*)dict withContext:(NSManagedObjectContext*)context{
    
    Location* location = [LocationService findById:[dict objectForKey:@"locationId"]];
    if(location == nil){
        location = [NSEntityDescription insertNewObjectForEntityForName:@"Location" inManagedObjectContext:context];
    }
    
    location.longitude = [dict objectForKey:@"longitude"];
    location.latitude = [dict objectForKey:@"latitude"];
    location.locationId = [dict objectForKey:@"locationId"];
    location.phoneNumber = [dict objectForKey:@"phoneNumber"];
    location.address = [dict objectForKey:@"address1"];
    location.city = [dict objectForKey:@"city"];
    location.state = [dict objectForKey:@"state"];

    return location;
}

@end
