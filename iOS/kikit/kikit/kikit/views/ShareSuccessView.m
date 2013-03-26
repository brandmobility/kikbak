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


@interface ShareSuccessView ()

@property (nonatomic, strong) UIView* backgroundView;
@property (nonatomic, strong) UILabel* successLabel;
@property (nonatomic, strong) UILabel* notificationLabel;
@property (nonatomic, strong) UILabel* rewardLabel;
@property (nonatomic, strong) UIButton* doneButton;

-(IBAction)onDone:(id)sender;

@end

@implementation ShareSuccessView

- (id)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        self.backgroundColor = [UIColor colorWithRed:.35 green:.35 blue:.35 alpha:.3];
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

-(void) manuallyLayoutSubviews{
    
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
    
    self.successLabel = [[UILabel alloc]initWithFrame:CGRectMake(0, 10.0, frame.size.width, 40)];
    self.successLabel.font = [UIFont boldSystemFontOfSize:35];
    self.successLabel.text = NSLocalizedString(@"Success", nil);
    self.successLabel.textAlignment = NSTextAlignmentCenter;
    self.successLabel.textColor = [UIColor grayColor];
    [self.backgroundView addSubview:self.successLabel];
    
    self.notificationLabel = [[UILabel alloc]initWithFrame:CGRectMake(0, 100, frame.size.width, 26)];
    self.notificationLabel.font = [UIFont systemFontOfSize:22];
    self.notificationLabel.text = NSLocalizedString(@"Shared Gift", nil);
    self.notificationLabel.textAlignment = NSTextAlignmentCenter;
    self.notificationLabel.numberOfLines = 1;
    self.notificationLabel.textColor = [UIColor grayColor];
    [self.backgroundView addSubview:self.notificationLabel];
    
    self.rewardLabel = [[UILabel alloc]initWithFrame:CGRectMake(0, 150, frame.size.width, 40)];
    self.rewardLabel.font = [UIFont systemFontOfSize:16];
    self.rewardLabel.text = NSLocalizedString(@"Gift Notification", nil);
    self.rewardLabel.textAlignment = NSTextAlignmentCenter;
    self.rewardLabel.numberOfLines = 2;
    self.rewardLabel.textColor = [UIColor grayColor];
    [self.backgroundView addSubview:self.rewardLabel];
    
    self.doneButton = [UIButton buttonWithType:UIButtonTypeCustom];
    self.doneButton.frame = CGRectMake(15,frame.size.height - 60, frame.size.width - 30, 40);
    self.doneButton.layer.cornerRadius = 3.0;
    self.doneButton.layer.borderWidth = 3.0;
    self.doneButton.layer.borderColor = [[UIColor colorWithRed:.81 green:0.81 blue:0.81 alpha:0.9] CGColor];
    self.doneButton.backgroundColor = [UIColor grayColor];
    [self.doneButton setTitle:NSLocalizedString(@"Awesome", nil) forState:UIControlStateNormal];
    self.doneButton.titleLabel.textColor = [UIColor whiteColor];
    [self.backgroundView addSubview:self.doneButton];
    [self.doneButton addTarget:self action:@selector(onDone:) forControlEvents:UIControlEventTouchUpInside];
}

-(IBAction)onDone:(id)sender{    
    [self removeFromSuperview];
}

@end
