//
//  ClaimSuccess.m
//  Kikbak
//
//  Created by Ian Barile on 8/25/13.
//  Copyright (c) 2013 Ian Barile. All rights reserved.
//

#import "ClaimSuccessView.h"
#import <QuartzCore/QuartzCore.h>
#import "UIDevice+Screen.h"
#import "util.h"


@interface ClaimSuccessView ()

@property (nonatomic, strong) UIView* backgroundView;
@property (nonatomic, strong) UILabel* success;
@property (nonatomic, strong) UILabel* submitted;
@property (nonatomic, strong) UIButton* okBtn;

-(IBAction)onOk:(id)sender;

@end


@implementation ClaimSuccessView

- (id)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        self.backgroundColor = [UIColor colorWithRed:0 green:0 blue:0 alpha:.8];
    }
    return self;
}

-(void) createSubviews{
    
    CGRect frame = self.frame;
    if ([UIDevice hasFourInchDisplay]) {
        frame.origin.x += 22;
        frame.origin.y += 134;
        frame.size.width -= 44;
        frame.size.height -= 342;
    }
    else{
        frame.origin.x += 22;
        frame.origin.y += 132;
        frame.size.width -= 44;
        frame.size.height -= 254;
    }
    
    self.backgroundView = [[UIView alloc]initWithFrame:frame];
    self.backgroundView.backgroundColor = UIColorFromRGB(0xE0E0E0);
    self.backgroundView.layer.cornerRadius = 10;
    self.backgroundView.layer.masksToBounds = YES;
    [self addSubview:self.backgroundView];
    
    self.success = [[UILabel alloc]initWithFrame:CGRectMake(0, 24, frame.size.width, 32)];
    self.success.font = [UIFont fontWithName:@"HelveticaNeue-Bold" size:31];
    self.success.text = NSLocalizedString(@"Success", nil);
    self.success.textAlignment = NSTextAlignmentCenter;
    self.success.textColor = UIColorFromRGB(0x3a3a3a);
    self.success.backgroundColor = [UIColor clearColor];
    [self.backgroundView addSubview:self.success];
    
    self.submitted = [[UILabel alloc]initWithFrame:CGRectMake(40, 80, frame.size.width-80, 50)];
    self.submitted.font = [UIFont fontWithName:@"HelveticaNeue-Bold" size:20];
    self.submitted.text = NSLocalizedString(@"reward claim", nil);
    self.submitted.textAlignment = NSTextAlignmentCenter;
    self.submitted.numberOfLines = 2;
    self.submitted.textColor = UIColorFromRGB(0x3a3a3a);
    self.submitted.backgroundColor = [UIColor clearColor];
    [self.backgroundView addSubview:self.submitted];
        
    self.okBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    self.okBtn.frame = CGRectMake(11,frame.size.height - 52, frame.size.width - 22, 40);
    [self.okBtn setBackgroundImage:[UIImage imageNamed:@"btn_blue"] forState:UIControlStateNormal];
    [self.okBtn setTitle:NSLocalizedString(@"Ok", nil) forState:UIControlStateNormal];
    self.okBtn.titleLabel.textColor = [UIColor whiteColor];
    [self.backgroundView addSubview:self.okBtn];
    [self.okBtn addTarget:self action:@selector(onOk:) forControlEvents:UIControlEventTouchUpInside];
}

-(IBAction)onOk:(id)sender{
    [self.delegate onClaimFinished];
    [self removeFromSuperview];
}

@end
