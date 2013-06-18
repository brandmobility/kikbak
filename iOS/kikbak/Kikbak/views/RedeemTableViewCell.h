//
//  ReedemTableViewCell.h
//  Kikbak
//
//  Created by Ian Barile on 4/12/13.
//  Copyright (c) 2013 Ian Barile. All rights reserved.
//

#import <Foundation/Foundation.h>

@class RewardCollection;

@interface RedeemTableViewCell : UITableViewCell

@property(strong, nonatomic)RewardCollection* rewards;
@property (nonatomic,strong) UILabel* giftValue;
@property (nonatomic,strong) UILabel* creditValue;

-(void)setup;//:(int)index;

@end
