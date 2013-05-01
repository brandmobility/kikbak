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
#import "GiftService.h"
#import "LocationParser.h"
#import "FBQuery.h"

@interface GiftParser()

@property (nonatomic, strong) NSMutableDictionary* gifts;

@end

@implementation GiftParser

-(void)parse:(NSDictionary*)dict{
    NSManagedObjectContext* context = ((AppDelegate*)[UIApplication sharedApplication].delegate).managedObjectContext;
    Gift* gift = [GiftService findGiftById:[dict objectForKey:@"id"]];
    if( gift == nil){
        gift = [NSEntityDescription insertNewObjectForEntityForName:@"Gift" inManagedObjectContext:context];
        gift.giftId = [dict objectForKey:@"id"];
    
        
        id merchant = [dict objectForKey:@"merchant"];
        if( merchant != [NSNull null]){
            gift.merchantId = [merchant objectForKey:@"id"];
            gift.merchantName = [merchant objectForKey:@"name"];
            
            NSArray* locations = [merchant objectForKey:@"locations"];
            for(id giftLocation in locations){
                Location* loc = [[[LocationParser alloc]init] parse:giftLocation withContext:gift.managedObjectContext];
                [gift addLocationObject:loc];
            }
        }
        gift.desc = [dict objectForKey:@"description"];
        gift.name = [dict objectForKey:@"name"];
        gift.friendUserId = [dict objectForKey:@"friendUserId"];
        gift.fbImageId = [dict objectForKey:@"fbImageId"];
        
        NSError *error = nil;
        if (![context save:&error]) {
            NSLog(@"Unresolved error %@, %@", error, [error userInfo]);
        }
    }
    
    if( self.gifts == nil){
        self.gifts = [[NSMutableDictionary alloc]initWithCapacity:1];
    }
    [self.gifts setObject:gift forKey:gift.giftId];
    
    [FBQuery resolveImageUrl:gift.fbImageId];
}


-(void)resolveDiff{
    NSArray* pGifts = [GiftService getGifts];
    NSMutableDictionary* pGiftDict = [[NSMutableDictionary alloc] initWithCapacity:[pGifts count]];
    for(Gift* gift in pGifts){
        [pGiftDict setObject:gift forKey:gift.giftId];
    }
    
    for(NSNumber* key in [self.gifts allKeys]){
        [pGiftDict removeObjectForKey:key];
    }
    
    
    NSManagedObjectContext* context = ((AppDelegate*)[UIApplication sharedApplication].delegate).managedObjectContext;
    for(Gift* gift in [pGiftDict allValues]){
        [context deleteObject:gift];
    }
    
    NSError* error = nil;
    if([[context deletedObjects]count] > 0 && ![context save:&error]){
        NSLog(@"Unresolved error %@, %@", error, [error userInfo]);
    }
}

@end
