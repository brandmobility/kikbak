//
//  Gift.h
//  Kikbak
//
//  Created by Ian Barile on 9/4/13.
//  Copyright (c) 2013 Ian Barile. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <CoreData/CoreData.h>

@class Location, ShareInfo;

@interface Gift : NSManagedObject

@property (nonatomic, retain) NSString * defaultGiveImageUrl;
@property (nonatomic, retain) NSString * desc;
@property (nonatomic, retain) NSString * detailedDesc;
@property (nonatomic, retain) NSString * discountType;
@property (nonatomic, retain) NSNumber * merchantId;
@property (nonatomic, retain) NSString * merchantName;
@property (nonatomic, retain) NSString * merchantUrl;
@property (nonatomic, retain) NSString * name;
@property (nonatomic, retain) NSString * redemptionLocationType;
@property (nonatomic, retain) NSString * tosUrl;
@property (nonatomic, retain) NSString * validationType;
@property (nonatomic, retain) NSNumber * value;
@property (nonatomic, retain) NSNumber * offerId;
@property (nonatomic, retain) NSSet *location;
@property (nonatomic, retain) NSSet *shareInfo;
@end

@interface Gift (CoreDataGeneratedAccessors)

- (void)addLocationObject:(Location *)value;
- (void)removeLocationObject:(Location *)value;
- (void)addLocation:(NSSet *)values;
- (void)removeLocation:(NSSet *)values;

- (void)addShareInfoObject:(ShareInfo *)value;
- (void)removeShareInfoObject:(ShareInfo *)value;
- (void)addShareInfo:(NSSet *)values;
- (void)removeShareInfo:(NSSet *)values;

@end
