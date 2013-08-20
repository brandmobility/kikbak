//
//  FriendSelectorTableViewCell.h
//  Kikbak
//
//  Created by Ian Barile on 8/15/13.
//  Copyright (c) 2013 Ian Barile. All rights reserved.
//

#import <UIKit/UIKit.h>

@class ShareInfo;

@interface FriendSelectorTableViewCell : UITableViewCell

@property(nonatomic,strong) NSNumber* fbFriendId;
@property(nonatomic,strong) NSString* name;
@property(nonatomic,strong) ShareInfo* shareInfo;

-(void)createSubviews;

@end
