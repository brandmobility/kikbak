//
//  GiftParser.m
//  Kikbak
//
//  Created by Ian Barile on 4/11/13.
//  Copyright (c) 2013 Ian Barile. All rights reserved.
//

#import "GiftParser.h"
#import "AppDelegate.h"
#import "Gift.h"
#import "Location.h"
#import "GiftLoader.h"
#import "LocationParser.h"

@implementation GiftParser

+(void)parse:(NSDictionary*)dict{
    NSManagedObjectContext* context = ((AppDelegate*)[UIApplication sharedApplication].delegate).managedObjectContext;
    Gift* gift = [GiftLoader findGiftById:[dict objectForKey:@"id"]];
    if( gift == nil){
        gift = [NSEntityDescription insertNewObjectForEntityForName:@"Gift" inManagedObjectContext:context];
        gift.giftId = [dict objectForKey:@"id"];
    }
    
    id merchant = [dict objectForKey:@"merchant"];
    if( merchant != [NSNull null]){
        gift.merchantId = [merchant objectForKey:@"id"];
        gift.merchantName = [merchant objectForKey:@"name"];
    }
    gift.desc = [dict objectForKey:@"description"];
    gift.name = [dict objectForKey:@"name"];
    
    NSArray* locations = [dict objectForKey:@"locations"];
    for(id giftLocation in locations){
        [LocationParser parse:giftLocation forGift:gift];
    }
    
	NSError *error = nil;
	if (![context save:&error]) {
		NSLog(@"Unresolved error %@, %@", error, [error userInfo]);
	}

}

@end
