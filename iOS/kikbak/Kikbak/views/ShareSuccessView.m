//
//  ShareSuccessView.m
//  kikit
//
//  Created by Ian Barile on 3/25/13.
//  Copyright (c) 2013 Ian Barile. All rights reserved.
//

#import "ShareSuccessView.h"
#import <QuartzCore/QuartzCore.h>
#import "UIDevice+Screen.h"
#import "util.h"


@interface ShareSuccessView ()

@property (nonatomic, strong) UIView* backgroundView;
@property (nonatomic, strong) UILabel* success;
@property (nonatomic, strong) UILabel* shared;
@property (nonatomic, strong) UILabel* notify;
@property (nonatomic, strong) UIButton* okBtn;

-(IBAction)onOk:(id)sender;

@end

@implementation ShareSuccessView

- (id)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        self.backgroundColor = [UIColor colorWithRed:0 green:0 blue:0 alpha:.8];
    }
    return self;
}

-(void) manuallyLayoutSubviews{
    
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
    
    self.shared = [[UILabel alloc]initWithFrame:CGRectMake(0, 55, frame.size.width, 26)];
    self.shared.font = [UIFont fontWithName:@"HelveticaNeue-Bold" size:21];
    self.shared.text = NSLocalizedString(@"Shared Gift", nil);
    self.shared.textAlignment = NSTextAlignmentCenter;
    self.shared.numberOfLines = 1;
    self.shared.textColor = UIColorFromRGB(0x3a3a3a);
    self.shared.backgroundColor = [UIColor clearColor];
    [self.backgroundView addSubview:self.shared];
    
    self.notify = [[UILabel alloc]initWithFrame:CGRectMake(11, 110, frame.size.width-22, 40)];
    self.notify.font = [UIFont fontWithName:@"HelveticaNeue-Light" size:16];
    self.notify.text = NSLocalizedString(@"Gift Notification", nil);
    self.notify.textAlignment = NSTextAlignmentCenter;
    self.notify.numberOfLines = 2;
    self.notify.textColor = UIColorFromRGB(0x3a3a3a);
    self.notify.backgroundColor = [UIColor clearColor];
    [self.backgroundView addSubview:self.notify];
    
    self.okBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    self.okBtn.frame = CGRectMake(11,frame.size.height - 52, frame.size.width - 22, 40);
    [self.okBtn setBackgroundImage:[UIImage imageNamed:@"btn_blue"] forState:UIControlStateNormal];
    [self.okBtn setTitle:NSLocalizedString(@"Ok", nil) forState:UIControlStateNormal];
    self.okBtn.titleLabel.textColor = [UIColor whiteColor];
    [self.backgroundView addSubview:self.okBtn];
    [self.okBtn addTarget:self action:@selector(onOk:) forControlEvents:UIControlEventTouchUpInside];
}

-(IBAction)onOk:(id)sender{
    [self removeFromSuperview];
    [self.delegate onShareFinished];
}

@end
