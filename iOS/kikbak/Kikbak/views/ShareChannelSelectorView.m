//
//  ShareChannelSelectorView.m
//  Kikbak
//
//  Created by Ian Barile on 7/14/13.
//  Copyright (c) 2013 Ian Barile. All rights reserved.
//

#import "ShareChannelSelectorView.h"
#import <QuartzCore/QuartzCore.h>
#import "util.h"

@interface ShareChannelSelectorView ()

@property (nonatomic,strong) UIView* backgroundView;
@property (nonatomic,strong) UILabel* share;
@property (nonatomic,strong) UILabel* email;
@property (nonatomic,strong) UIButton* emailBtn;
@property (nonatomic,strong) UILabel* facebook;
@property (nonatomic,strong) UIButton* timelineBtn;
@property (nonatomic,strong) UIButton* closeBtn;

-(IBAction)onEmail:(id)sender;
-(IBAction)onTimeline:(id)sender;
-(IBAction)onCloseBtn:(id)sender;

@end

@implementation ShareChannelSelectorView

- (id)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        // Initialization code
        self.backgroundColor = [UIColor colorWithRed:0 green:0 blue:0 alpha:.8];
    }
    return self;
}

/*
// Only override drawRect: if you perform custom drawing.
// An empty implementation adversely affects performance during animation.
- (void)drawRect:(CGRect)rect
{
    // Drawing code
}
*/
-(void)createsubviews{
 
    self.backgroundView = [[UIView alloc] initWithFrame:CGRectMake(23, 100, self.frame.size.width - 46, 226)];
    self.backgroundView.backgroundColor = UIColorFromRGB(0xE0E0E0);
    self.backgroundView.layer.cornerRadius = 10;
    [self addSubview:self.backgroundView];
    
    self.share = [[UILabel alloc]initWithFrame:CGRectMake(0, 18, self.backgroundView.frame.size.width, 19)];
    self.share.font = [UIFont fontWithName:@"HelveticaNeue" size:16];
    self.share.text = NSLocalizedString(@"Share type", nil);
    self.share.textAlignment = NSTextAlignmentCenter;
    self.share.backgroundColor = [UIColor clearColor];
    self.share.textColor = UIColorFromRGB(0x3a3a3a);
    [self.backgroundView addSubview:self.share];
    
    self.email = [[UILabel alloc]initWithFrame:CGRectMake(0, 50, self.backgroundView.frame.size.width, 24)];
    self.email.font = [UIFont fontWithName:@"HelveticaNeue-Bold" size:21];
    self.email.text = NSLocalizedString(@"Email", nil);
    self.email.textAlignment = NSTextAlignmentCenter;
    self.email.backgroundColor = [UIColor clearColor];
    self.email.textColor = UIColorFromRGB(0x3a3a3a);
    [self.backgroundView addSubview:self.email];

    self.emailBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    self.emailBtn.frame = CGRectMake(12, 81, self.backgroundView.frame.size.width - 24, 40);
    [self.emailBtn setBackgroundImage:[UIImage imageNamed:@"btn_blue"] forState:UIControlStateNormal];
    [self.emailBtn setTitle:NSLocalizedString(@"Send to select friends", nil) forState:UIControlStateNormal];
    self.emailBtn.titleLabel.font = [UIFont fontWithName:@"HelveticaNeue-Bold" size:16];
    self.emailBtn.titleLabel.textColor = [UIColor whiteColor];
    [self.emailBtn addTarget:self action:@selector(onEmail:) forControlEvents:UIControlEventTouchUpInside];
    [self.backgroundView addSubview:self.emailBtn];
    
    self.facebook = [[UILabel alloc]initWithFrame:CGRectMake(0, 141, self.backgroundView.frame.size.width, 24)];
    self.facebook.font = [UIFont fontWithName:@"HelveticaNeue-Bold" size:21];
    self.facebook.text = NSLocalizedString(@"Facebook", nil);
    self.facebook.textAlignment = NSTextAlignmentCenter;
    self.facebook.backgroundColor = [UIColor clearColor];
    self.facebook.textColor = UIColorFromRGB(0x3a3a3a);
    [self.backgroundView addSubview:self.facebook];
    
    self.timelineBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    self.timelineBtn.frame = CGRectMake(12, 172, self.backgroundView.frame.size.width - 24, 40);
    [self.timelineBtn setBackgroundImage:[UIImage imageNamed:@"btn_blue"] forState:UIControlStateNormal];
    [self.timelineBtn setTitle:NSLocalizedString(@"Post on my timeline", nil) forState:UIControlStateNormal];
    self.timelineBtn.titleLabel.font = [UIFont fontWithName:@"HelveticaNeue-Bold" size:16];
    self.timelineBtn.titleLabel.textColor = [UIColor whiteColor];
    [self.timelineBtn addTarget:self action:@selector(onTimeline:) forControlEvents:UIControlEventTouchUpInside];
    [self.backgroundView addSubview:self.timelineBtn];
    
    self.closeBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    [self.closeBtn addTarget:self action:@selector(onCloseBtn:) forControlEvents:UIControlEventTouchUpInside];
    [self.closeBtn setImage:[UIImage imageNamed:@"btn_cancel"] forState:UIControlStateNormal];
    self.closeBtn.frame = CGRectMake(8, self.backgroundView.frame.origin.y-15, 35, 35);
    [self addSubview:self.closeBtn];
}

-(IBAction)onEmail:(id)sender{
    [self.delegate onEmailSelected];
    [self removeFromSuperview];
}

-(IBAction)onTimeline:(id)sender{
    [self.delegate onTimelineSelected];
    [self removeFromSuperview];
}

-(IBAction)onCloseBtn:(id)sender{
    [self removeFromSuperview];
}

@end
