//
//  OfferParser.m
//  Kikbak
//
//  Created by Ian Barile on 4/6/13.
//  Copyright (c) 2013 Ian Barile. All rights reserved.
//

#import "OfferParser.h"
#import "AppDelegate.h"
#import "Offer.h"
#import "LocationParser.h"
#import "OfferLoader.h"

@implementation OfferParser

+(void)parse:(NSDictionary*)dict{
    NSManagedObjectContext* context = ((AppDelegate*)[UIApplication sharedApplication].delegate).managedObjectContext;
    Offer* offer = [OfferLoader findOfferById:[dict objectForKey:@"id"]];
    if( offer == nil){
        offer = [NSEntityDescription insertNewObjectForEntityForName:@"Offer" inManagedObjectContext:context];
        offer.offerId = [dict objectForKey:@"id"];
    }
    offer.name = [dict objectForKey:@"name"];
    offer.desc = [dict objectForKey:@"description"];
    offer.defaultText = [dict objectForKey:@"defaultText"];
//    offer.giftName = [dict objectForKey:@"giftName"];
//    offer.giftDescription = [dict objectForKey:@"giftDescription"];
//    offer.giftNotificationText = [dict objectForKey:@"giftNotificationText"];
    offer.giftValue = [dict objectForKey:@"giftValue"];
//    offer.kikbakName = [dict objectForKey:@"kikbakName"];
//    offer.kikbakDescription = [dict objectForKey:@"kikbakDescription"];
    offer.kikbakValue = [dict objectForKey:@"kikbakValue"];
 //   offer.kikbakNotificationText = [dict objectForKey:@"kikbakNotificationText"];
    long date = [dict objectForKey:@"beginDate"];
    NSTimeInterval timeSince70 = date;
    offer.beginDate = [NSDate dateWithTimeIntervalSince1970:timeSince70];
    date = [dict objectForKey:@"endDate"];
    timeSince70 = date;
    offer.endDate = [NSDate dateWithTimeIntervalSince1970:timeSince70];
    
    NSArray* locations = [dict objectForKey:@"locations"];
    for(id offerLocation in locations){
        [LocationParser parse:offerLocation forOffer:offer];
    }
    
	NSError *error = nil;
	if (![context save:&error]) {
		NSLog(@"Unresolved error %@, %@", error, [error userInfo]);
		abort();
	}
    
}

@end
