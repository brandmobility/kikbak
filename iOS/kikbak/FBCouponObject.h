//
//  FBObject.h
//  Kikbak
//
//  Created by Ian Barile on 8/6/13.
//  Copyright (c) 2013 Ian Barile. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface FBCouponObject : NSObject

@property (nonatomic,strong)NSString* caption;
@property (nonatomic,strong)NSString* merchant;
@property (nonatomic,strong)NSString* gift;
@property (nonatomic,strong)NSString* detailedDescription;

-(void)postCoupon:(NSString*)url;

@end
