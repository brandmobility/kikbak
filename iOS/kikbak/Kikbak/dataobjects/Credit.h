//
//  Credit.h
//  Kikbak
//
//  Created by Ian Barile on 9/6/13.
//  Copyright (c) 2013 Ian Barile. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <CoreData/CoreData.h>

@class Location;

@interface Credit : NSManagedObject

@property (nonatomic, retain) NSNumber * creditId;
@property (nonatomic, retain) NSString * desc;
@property (nonatomic, retain) NSString * detailedDesc;
@property (nonatomic, retain) NSString * imageUrl;
@property (nonatomic, retain) NSNumber * merchantId;
@property (nonatomic, retain) NSString * merchantName;
@property (nonatomic, retain) NSString * merchantUrl;
@property (nonatomic, retain) NSString * name;
@property (nonatomic, retain) NSNumber * redeeemedGiftsCount;
@property (nonatomic, retain) NSString * rewardType;
@property (nonatomic, retain) NSString * tosUrl;
@property (nonatomic, retain) NSString * validationType;
@property (nonatomic, retain) NSNumber * value;
@property (nonatomic, retain) NSNumber * offerId;
@property (nonatomic, retain) NSSet *location;
@end

@interface Credit (CoreDataGeneratedAccessors)

- (void)addLocationObject:(Location *)value;
- (void)removeLocationObject:(Location *)value;
- (void)addLocation:(NSSet *)values;
- (void)removeLocation:(NSSet *)values;

@end
