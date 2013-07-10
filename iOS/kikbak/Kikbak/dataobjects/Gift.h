//
//  Gift.h
//  Kikbak
//
//  Created by Ian Barile on 7/10/13.
//  Copyright (c) 2013 Ian Barile. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <CoreData/CoreData.h>

@class Location;

@interface Gift : NSManagedObject

@property (nonatomic, retain) NSString * caption;
@property (nonatomic, retain) NSString * desc;
@property (nonatomic, retain) NSString * detailedDesc;
@property (nonatomic, retain) NSNumber * fbFriendId;
@property (nonatomic, retain) NSNumber * fbImageId;
@property (nonatomic, retain) NSString * friendName;
@property (nonatomic, retain) NSNumber * friendUserId;
@property (nonatomic, retain) NSNumber * giftId;
@property (nonatomic, retain) NSNumber * merchantId;
@property (nonatomic, retain) NSString * merchantName;
@property (nonatomic, retain) NSString * merchantUrl;
@property (nonatomic, retain) NSString * name;
@property (nonatomic, retain) NSString * type;
@property (nonatomic, retain) NSNumber * value;
@property (nonatomic, retain) NSSet *location;
@end

@interface Gift (CoreDataGeneratedAccessors)

- (void)addLocationObject:(Location *)value;
- (void)removeLocationObject:(Location *)value;
- (void)addLocation:(NSSet *)values;
- (void)removeLocation:(NSSet *)values;

@end
