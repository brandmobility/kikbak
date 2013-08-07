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
@property (nonatomic,strong) UIButton* fbBtn;
@property (nonatomic,strong) UIImageView* or1;
@property (nonatomic,strong) UIButton* emailBtn;
@property (nonatomic,strong) UIImageView* or2;
@property (nonatomic,strong) UIButton* smsBtn;
@property (nonatomic,strong) UIButton* closeBtn;

-(IBAction)onEmail:(id)sender;
-(IBAction)onSms:(id)sender;
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
 
    self.backgroundView = [[UIView alloc] initWithFrame:CGRectMake(23, 100, self.frame.size.width - 46, 256)];
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
    
    self.fbBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    self.fbBtn.frame = CGRectMake(12, 50, self.backgroundView.frame.size.width - 24, 40);
    [self.fbBtn setBackgroundImage:[UIImage imageNamed:@"btn_blue"] forState:UIControlStateNormal];
    [self.fbBtn setTitle:NSLocalizedString(@"Post on my timeline", nil) forState:UIControlStateNormal];
    self.fbBtn.titleLabel.font = [UIFont fontWithName:@"HelveticaNeue-Bold" size:16];
    self.fbBtn.titleLabel.textColor = [UIColor whiteColor];
    [self.fbBtn addTarget:self action:@selector(onTimeline:) forControlEvents:UIControlEventTouchUpInside];
    [self.backgroundView addSubview:self.fbBtn];
    

    UIImage* orSeperator = [UIImage imageNamed:@"seperator_share_or"];
    self.or1 = [[UIImageView alloc]initWithImage:orSeperator];
    CGRect fr = CGRectMake(69, 105, orSeperator.size.width/2, orSeperator.size.height/2);
    self.or1.frame = fr;
    [self.backgroundView addSubview:self.or1];
    
    self.emailBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    self.emailBtn.frame = CGRectMake(12, 125, self.backgroundView.frame.size.width - 24, 40);
    [self.emailBtn setBackgroundImage:[UIImage imageNamed:@"btn_grey"] forState:UIControlStateNormal];
    [self.emailBtn setTitle:NSLocalizedString(@"Email", nil) forState:UIControlStateNormal];
    self.emailBtn.titleLabel.font = [UIFont fontWithName:@"HelveticaNeue-Bold" size:16];
    self.emailBtn.titleLabel.textColor = [UIColor whiteColor];
    [self.emailBtn addTarget:self action:@selector(onEmail:) forControlEvents:UIControlEventTouchUpInside];
    [self.backgroundView addSubview:self.emailBtn];
    
    
    self.or2 = [[UIImageView alloc]initWithImage:orSeperator];
    fr = CGRectMake(69, 175, orSeperator.size.width/2, orSeperator.size.height/2);
    self.or2.frame = fr;
    [self.backgroundView addSubview:self.or2];
    
    self.smsBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    self.smsBtn.frame = CGRectMake(12, 195, self.backgroundView.frame.size.width - 24, 40);
    [self.smsBtn setBackgroundImage:[UIImage imageNamed:@"btn_grey"] forState:UIControlStateNormal];
    [self.smsBtn setTitle:NSLocalizedString(@"SMS", nil) forState:UIControlStateNormal];
    self.smsBtn.titleLabel.font = [UIFont fontWithName:@"HelveticaNeue-Bold" size:16];
    self.smsBtn.titleLabel.textColor = [UIColor whiteColor];
    [self.smsBtn addTarget:self action:@selector(onSms:) forControlEvents:UIControlEventTouchUpInside];
    [self.backgroundView addSubview:self.smsBtn];

    
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

-(IBAction)onSms:(id)sender{
    [self.delegate onSmsSelected];
    [self removeFromSuperview];
}

-(IBAction)onCloseBtn:(id)sender{
    [self removeFromSuperview];
}

@end
