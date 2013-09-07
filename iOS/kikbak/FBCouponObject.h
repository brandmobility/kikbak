//
//  FBObject.h
//  Kikbak
//
//  Created by Ian Barile on 8/6/13.
//  Copyright (c) 2013 Ian Barile. All rights reserved.
//

#import <Foundation/Foundation.h>

@class Location;

@interface FBCouponObject : NSObject

@property (nonatomic,strong) NSString* caption;
@property (nonatomic,strong) NSString* merchant;
@property (nonatomic,strong) NSString* gift;
@property (nonatomic,strong) NSString* detailedDescription;
@property (nonatomic,strong) NSNumber* locationId;
@property (nonatomic,strong) NSString* employeeName;
@property (nonatomic,strong) NSString* landingUrl;

-(void)postCoupon:(NSString*)url;

@end
