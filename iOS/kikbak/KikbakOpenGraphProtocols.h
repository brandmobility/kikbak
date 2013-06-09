//
//  KikbakOGCoupon.h
//  Kikbak
//
//  Created by Ian Barile on 5/20/13.
//  Copyright (c) 2013 Ian Barile. All rights reserved.
//

#import <Foundation/Foundation.h>

@protocol KikbakOGCoupon <NSObject>

@property (strong, nonatomic) NSString *id;
@property (strong, nonatomic) NSString *url;

@end

@protocol KikbakOGShareCouponAction<FBOpenGraphAction>

@property (strong, nonatomic) id<KikbakOGCoupon> coupon;

@end