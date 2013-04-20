//
//  LocationParser.m
//  Kikbak
//
//  Created by Ian Barile on 4/6/13.
//  Copyright (c) 2013 Ian Barile. All rights reserved.
//

#import "LocationParser.h"
#import "Location.h"
#import "LocationLoader.h"


@implementation LocationParser

+(Location*)parse:(NSDictionary*)dict withContext:(NSManagedObjectContext*)context{
    
    Location* location = [LocationLoader findById:[dict objectForKey:@"locationId"]];
    if(location == nil){
        location = [NSEntityDescription insertNewObjectForEntityForName:@"Location" inManagedObjectContext:context];
    }
    
    location.longitude = [dict objectForKey:@"longitude"];
    location.latitude = [dict objectForKey:@"latitude"];
    location.locationId = [dict objectForKey:@"locationId"];

    return location;
}

@end