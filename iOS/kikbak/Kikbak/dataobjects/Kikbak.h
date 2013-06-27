//
//  Kikbak.h
//  Kikbak
//
//  Created by Ian Barile on 6/26/13.
//  Copyright (c) 2013 Ian Barile. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <CoreData/CoreData.h>

@class Location;

@interface Kikbak : NSManagedObject

@property (nonatomic, retain) NSString * desc;
@property (nonatomic, retain) NSString * imageUrl;
@property (nonatomic, retain) NSNumber * kikbakId;
@property (nonatomic, retain) NSNumber * merchantId;
@property (nonatomic, retain) NSString * merchantName;
@property (nonatomic, retain) NSString * merchantUrl;
@property (nonatomic, retain) NSString * name;
@property (nonatomic, retain) NSNumber * redeeemedGiftsCount;
@property (nonatomic, retain) NSNumber * value;
@property (nonatomic, retain) NSString * descOptional;
@property (nonatomic, retain) NSSet *location;
@end

@interface Kikbak (CoreDataGeneratedAccessors)

- (void)addLocationObject:(Location *)value;
- (void)removeLocationObject:(Location *)value;
- (void)addLocation:(NSSet *)values;
- (void)removeLocation:(NSSet *)values;

@end
