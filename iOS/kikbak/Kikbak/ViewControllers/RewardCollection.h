//
//  RewardCollection.h
//  Kikbak
//
//  Created by Ian Barile on 4/17/13.
//  Copyright (c) 2013 Ian Barile. All rights reserved.
//

#import <Foundation/Foundation.h>

@class Gift;
@class Credit;

@interface RewardCollection : NSObject

@property (nonatomic, strong) Gift* gift;
@property (nonatomic, strong) Credit* credit;

@end
