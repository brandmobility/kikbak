//
//  KikbakParser.m
//  Kikbak
//
//  Created by Ian Barile on 4/16/13.
//  Copyright (c) 2013 Ian Barile. All rights reserved.
//

#import "KikbakParser.h"
#import "AppDelegate.h"
#import "Location.h"
#import "LocationParser.h"
#import "Kikbak.h"
#import "KikbakLoader.h"

@implementation KikbakParser

+(void)parse:(NSDictionary*)dict{
    NSManagedObjectContext* context = ((AppDelegate*)[UIApplication sharedApplication].delegate).managedObjectContext;
    Kikbak* kikbak = [KikbakLoader findKikbaktById:[dict objectForKey:@"id"]];
    if( kikbak == nil){
        kikbak = [NSEntityDescription insertNewObjectForEntityForName:@"Kikbak" inManagedObjectContext:context];
        kikbak.kikbakId = [dict objectForKey:@"id"];
    }
    
    id merchant = [dict objectForKey:@"merchant"];
    if( merchant != [NSNull null]){
        kikbak.merchantId = [merchant objectForKey:@"id"];
        kikbak.merchantName = [merchant objectForKey:@"name"];
        
        NSArray* locations = [merchant objectForKey:@"locations"];
        for(id kikbakLocation in locations){
            Location* loc = [LocationParser parse:kikbakLocation withContext:kikbak.managedObjectContext];
            [kikbak addLocationObject:loc];
        }
    }
    kikbak.desc = [dict objectForKey:@"description"];
    kikbak.name = [dict objectForKey:@"name"];
    kikbak.value = [dict objectForKey:@"value"];
    
    
    NSError *error = nil;
    if (![context save:&error]) {
        NSLog(@"Unresolved error %@, %@", error, [error userInfo]);
    }
    
}


@end
