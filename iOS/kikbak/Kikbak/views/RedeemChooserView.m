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

@interface RedeemChooserView()

@property (nonatomic, strong) UIView* backgroundView;
@property (nonatomic,strong) UILabel* redeemChoiceLabel;
@property (nonatomic,strong) UIButton* giftBtn;
@property (nonatomic,strong) UIButton* creditBtn;

-(IBAction)onRedeemGift:(id)sender;
-(IBAction)onRedeemCredit:(id)sender;

@end

@implementation RedeemChooserView

- (id)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        self.backgroundColor = [UIColor colorWithRed:.35 green:.35 blue:.35 alpha:.3];
    }
    return self;
}

-(void)manuallyLayoutSubviews{
    
    CGRect frame = self.frame;
    if ([UIDevice hasFourInchDisplay]) {
        frame.origin.x += 8;
        frame.origin.y += 170;
        frame.size.width -= 16;
        frame.size.height -= 280;
    }
    else{
        frame.origin.x += 8;
        frame.origin.y += 120;
        frame.size.width -= 16;
        frame.size.height -= 200;
    }
    
    self.backgroundView = [[UIView alloc]initWithFrame:frame];
    self.backgroundView.backgroundColor = [UIColor whiteColor];
    [self addSubview:self.backgroundView];
    self.backgroundView.layer.borderWidth = 4.0f;
    self.backgroundView.layer.borderColor = [UIColor grayColor].CGColor;
    
    
    self.redeemChoiceLabel = [[UILabel alloc]initWithFrame:CGRectMake(0, 0, frame.size.width, 26)];
    self.redeemChoiceLabel.font = [UIFont systemFontOfSize:26];
    self.redeemChoiceLabel.textAlignment = NSTextAlignmentCenter;
    self.redeemChoiceLabel.text = NSLocalizedString(@"redeem choice", nil);
    [self.backgroundView addSubview:self.redeemChoiceLabel];
    
    self.giftBtn = [UIButton buttonWithType:UIButtonTypeRoundedRect];
    [self.giftBtn setTitle:@"Give" forState:UIControlStateNormal];
    self.giftBtn.frame = CGRectMake(0, 40, 80, 40);
    [self.giftBtn addTarget:self action:@selector(onRedeemGift:) forControlEvents:UIControlEventTouchUpInside];
    [self.backgroundView addSubview:self.giftBtn];
    
    self.creditBtn = [UIButton buttonWithType:UIButtonTypeRoundedRect];
    [self.creditBtn setTitle:@"Credit" forState:UIControlStateNormal];
    self.creditBtn.frame = CGRectMake(0, 100, 80, 40);
    [self.creditBtn addTarget:self action:@selector(onRedeemCredit:) forControlEvents:UIControlEventTouchUpInside];
    [self.backgroundView addSubview:self.creditBtn];
    
}

-(IBAction)onRedeemGift:(id)sender{
    [self removeFromSuperview];
    [self.delegate onRedeemGift:self.collection.gift];
}

-(IBAction)onRedeemCredit:(id)sender{
    [self removeFromSuperview];
    [self.delegate onRedeemCredit:self.collection.credit];
}
@end
