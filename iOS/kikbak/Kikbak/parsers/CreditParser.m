//
//  KikbakParser.m
//  Kikbak
//
//  Created by Ian Barile on 4/16/13.
//  Copyright (c) 2013 Ian Barile. All rights reserved.
//

#import "CreditParser.h"
#import "AppDelegate.h"
#import "Location.h"
#import "LocationParser.h"
#import "Credit.h"
#import "CreditService.h"

@interface CreditParser()

@property (nonatomic, strong) NSMutableDictionary* credits;

@end

@implementation CreditParser

-(void)parse:(NSDictionary*)dict{
    NSManagedObjectContext* context = ((AppDelegate*)[UIApplication sharedApplication].delegate).managedObjectContext;
    Credit* credit = [CreditService findKikbaktById:[dict objectForKey:@"id"]];
    if( credit == nil){
        credit = [NSEntityDescription insertNewObjectForEntityForName:@"Credit" inManagedObjectContext:context];
        credit.creditId = [dict objectForKey:@"id"];
    }
    
    
    id merchant = [dict objectForKey:@"merchant"];
    if( merchant != [NSNull null]){
        credit.merchantId = [merchant objectForKey:@"id"];
        credit.merchantName = [merchant objectForKey:@"name"];
        credit.merchantUrl = [merchant objectForKey:@"url"];
        
        NSArray* locations = [merchant objectForKey:@"locations"];
        for(id location in locations){
            Location* loc = [[[LocationParser alloc]init] parse:location withContext:credit.managedObjectContext];
            [credit addLocationObject:loc];
        }
    }
    credit.desc = [dict objectForKey:@"desc"];
    credit.detailedDesc = [dict objectForKey:@"detailedDesc"];
    credit.name = [dict objectForKey:@"name"];
    credit.value = [dict objectForKey:@"value"];
    credit.redeeemedGiftsCount = [dict objectForKey:@"redeemedGiftsCount"];
    credit.tosUrl = [dict objectForKey:@"tosUrl"];
    credit.validationType = [dict objectForKey:@"validationType"];
    credit.rewardType = [dict objectForKey:@"rewardType"];
    credit.imageUrl = [dict objectForKey:@"imageUrl"];
    
    
    NSError *error = nil;
    if (![context save:&error]) {
        NSLog(@"Unresolved error %@, %@", error, [error userInfo]);
    }


    if(self.credits == nil){
        self.credits = [[NSMutableDictionary alloc]initWithCapacity:1];
    }
    [self.credits setObject:credit forKey:credit.creditId];
}


-(void)resolveDiff{
    NSArray* credits = [CreditService getCredits];
    NSMutableDictionary* creditDict = [[NSMutableDictionary alloc] initWithCapacity:[credits count]];
    for(Credit* credit in credits){
        [creditDict setObject:credit forKey:credit.creditId];
    }
    
    for(NSNumber* key in [self.credits allKeys]){
        [creditDict removeObjectForKey:key];
    }
    
    
    NSManagedObjectContext* context = ((AppDelegate*)[UIApplication sharedApplication].delegate).managedObjectContext;
    for(Credit* credit in [creditDict allValues]){
        [context deleteObject:credit];
    }
    
    NSError* error = nil;
    if([[context deletedObjects]count] > 0 && ![context save:&error]){
       NSLog(@"Unresolved error %@, %@", error, [error userInfo]);
    }
}

@end
