//
//  Location.h
//  Kikbak
//
//  Created by Ian Barile on 4/16/13.
//  Copyright (c) 2013 Ian Barile. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <CoreData/CoreData.h>

@class Gift, Kikbak, Offer;

@interface Location : NSManagedObject

@property (nonatomic, retain) NSNumber * latitude;
@property (nonatomic, retain) NSNumber * locationId;
@property (nonatomic, retain) NSNumber * longitude;
@property (nonatomic, retain) Gift *gift;
@property (nonatomic, retain) Offer *offer;
@property (nonatomic, retain) Kikbak *kikbak;

@end