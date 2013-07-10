//
//  KikbakLoader.m
//  Kikbak
//
//  Created by Ian Barile on 4/17/13.
//  Copyright (c) 2013 Ian Barile. All rights reserved.
//

#import "CreditService.h"
#import "AppDelegate.h"
#import "Credit.h"

@implementation CreditService



+(Credit*)findKikbaktById:(NSNumber*)kikbakId{
    NSManagedObjectContext* context = ((AppDelegate*)[UIApplication sharedApplication].delegate).managedObjectContext;
    
    NSEntityDescription *entityDescription = [NSEntityDescription entityForName:@"Credit" inManagedObjectContext:context];
    NSFetchRequest *request = [[NSFetchRequest alloc] init];
    [request setEntity:entityDescription];
    
    // Set example predicate and sort orderings...
    NSPredicate *predicate = [NSPredicate predicateWithFormat: @"(creditId = %@)", kikbakId];
    [request setPredicate:predicate];
    
    NSSortDescriptor *sortDescriptor = [[NSSortDescriptor alloc]initWithKey:@"creditId" ascending:YES];
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

+(NSArray*)getCredits{
    NSManagedObjectContext* context = ((AppDelegate*)[UIApplication sharedApplication].delegate).managedObjectContext;
    
    NSEntityDescription *entityDescription = [NSEntityDescription entityForName:@"Credit" inManagedObjectContext:context];
    NSFetchRequest *request = [[NSFetchRequest alloc] init];
    [request setEntity:entityDescription];
    
    NSSortDescriptor *sortDescriptor = [[NSSortDescriptor alloc]initWithKey:@"creditId" ascending:YES];
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
    NSArray* credits = [self getCredits];
    for(id credit in credits){
        [context deleteObject:credit];
    }
    
    NSError* error = nil;
    if(![context save:&error]){
        NSLog(@"Unresolved error %@, %@", error, [error userInfo]);
    }
}

@end
