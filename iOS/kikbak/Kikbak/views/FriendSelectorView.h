//
//  FriendSelectorView.h
//  Kikbak
//
//  Created by Ian Barile on 8/15/13.
//  Copyright (c) 2013 Ian Barile. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "RedeemGiftFriendChooserProtocol.h"

@class Gift;

@interface FriendSelectorView : UIView<UITableViewDataSource, UITableViewDelegate>

@property(nonatomic,strong) Gift* gift;
@property(nonatomic,strong) id<RedeemGiftFriendChooserProtocol> delegate;

-(void)createSubviews;

@end
