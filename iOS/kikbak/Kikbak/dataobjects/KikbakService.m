//
//  KikbakLoader.m
//  Kikbak
//
//  Created by Ian Barile on 4/17/13.
//  Copyright (c) 2013 Ian Barile. All rights reserved.
//

#import "KikbakService.h"
#import "AppDelegate.h"
#import "Kikbak.h"

@implementation KikbakService



+(Kikbak*)findKikbaktById:(NSNumber*)kikbakId{
    NSManagedObjectContext* context = ((AppDelegate*)[UIApplication sharedApplication].delegate).managedObjectContext;
    
    NSEntityDescription *entityDescription = [NSEntityDescription entityForName:@"Kikbak" inManagedObjectContext:context];
    NSFetchRequest *request = [[NSFetchRequest alloc] init];
    [request setEntity:entityDescription];
    
    // Set example predicate and sort orderings...
    NSPredicate *predicate = [NSPredicate predicateWithFormat: @"(kikbakId = %@)", kikbakId];
    [request setPredicate:predicate];
    
    NSSortDescriptor *sortDescriptor = [[NSSortDescriptor alloc]initWithKey:@"kikbakId" ascending:YES];
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

+(NSArray*)getKikbaks{
    NSManagedObjectContext* context = ((AppDelegate*)[UIApplication sharedApplication].delegate).managedObjectContext;
    
    NSEntityDescription *entityDescription = [NSEntityDescription entityForName:@"Kikbak" inManagedObjectContext:context];
    NSFetchRequest *request = [[NSFetchRequest alloc] init];
    [request setEntity:entityDescription];
    
    NSSortDescriptor *sortDescriptor = [[NSSortDescriptor alloc]initWithKey:@"kikbakId" ascending:YES];
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
    NSArray* kikbaks = [self getKikbaks];
    for(id kikbak in kikbaks){
        [context deleteObject:kikbak];
    }
    
    NSError* error = nil;
    if(![context save:&error]){
        NSLog(@"Unresolved error %@, %@", error, [error userInfo]);
    }
}

@end
