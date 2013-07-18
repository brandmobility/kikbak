//
//  ShareChannelSelectorView.h
//  Kikbak
//
//  Created by Ian Barile on 7/14/13.
//  Copyright (c) 2013 Ian Barile. All rights reserved.
//

#import <UIKit/UIKit.h>

@protocol ChannelSelectorDelegate <NSObject>

-(void)onEmailSelected;
-(void)onTimelineSelected;

@end

@interface ShareChannelSelectorView : UIView

@property (nonatomic,strong) id<ChannelSelectorDelegate> delegate;

-(void)createsubviews;

@end
