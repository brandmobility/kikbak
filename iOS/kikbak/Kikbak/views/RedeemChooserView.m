//
//  RedeemChooserView.m
//  Kikbak
//
//  Created by Ian Barile on 5/22/13.
//  Copyright (c) 2013 Ian Barile. All rights reserved.
//

#import "RedeemChooserView.h"
#import "UIDevice+Screen.h"
#import "RedeemListViewController.h"
#import "RewardCollection.h"
#import <QuartzCore/QuartzCore.h>
#import "util.h"

@interface RedeemChooserView()

@property (nonatomic,strong) UIView* backgroundView;
@property (nonatomic,strong) UIButton* cancelBtn;
@property (nonatomic,strong) UILabel* selectOne;
@property (nonatomic,strong) UILabel* redeem;
@property (nonatomic,strong) UIButton* giftBtn;
@property (nonatomic,strong) UILabel* choice;
@property (nonatomic,strong) UIButton* creditBtn;

-(IBAction)onRedeemGift:(id)sender;
-(IBAction)onRedeemCredit:(id)sender;
-(IBAction)onCancel:(id)sender;

@end

@implementation RedeemChooserView

- (id)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        self.backgroundColor = [UIColor colorWithRed:0 green:0 blue:0 alpha:.8];
    }
    return self;
}

-(void)manuallyLayoutSubviews{
    
    CGRect frame = self.frame;
    if ([UIDevice hasFourInchDisplay]) {
        frame.origin.x += 24;
        frame.origin.y += 165;
        frame.size.width -= 48;
        frame.size.height = 200;
    }
    else{
        frame.origin.x += 24;
        frame.origin.y += 137;
        frame.size.width -= 48;
        frame.size.height = 200;
    }
    
    self.backgroundView = [[UIView alloc]initWithFrame:frame];
    self.backgroundView.backgroundColor = UIColorFromRGB(0xE0E0E0);
    self.backgroundView.layer.cornerRadius = 10;
    self.backgroundView.layer.masksToBounds = YES;
    [self addSubview:self.backgroundView];
    
    self.cancelBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    [self.cancelBtn addTarget:self action:@selector(onCancel:) forControlEvents:UIControlEventTouchUpInside];
    [self.cancelBtn setImage:[UIImage imageNamed:@"btn_cancel"] forState:UIControlStateNormal];
    self.cancelBtn.frame = CGRectMake(8, frame.origin.y-15, 35, 35);
    [self addSubview:self.cancelBtn];
    
    
    self.selectOne = [[UILabel alloc]initWithFrame:CGRectMake(12, 18, frame.size.width - 24, 23)];
    self.selectOne.font = [UIFont fontWithName:@"HelveticaNeue-Bold" size:21];
    self.selectOne.textAlignment = NSTextAlignmentCenter;
    self.selectOne.backgroundColor = [UIColor clearColor];
    self.selectOne.text = NSLocalizedString(@"Redeem Select", nil);
    [self.backgroundView addSubview:self.selectOne];

    self.redeem = [[UILabel alloc]initWithFrame:CGRectMake(12, 40, frame.size.width - 24, 23)];
    self.redeem.font = [UIFont fontWithName:@"HelveticaNeue-Bold" size:21];
    self.redeem.textAlignment = NSTextAlignmentCenter;
    self.redeem.backgroundColor = [UIColor clearColor];
    self.redeem.text = NSLocalizedString(@"To Redeem", nil);
    [self.backgroundView addSubview:self.redeem];
    
    self.creditBtn = [UIButton buttonWithType:UIButtonTypeRoundedRect];
    [self.creditBtn setTitle:self.credit forState:UIControlStateNormal];
    [self.creditBtn setBackgroundImage:[UIImage imageNamed:@"btn_blue"] forState:UIControlStateNormal];
    self.creditBtn.frame = CGRectMake(12, 74, frame.size.width - 24, 40);
    [self.creditBtn setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
    self.creditBtn.titleLabel.font = [UIFont fontWithName:@"HelveticaNeue-Bold" size:16];
    [self.creditBtn addTarget:self action:@selector(onRedeemCredit:) forControlEvents:UIControlEventTouchUpInside];
    [self.backgroundView addSubview:self.creditBtn];
    
    self.choice = [[UILabel alloc]initWithFrame:CGRectMake(0, 123, frame.size.width, 18)];
    self.choice.font = [UIFont fontWithName:@"HelveticaNeue-Light" size:16];
    self.choice.textColor = UIColorFromRGB(0x3a3a3a);
    self.choice.textAlignment = NSTextAlignmentCenter;
    self.choice.backgroundColor = [UIColor clearColor];
    self.choice.text = NSLocalizedString(@"Or", nil);
    [self.backgroundView addSubview:self.choice];

    self.giftBtn = [UIButton buttonWithType:UIButtonTypeRoundedRect];
    [self.giftBtn setTitle:self.gift forState:UIControlStateNormal];
    [self.giftBtn setBackgroundImage:[UIImage imageNamed:@"btn_grey"] forState:UIControlStateNormal];
    [self.giftBtn setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
    self.giftBtn.frame = CGRectMake(12, 146, frame.size.width - 24, 40);
    self.giftBtn.titleLabel.font = [UIFont fontWithName:@"HelveticaNeue-Bold" size:16];
    [self.giftBtn addTarget:self action:@selector(onRedeemGift:) forControlEvents:UIControlEventTouchUpInside];
    [self.backgroundView addSubview:self.giftBtn];

}

-(IBAction)onRedeemGift:(id)sender{
    [self.delegate onRedeemGift:self.collection.gift];
    [self removeFromSuperview];
}

-(IBAction)onRedeemCredit:(id)sender{
    [self.delegate onRedeemCredit:self.collection.credit];
    [self removeFromSuperview];
}

-(IBAction)onCancel:(id)sender{
    [self removeFromSuperview];
}

@end
