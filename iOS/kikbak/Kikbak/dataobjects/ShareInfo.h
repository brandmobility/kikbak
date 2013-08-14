//
//  ShareInfo.h
//  Kikbak
//
//  Created by Ian Barile on 8/13/13.
//  Copyright (c) 2013 Ian Barile. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <CoreData/CoreData.h>

@class Gift;

@interface ShareInfo : NSManagedObject

@property (nonatomic, retain) NSNumber * allocatedGiftId;
@property (nonatomic, retain) NSNumber * friendUserId;
@property (nonatomic, retain) NSNumber * fbFriendId;
@property (nonatomic, retain) NSString * friendName;
@property (nonatomic, retain) NSString * imageUrl;
@property (nonatomic, retain) NSString * caption;
@property (nonatomic, retain) Gift *gift;

@end
