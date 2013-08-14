//
//  GiftLoader.m
//  Kikbak
//
//  Created by Ian Barile on 4/11/13.
//  Copyright (c) 2013 Ian Barile. All rights reserved.
//

#import "GiftService.h"
#import "AppDelegate.h"
#import "Gift.h"


@implementation GiftService

+(Gift*)findGiftByMerchantId:(NSNumber*)merchantId{
    NSManagedObjectContext* context = ((AppDelegate*)[UIApplication sharedApplication].delegate).managedObjectContext;
    
    NSEntityDescription *entityDescription = [NSEntityDescription entityForName:@"Gift" inManagedObjectContext:context];
    NSFetchRequest *request = [[NSFetchRequest alloc] init];
    [request setEntity:entityDescription];
    
    // Set example predicate and sort orderings...
    NSPredicate *predicate = [NSPredicate predicateWithFormat: @"(merchantId = %@)", merchantId];
    [request setPredicate:predicate];
    
    NSSortDescriptor *sortDescriptor = [[NSSortDescriptor alloc]initWithKey:@"merchantId" ascending:YES];
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

+(NSArray*)getGifts{
    NSManagedObjectContext* context = ((AppDelegate*)[UIApplication sharedApplication].delegate).managedObjectContext;
    
    NSEntityDescription *entityDescription = [NSEntityDescription entityForName:@"Gift" inManagedObjectContext:context];
    NSFetchRequest *request = [[NSFetchRequest alloc] init];
    [request setEntity:entityDescription];
    
    NSSortDescriptor *sortDescriptor = [[NSSortDescriptor alloc]initWithKey:@"giftId" ascending:YES];
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
    NSArray* gifts = [self getGifts];
    for(id gift in gifts){
        [context deleteObject:gift];
    }
    
    NSError* error = nil;
    if(![context save:&error]){
        NSLog(@"Unresolved error %@, %@", error, [error userInfo]);
    }
}

@end
