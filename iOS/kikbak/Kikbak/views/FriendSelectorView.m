//
//  FriendSelectorView.m
//  Kikbak
//
//  Created by Ian Barile on 8/15/13.
//  Copyright (c) 2013 Ian Barile. All rights reserved.
//

#import "FriendSelectorView.h"
#import "FriendSelectorTableViewCell.h"
#import "util.h"
#import "Gift.h"
#import <QuartzCore/QuartzCore.h>
#import "ShareInfo.h"

@interface FriendSelectorView()


@property (nonatomic,strong) UIView* backgroundView;
@property (nonatomic,strong) UILabel* ammount;
@property (nonatomic,strong) UILabel* select;
@property (nonatomic,strong) UITableView* table;
@property (nonatomic,strong) UIButton* closeBtn;

-(IBAction)onCloseBtn:(id)sender;

@end

@implementation FriendSelectorView

- (id)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        // Initialization code
        self.backgroundColor = [UIColor colorWithRed:0 green:0 blue:0 alpha:.8];
    }
    return self;
}

-(void)createSubviews{
    self.backgroundView = [[UIView alloc] initWithFrame:CGRectMake(23, 60, self.frame.size.width - 46, 356)];
    self.backgroundView.backgroundColor = UIColorFromRGB(0xE0E0E0);
    self.backgroundView.layer.cornerRadius = 10;
    [self addSubview:self.backgroundView];
    
    self.ammount = [[UILabel alloc]initWithFrame:CGRectMake(0, 15, self.backgroundView.frame.size.width, 24)];
    self.ammount.font = [UIFont fontWithName:@"HelveticaNeue-Bold" size:21];
    self.ammount.textAlignment = NSTextAlignmentCenter;
    self.ammount.backgroundColor = [UIColor clearColor];
    self.ammount.textColor = UIColorFromRGB(0x3a3a3a);
    [self.backgroundView addSubview:self.ammount];
    if ([self.gift.discountType compare:@"percentage"] == NSOrderedSame) {
        self.ammount.text = [NSString stringWithFormat:NSLocalizedString(@"gift percent", nil), [self.gift.value integerValue]];
    }
    else{
        self.ammount.text = [NSString stringWithFormat:NSLocalizedString(@"amount off", nil), [self.gift.value integerValue]];
    }
    
    self.select = [[UILabel alloc]initWithFrame:CGRectMake(39, 40, self.backgroundView.frame.size.width - 78, 40)];
    self.select.font = [UIFont fontWithName:@"HelveticaNeue" size:14];
    self.select.text = NSLocalizedString(@"Select friend", nil);
    self.select.textAlignment = NSTextAlignmentCenter;
    self.select.backgroundColor = [UIColor clearColor];
    self.select.numberOfLines = 2;
    self.select.textColor = UIColorFromRGB(0x3a3a3a);
    [self.backgroundView addSubview:self.select];
    
    self.table = [[UITableView alloc]initWithFrame:CGRectMake(0, 80, self.backgroundView.frame.size.width, 256) style:UITableViewStyleGrouped];
    self.table.dataSource = self;
    self.table.delegate = self;
    self.table.separatorStyle = UITableViewCellSeparatorStyleSingleLine;
    self.table.separatorColor = UIColorFromRGB(0xB9B9B9);
    self.table.backgroundColor = UIColorFromRGB(0xE0E0E0);
    self.table.backgroundView = nil;
    [self.backgroundView addSubview:self.table];
    
    
    self.closeBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    [self.closeBtn addTarget:self action:@selector(onCloseBtn:) forControlEvents:UIControlEventTouchUpInside];
    [self.closeBtn setImage:[UIImage imageNamed:@"btn_cancel"] forState:UIControlStateNormal];
    self.closeBtn.frame = CGRectMake(8, self.backgroundView.frame.origin.y-15, 35, 35);
    [self addSubview:self.closeBtn];
}




#pragma mark - table datasource methods
- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath{
    return 48;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath{
    
    FriendSelectorTableViewCell* cell = [tableView dequeueReusableCellWithIdentifier:@"friendCell"];
    if( cell == nil){
        cell = [[FriendSelectorTableViewCell alloc]initWithStyle:UITableViewCellStyleDefault reuseIdentifier:@"friendCell"];
    }
    
    cell.selectionStyle = UITableViewCellSelectionStyleGray;
    ShareInfo* info = [[self.gift.shareInfo allObjects] objectAtIndex:indexPath.row];
    cell.name = info.friendName;
    cell.fbFriendId = info.fbFriendId;
    cell.shareInfo = info;
    [cell createSubviews];
    return cell;
}


- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section{
    return [self.gift.shareInfo count];
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView{
    return 1;
}


- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath{
    FriendSelectorTableViewCell* cell = (FriendSelectorTableViewCell*)[tableView cellForRowAtIndexPath:indexPath];
    [self.delegate onRedeemGift:self.gift withShareInfo:cell.shareInfo];
    [self removeFromSuperview];
}

#pragma mark - friend selector
-(IBAction)onCloseBtn:(id)sender{
    [self removeFromSuperview];
}

@end
