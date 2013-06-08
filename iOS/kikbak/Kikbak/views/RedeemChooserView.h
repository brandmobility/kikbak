//
//  RedeemChooserView.h
//  Kikbak
//
//  Created by Ian Barile on 5/22/13.
//  Copyright (c) 2013 Ian Barile. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "RedeemTypeProtocol.h"

@class RewardCollection;

@interface RedeemChooserView : UIView

@property (nonatomic,strong) id<RedeemTypeProtocol> delegate;
@property (nonatomic,strong) RewardCollection* collection;

-(void)manuallyLayoutSubviews;

@end
