//
//  RedeemView.m
//  kikit
//
//  Created by Ian Barile on 3/14/13.
//  Copyright (c) 2013 Ian Barile. All rights reserved.
//

#import "RedeemView.h"
#import <QuartzCore/QuartzCore.h>
#import "UIDevice+Screen.h"

@interface RedeemView()
@property (nonatomic, strong) UIView* backgroundView;
@property (nonatomic, strong) UILabel* successLabel;
@property (nonatomic, strong) UILabel* claimedLabel;
@property (nonatomic, strong) UILabel* instructionLabel;
@property (nonatomic, strong) UIView* barcodeBackgroundView;
@property (nonatomic, strong) UILabel* valueLabel;
@property (nonatomic, strong) UILabel* restrictionsLabel;
@property (nonatomic, strong) UIImageView* dottedLineImage;
@property (nonatomic, strong) UILabel* verficationCodeLabel;
@property (nonatomic, strong) UILabel* verficationCode;
@property (nonatomic, strong) UIButton* doneButton;

-(IBAction)onDone:(id)sender;
@end

@implementation RedeemView

- (id)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        // Initialization code
        self.backgroundColor = [UIColor colorWithRed:.35 green:.35 blue:.35 alpha:.3];
    }
    return self;
}


-(void) manuallyLayoutSubviews{
    
    CGRect frame = self.frame;
    if ([UIDevice hasFourInchDisplay]) {
        frame.origin.x += 8;
        frame.origin.y += 140;
        frame.size.width -= 16;
        frame.size.height -= 230;
    }
    else{
        frame.origin.x += 8;
        frame.origin.y += 100;
        frame.size.width -= 16;
        frame.size.height -= 140;
    }
    self.backgroundView = [[UIView alloc]initWithFrame:frame];
    self.backgroundView.backgroundColor = [UIColor whiteColor];
    [self addSubview:self.backgroundView];
    self.backgroundView.layer.borderWidth = 4.0f;
    self.backgroundView.layer.borderColor = [UIColor grayColor].CGColor;
    
    self.successLabel = [[UILabel alloc]initWithFrame:CGRectMake(0, 10.0, frame.size.width, 40)];
    self.successLabel.font = [UIFont boldSystemFontOfSize:35];
    self.successLabel.text = @"Success!";
    self.successLabel.textAlignment = NSTextAlignmentCenter;
    self.successLabel.textColor = [UIColor grayColor];
    [self.backgroundView addSubview:self.successLabel];
    
    self.claimedLabel = [[UILabel alloc]initWithFrame:CGRectMake(0, 70, frame.size.width, 26)];
    self.claimedLabel.font = [UIFont systemFontOfSize:22];
    self.claimedLabel.text = @"You have claimed your gift.";
    self.claimedLabel.textAlignment = NSTextAlignmentCenter;
    self.claimedLabel.textColor = [UIColor grayColor];
    [self.backgroundView addSubview:self.claimedLabel];
    
    self.instructionLabel = [[UILabel alloc]initWithFrame:CGRectMake(0, 100, frame.size.width, 20)];
    self.instructionLabel.font = [UIFont systemFontOfSize:16];
    self.instructionLabel.text = @"Please show this screen to the Cashier";
    self.instructionLabel.textAlignment = NSTextAlignmentCenter;
    self.instructionLabel.textColor = [UIColor grayColor];
    [self.backgroundView addSubview:self.instructionLabel];
    
    self.barcodeBackgroundView = [[UIView alloc]initWithFrame:CGRectMake(15, 135, frame.size.width - 30, frame.size.height-80-135)];
    self.barcodeBackgroundView.backgroundColor = [UIColor colorWithRed:0.98 green:0.98 blue:0.98 alpha:1];
    self.barcodeBackgroundView.layer.borderColor = [UIColor grayColor].CGColor;
    self.barcodeBackgroundView.layer.borderWidth = 1.0f;
    self.barcodeBackgroundView.layer.cornerRadius = 5;
    [self.backgroundView addSubview:self.barcodeBackgroundView];
    
    self.valueLabel = [[UILabel alloc]initWithFrame:CGRectMake(0, 0, self.barcodeBackgroundView.frame.size.width, 40)];
    self.valueLabel.font = [UIFont boldSystemFontOfSize:37];
    self.valueLabel.text = @"$10 Off";
    self.valueLabel.textAlignment = NSTextAlignmentCenter;
    self.valueLabel.textColor = [UIColor colorWithRed:0.549 green:0.278 blue:0.337 alpha:1];
    self.valueLabel.backgroundColor = [UIColor clearColor];
    [self.barcodeBackgroundView addSubview:self.valueLabel];
    
    self.restrictionsLabel = [[UILabel alloc]initWithFrame:CGRectMake(0, 40, self.barcodeBackgroundView.frame.size.width, 15)];
    self.restrictionsLabel.font = [UIFont boldSystemFontOfSize:13];
    self.restrictionsLabel.text = @"any purchase over $50";
    self.restrictionsLabel.textAlignment = NSTextAlignmentCenter;
    self.restrictionsLabel.textColor = [UIColor colorWithRed:0.549 green:0.278 blue:0.337 alpha:1];
    self.restrictionsLabel.backgroundColor = [UIColor clearColor];
    [self.barcodeBackgroundView addSubview:self.restrictionsLabel];
    
    self.dottedLineImage = [[UIImageView alloc]initWithImage:[UIImage imageNamed:@"dotted_line"]];
    self.dottedLineImage.backgroundColor = [UIColor clearColor];
    self.dottedLineImage.frame = CGRectMake(70, 67, self.barcodeBackgroundView.frame.size.width-140, 2);
    [self.barcodeBackgroundView addSubview:self.dottedLineImage];
    
    self.verficationCodeLabel = [[UILabel alloc] initWithFrame:CGRectMake(0, 80, self.barcodeBackgroundView.frame.size.width, 15)];
    self.verficationCodeLabel.font = [UIFont boldSystemFontOfSize:13];
    self.verficationCodeLabel.text = @"Verification Code";
    self.verficationCodeLabel.textAlignment = NSTextAlignmentCenter;
    self.verficationCodeLabel.textColor = [UIColor colorWithRed:0.549 green:0.278 blue:0.337 alpha:1];
    self.verficationCodeLabel.backgroundColor = [UIColor clearColor];
    [self.barcodeBackgroundView addSubview:self.verficationCodeLabel];
    
    self.verficationCode = [[UILabel alloc]initWithFrame:CGRectMake(0, self.barcodeBackgroundView.frame.size.height - 30, self.barcodeBackgroundView.frame.size.width, 30)];
    self.verficationCode.font = [UIFont boldSystemFontOfSize:27];
    self.verficationCode.text = @"AL8RT4";
    self.verficationCode.textAlignment = NSTextAlignmentCenter;
    self.verficationCode.textColor = [UIColor colorWithRed:0.549 green:0.278 blue:0.337 alpha:1];
    self.verficationCode.backgroundColor = [UIColor clearColor];
    [self.barcodeBackgroundView addSubview:self.verficationCode];
    
    self.doneButton = [UIButton buttonWithType:UIButtonTypeCustom];
    self.doneButton.frame = CGRectMake(15,frame.size.height - 60, frame.size.width - 30, 40);
    self.doneButton.layer.cornerRadius = 2.0;
    self.doneButton.backgroundColor = [UIColor grayColor];
    [self.doneButton setTitle:@"Done" forState:UIControlStateNormal];
    self.doneButton.titleLabel.textColor = [UIColor whiteColor];
    [self.backgroundView addSubview:self.doneButton];
    [self.doneButton addTarget:self action:@selector(onDone:) forControlEvents:UIControlEventTouchUpInside];
}

/*
// Only override drawRect: if you perform custom drawing.
// An empty implementation adversely affects performance during animation.
- (void)drawRect:(CGRect)rect
{
    // Drawing code
}
*/


-(IBAction)onDone:(id)sender{
    [self removeFromSuperview];
}
@end
