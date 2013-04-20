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
#import "ImageRequest.h"

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
    offer.giftDescription = [dict objectForKey:@"giftDescription"];
    offer.kikbakDescription = [dict objectForKey:@"kikbakDescription"];
    offer.merchantImageUrl = [dict objectForKey:@"merchantImageUrl"];
    offer.merchantId = [dict objectForKey:@"merchantId"];
    offer.merchantName = [dict objectForKey:@"merchantName"];
    long date = [dict objectForKey:@"beginDate"];
    NSTimeInterval timeSince70 = date;
    offer.beginDate = [NSDate dateWithTimeIntervalSince1970:timeSince70];
    date = [dict objectForKey:@"endDate"];
    timeSince70 = date;
    offer.endDate = [NSDate dateWithTimeIntervalSince1970:timeSince70];
    
    NSArray* locations = [dict objectForKey:@"locations"];
    for(id offerLocation in locations){
        Location* loc = [LocationParser parse:offerLocation withContext:offer.managedObjectContext];
        [offer addLocationObject:loc];
    }
    
	NSError *error = nil;
	if (![context save:&error]) {
		NSLog(@"Unresolved error %@, %@", error, [error userInfo]);
	}

    ImageRequest* request = [[ImageRequest alloc]init];
    [request requestMerchangeImage:offer.merchantImageUrl forMerchantId:offer.merchantId];
}

@end
