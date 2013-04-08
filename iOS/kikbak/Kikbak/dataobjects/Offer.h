//
//  Offer.h
//  Kikbak
//
//  Created by Ian Barile on 4/7/13.
//  Copyright (c) 2013 Ian Barile. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <CoreData/CoreData.h>

@class Location;

@interface Offer : NSManagedObject

@property (nonatomic, retain) NSDate * beginDate;
@property (nonatomic, retain) NSString * defaultText;
@property (nonatomic, retain) NSString * desc;
@property (nonatomic, retain) NSDate * endDate;
@property (nonatomic, retain) NSString * giftDescription;
@property (nonatomic, retain) NSString * giftName;
@property (nonatomic, retain) NSString * giftNotificationText;
@property (nonatomic, retain) NSNumber * giftValue;
@property (nonatomic, retain) NSNumber * offerId;
@property (nonatomic, retain) NSString * kikbakDescription;
@property (nonatomic, retain) NSString * kikbakName;
@property (nonatomic, retain) NSString * kikbakNotificationText;
@property (nonatomic, retain) NSNumber * kikbakValue;
@property (nonatomic, retain) NSNumber * merchantId;
@property (nonatomic, retain) NSString * name;
@property (nonatomic, retain) NSSet *location;
@end

@interface Offer (CoreDataGeneratedAccessors)

- (void)addLocationObject:(Location *)value;
- (void)removeLocationObject:(Location *)value;
- (void)addLocation:(NSSet *)values;
- (void)removeLocation:(NSSet *)values;

@end
