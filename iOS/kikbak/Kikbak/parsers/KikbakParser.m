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
#import "KikbakService.h"

@interface KikbakParser()

@property (nonatomic, strong) NSMutableDictionary* kikbaks;

@end

@implementation KikbakParser

-(void)parse:(NSDictionary*)dict{
    NSManagedObjectContext* context = ((AppDelegate*)[UIApplication sharedApplication].delegate).managedObjectContext;
    Kikbak* kikbak = [KikbakService findKikbaktById:[dict objectForKey:@"id"]];
    if( kikbak == nil){
        kikbak = [NSEntityDescription insertNewObjectForEntityForName:@"Kikbak" inManagedObjectContext:context];
        kikbak.kikbakId = [dict objectForKey:@"id"];
    }
    
    
    id merchant = [dict objectForKey:@"merchant"];
    if( merchant != [NSNull null]){
        kikbak.merchantId = [merchant objectForKey:@"id"];
        kikbak.merchantName = [merchant objectForKey:@"name"];
        kikbak.merchantUrl = [merchant objectForKey:@"merchantUrl"];
        
        NSArray* locations = [merchant objectForKey:@"locations"];
        for(id kikbakLocation in locations){
            Location* loc = [[[LocationParser alloc]init] parse:kikbakLocation withContext:kikbak.managedObjectContext];
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


    if(self.kikbaks == nil){
        self.kikbaks = [[NSMutableDictionary alloc]initWithCapacity:1];
    }
    [self.kikbaks setObject:kikbak forKey:kikbak.kikbakId];
}


-(void)resolveDiff{
    NSArray* pKikbaks = [KikbakService getKikbaks];
    NSMutableDictionary* pKikbakDict = [[NSMutableDictionary alloc] initWithCapacity:[pKikbaks count]];
    for(Kikbak* kikbak in pKikbaks){
        [pKikbakDict setObject:kikbak forKey:kikbak.kikbakId];
    }
    
    for(NSNumber* key in [self.kikbaks allKeys]){
        [pKikbakDict removeObjectForKey:key];
    }
    
    
    NSManagedObjectContext* context = ((AppDelegate*)[UIApplication sharedApplication].delegate).managedObjectContext;
    for(Kikbak* kikbak in [pKikbakDict allValues]){
        [context deleteObject:kikbak];
    }
    
    NSError* error = nil;
    if([[context deletedObjects]count] > 0 && ![context save:&error]){
       NSLog(@"Unresolved error %@, %@", error, [error userInfo]);
    }
}

@end
