//
//  OfferLoader.m
//  Kikbak
//
//  Created by Ian Barile on 4/7/13.
//  Copyright (c) 2013 Ian Barile. All rights reserved.
//

#import "OfferService.h"
#import "AppDelegate.h"

@implementation OfferService

+(Offer*)findOfferById:(NSNumber*)offerId{
    NSManagedObjectContext* context = ((AppDelegate*)[UIApplication sharedApplication].delegate).managedObjectContext;

    NSEntityDescription *entityDescription = [NSEntityDescription entityForName:@"Offer" inManagedObjectContext:context];
    NSFetchRequest *request = [[NSFetchRequest alloc] init];
    [request setEntity:entityDescription];
    
    // Set example predicate and sort orderings...
    NSPredicate *predicate = [NSPredicate predicateWithFormat: @"(offerId = %@)", offerId];
    [request setPredicate:predicate];
    
    NSSortDescriptor *sortDescriptor = [[NSSortDescriptor alloc]initWithKey:@"offerId" ascending:YES];
    [request setSortDescriptors:@[sortDescriptor]];
    
    NSError *error;
    NSArray *array = [context executeFetchRequest:request error:&error];
    if (array == nil)
    {
		NSLog(@"Unresolved error %@, %@", error, [error userInfo]);
		return nil;
    }
    
    if( [array count] ){
        return [array objectAtIndex:0];
    }
    else{
        return nil;
    }
}

+(NSArray*)getOffers{
    NSManagedObjectContext* context = ((AppDelegate*)[UIApplication sharedApplication].delegate).managedObjectContext;
    
    NSEntityDescription *entityDescription = [NSEntityDescription entityForName:@"Offer" inManagedObjectContext:context];
    NSFetchRequest *request = [[NSFetchRequest alloc] init];
    [request setEntity:entityDescription];
    
    NSSortDescriptor *sortDescriptor = [[NSSortDescriptor alloc]initWithKey:@"offerId" ascending:YES];
    [request setSortDescriptors:@[sortDescriptor]];
    
    NSError *error;
    NSArray *array = [context executeFetchRequest:request error:&error];
    if (array == nil)
    {
		NSLog(@"Unresolved error %@, %@", error, [error userInfo]);
		return nil;
    }
    
    return array;
}

+(void)deleteAll{
    NSManagedObjectContext* context = ((AppDelegate*)[UIApplication sharedApplication].delegate).managedObjectContext;
    NSArray* offers = [OfferService getOffers];
    for(id offer in offers){
        [context deleteObject:offer];
    }
    
    NSError* error = nil;
    if(![context save:&error]){
        NSLog(@"Unresolved error %@, %@", error, [error userInfo]);
    }
}

@end
