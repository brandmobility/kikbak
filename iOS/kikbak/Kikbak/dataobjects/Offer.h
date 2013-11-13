//
//  Offer.h
//  Kikbak
//
//  Created by Ian Barile on 11/11/13.
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
@property (nonatomic, retain) NSString * giftDescriptionOptional;
@property (nonatomic, retain) NSString * giftDiscountType;
@property (nonatomic, retain) NSNumber * giftValue;
@property (nonatomic, retain) NSString * giveImageUrl;
@property (nonatomic, retain) NSString * kikbakDescription;
@property (nonatomic, retain) NSString * kikbakDescriptionOptional;
@property (nonatomic, retain) NSNumber * kikbakValue;
@property (nonatomic, retain) NSNumber * merchantId;
@property (nonatomic, retain) NSString * merchantName;
@property (nonatomic, retain) NSString * merchantUrl;
@property (nonatomic, retain) NSString * name;
@property (nonatomic, retain) NSNumber * offerId;
@property (nonatomic, retain) NSString * offerImageUrl;
@property (nonatomic, retain) NSString * offerType;
@property (nonatomic, retain) NSString * termsOfService;
@property (nonatomic, retain) NSNumber * hasEmployeeProgram;
@property (nonatomic, retain) NSSet *location;
@end

@interface Offer (CoreDataGeneratedAccessors)

- (void)addLocationObject:(Location *)value;
- (void)removeLocationObject:(Location *)value;
- (void)addLocation:(NSSet *)values;
- (void)removeLocation:(NSSet *)values;

@end
