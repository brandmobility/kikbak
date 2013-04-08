//
//  LocationParser.m
//  Kikbak
//
//  Created by Ian Barile on 4/6/13.
//  Copyright (c) 2013 Ian Barile. All rights reserved.
//

#import "LocationParser.h"
#import "Location.h"
#import "Offer.h"
#import "LocationLoader.h"

@implementation LocationParser

+(void)parse:(NSDictionary*)dict forOffer:(Offer*) offer{

    NSManagedObjectContext* context = offer.managedObjectContext;
    Location* location = [LocationLoader findById:[dict objectForKey:@"locationId"]];
    if(location == nil){
        location = [NSEntityDescription insertNewObjectForEntityForName:@"Location" inManagedObjectContext:context];
        [offer addLocationObject:location];
    }
    location.longitude = [dict objectForKey:@"longitude"];
    location.latitude = [dict objectForKey:@"latitude"];
    location.locationId = [dict objectForKey:@"locationId"];
}

@end
