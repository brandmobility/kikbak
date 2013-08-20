//
//  ShareChannelSelectorView.h
//  Kikbak
//
//  Created by Ian Barile on 7/14/13.
//  Copyright (c) 2013 Ian Barile. All rights reserved.
//

#import <UIKit/UIKit.h>

@protocol ChannelSelectorDelegate <NSObject>

-(void)onEmailSelected:(NSNumber*)locationId withEmployeeName:(NSString*)name;
-(void)onSmsSelected:(NSNumber*)locationId withEmployeeName:(NSString*)name;
-(void)onTimelineSelected:(NSNumber*)locationId withEmployeeName:(NSString*)name;

@end

@class Location;

@interface ShareChannelSelectorView : UIView<UITableViewDataSource,
                                            UITableViewDelegate,
                                            UITextViewDelegate>

@property (nonatomic,strong) id<ChannelSelectorDelegate> delegate;
@property (nonatomic,strong) NSSet* locations;

-(void)createsubviews;

@end
