//
//  RedeemClaimViewController.m
//  Kikbak
//
//  Created by Ian Barile on 11/1/13.
//  Copyright (c) 2013 Ian Barile. All rights reserved.
//

#import "RedeemClaimViewController.h"
#import "NotificationContstants.h"
#import "util.h"
#import "Credit.h"
#import "UIDevice+Screen.h"
#import "UIDevice+OSVersion.h"
#import "ClaimCreditViewController.h"
#import "Location.h"
#import "TermsAndConditionsView.h"
#import "AppDelegate.h"
#import "SpinnerView.h"
#import "ImagePersistor.h"
#import "UIImage+Manipulate.h"
#import "UIButton+Util.h"
#import "ClaimCreditViewController.h"


static int offsetForIOS6 = 44;

@interface RedeemClaimViewController ()
@property (nonatomic,strong) NSNumber* creditToUse;

@property (nonatomic,strong) SpinnerView* spinnerView;

@property (nonatomic,strong) UIImageView* retailerImage;
@property (nonatomic,strong) UIImageView* dropShadow;
@property (nonatomic,strong) UIImageView* imageGradient;
@property (nonatomic,strong) UILabel* retailerName;
@property (nonatomic,strong) UILabel* redeemCount;
@property (nonatomic,strong) UIImageView* imageSeparator;

@property (nonatomic,strong) UILabel* amountToApply;
@property (nonatomic,strong) UIImageView* topAmountSeparator;
@property (nonatomic,strong) UILabel* creditAmount;

@property (nonatomic,strong) UILabel* giftcard;

@property (nonatomic,strong) UIButton* termsBtn;
@property (nonatomic,strong) UIButton* claimBtn;
@property (nonatomic,strong) NSNumber* offerId;

-(void)createSubviews;
-(void)manuallyLayoutSubviews;

-(IBAction)onClaimBtn:(id)sender;

@end

@implementation RedeemClaimViewController

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    self.title = NSLocalizedString(@"Redeem", nil);
	// Do any additional setup after loading the view.
    self.view.backgroundColor = [UIColor whiteColor];
    
    if( [UIDevice osVersion7orGreater] ){
        self.edgesForExtendedLayout = UIRectEdgeNone;
        offsetForIOS6 = 0;
    }
    
    self.navigationItem.hidesBackButton = YES;
    self.navigationItem.leftBarButtonItem = [UIButton blackBackBtn:self];
    
    self.creditToUse = self.credit.value;
    self.offerId = self.credit.offerId;
    [self createSubviews];
    [self manuallyLayoutSubviews];
}


-(void)viewWillAppear:(BOOL)animated{
    [super viewWillAppear:animated];
    
    NSString* imagePath = [ImagePersistor imageFileExists:self.credit.merchantId imageType:DEFAULT_MERCHANT_IMAGE_TYPE];
    if(imagePath != nil){
        self.retailerImage.image = [[UIImage alloc]initWithContentsOfFile:imagePath];
    }
    if( ![UIDevice hasFourInchDisplay] ){
        CGRect retina35CropRect = CGRectMake(0, 74, 640, self.retailerImage.image.size.height-148);
        self.retailerImage.image = [self.retailerImage.image imageCropToRect:retina35CropRect];
    }
}

-(void)viewWillDisappear:(BOOL)animated{
    [super viewWillDisappear:animated];
    
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

-(void)manuallyLayoutSubviews{
    if(![UIDevice hasFourInchDisplay]){
        self.retailerImage.frame = CGRectMake(0, 0+offsetForIOS6, 320, 219);
        self.imageGradient.frame = CGRectMake(0, 105+offsetForIOS6, 320, 114);
        self.retailerName.frame = CGRectMake(11, 159+offsetForIOS6, 298, 28);
        self.redeemCount.frame = CGRectMake(11, 189+offsetForIOS6, 298, 18);
        self.imageSeparator.frame = CGRectMake(0, 217+offsetForIOS6, 320, 2);
        self.amountToApply.frame = CGRectMake(11, 236+offsetForIOS6, 298, 14);
        self.topAmountSeparator.frame = CGRectMake(11, 255+offsetForIOS6, 298, 1);
        self.creditAmount.frame = CGRectMake(11, 265+offsetForIOS6, 150, 38);
        self.giftcard.frame = CGRectMake(11, 305+offsetForIOS6, 276, 28);
        self.termsBtn.frame = CGRectMake(11, 347+offsetForIOS6, 150, 14);
        self.claimBtn.frame = CGRectMake(11, 366+offsetForIOS6, 298, 40);
    }
}

-(void)createSubviews{
    self.retailerImage = [[UIImageView alloc]initWithImage:[UIImage imageNamed:@"img"]];
    self.retailerImage.frame = CGRectMake(0, 0+offsetForIOS6, 320, 290);
    [self.view addSubview:self.retailerImage];
    
    self.dropShadow = [[UIImageView alloc]initWithFrame:CGRectMake(0, 0+offsetForIOS6, 320, 4)];
    self.dropShadow.image = [UIImage imageNamed:@"grd_navbar_drop_shadow"];
    [self.view addSubview:self.dropShadow];
    
    self.imageGradient = [[UIImageView alloc]initWithImage:[UIImage imageNamed:@"grd_redeem_credit_img"]];
    self.imageGradient.frame = CGRectMake(0, 140+offsetForIOS6, 320, 150);
    [self.view addSubview:self.imageGradient];
    
    self.retailerName = [[UILabel alloc]initWithFrame:CGRectMake(11, 232+offsetForIOS6, 298, 28)];
    self.retailerName.font = [UIFont fontWithName:@"HelveticaNeue" size:24];
    self.retailerName.textColor = [UIColor whiteColor];
    self.retailerName.text = self.credit.merchantName;
    self.retailerName.backgroundColor = [UIColor clearColor];
    self.retailerName.textAlignment = NSTextAlignmentLeft;
    [self.view addSubview:self.retailerName];
    
    self.redeemCount = [[UILabel alloc]initWithFrame:CGRectMake(11, 260+offsetForIOS6, 298, 19)];
    self.redeemCount.text = [NSString stringWithFormat:NSLocalizedString(@"Redeemed By Friends", nil), [self.credit.redeeemedGiftsCount integerValue]];
    self.redeemCount.font = [UIFont fontWithName:@"HelveticaNeue-Bold" size:14];
    self.redeemCount.textColor = [UIColor whiteColor];
    self.redeemCount.backgroundColor = [UIColor clearColor];
    self.redeemCount.textAlignment = NSTextAlignmentLeft;
    [self.view addSubview:self.redeemCount];
    
    self.imageSeparator = [[UIImageView alloc]initWithFrame:CGRectMake(0, 289+offsetForIOS6, 320, 2)];
    self.imageSeparator.image = [UIImage imageNamed:@"separator_dots"];
    [self.view addSubview:self.imageSeparator];
    
    self.amountToApply = [[UILabel alloc]initWithFrame:CGRectMake(11, 307+offsetForIOS6, 298, 14)];
    self.amountToApply.font = [UIFont fontWithName:@"HelveticaNeue" size:12];
    self.amountToApply.backgroundColor = [UIColor clearColor];
    self.amountToApply.textColor = UIColorFromRGB(0x3a3a3a);
    self.amountToApply.textAlignment = NSTextAlignmentLeft;
    self.amountToApply.text = NSLocalizedString(@"Reward Ready", nil);
    [self.view addSubview:self.amountToApply];
    
    self.topAmountSeparator = [[UIImageView alloc]initWithFrame:CGRectMake(11, 326+offsetForIOS6, 298, 1)];
    self.topAmountSeparator.image = [UIImage imageNamed:@"separator_gray_line"];
    [self.view addSubview:self.topAmountSeparator];
    
    self.creditAmount = [[UILabel alloc]initWithFrame:CGRectMake(11, 335+offsetForIOS6, 150, 38)];
    self.creditAmount.backgroundColor = [UIColor clearColor];
    self.creditAmount.textColor = UIColorFromRGB(0x3a3a3a);
    self.creditAmount.textAlignment = NSTextAlignmentLeft;
    self.creditAmount.text = [NSString stringWithFormat:NSLocalizedString(@"Redeem Amount", nil),[self.creditToUse doubleValue]];
    self.creditAmount.font = [UIFont fontWithName:@"HelveticaNeue-Bold" size:36];
    [self.view addSubview:self.creditAmount];
    
    
    self.giftcard = [[UILabel alloc]initWithFrame:CGRectMake(11, 375+offsetForIOS6, 276, 34)];
    self.giftcard.backgroundColor = [UIColor clearColor];
    self.giftcard.textColor = UIColorFromRGB(0x929292);
    self.giftcard.font = [UIFont fontWithName:@"HelveticaNeue" size:26];
    self.giftcard.numberOfLines = 2;
    self.giftcard.text = NSLocalizedString(@"Gift Card", nil);
    self.giftcard.textAlignment = NSTextAlignmentLeft;
    [self.view addSubview:self.giftcard];
    
    self.termsBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    self.termsBtn.frame = CGRectMake(11, 434+offsetForIOS6, 150, 14);
    [self.termsBtn setTitle:NSLocalizedString(@"Terms and Conditions", nil) forState:UIControlStateNormal];
    self.termsBtn.titleLabel.font = [UIFont fontWithName:@"HelveticaNeue-Light" size:12];
    [self.termsBtn setTitleColor:UIColorFromRGB(0x686868) forState:UIControlStateNormal];
    self.termsBtn.contentHorizontalAlignment = UIControlContentHorizontalAlignmentLeft;
    [self.termsBtn addTarget:self action:@selector(onTermsBtn:) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:self.termsBtn];
    
    
    self.claimBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    self.claimBtn.frame = CGRectMake(11, 453+offsetForIOS6, 298, 40);
    [self.claimBtn setBackgroundImage:[UIImage imageNamed:@"btn_blue"] forState:UIControlStateNormal];
    [self.claimBtn setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
    [self.claimBtn setTitle:NSLocalizedString(@"Claim reward now", nil) forState:UIControlStateNormal];
    [self.claimBtn addTarget:self action:@selector(onClaimBtn:) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:self.claimBtn];
    
}


#pragma mark - button handlers
-(IBAction)onBackBtn:(id)sender{
    [self.navigationController popViewControllerAnimated:YES];
}


-(IBAction)onClaimBtn:(id)sender{
    ClaimCreditViewController* vc = [[ClaimCreditViewController alloc]initWithNibName:nil bundle:nil];
    vc.merchantName = self.credit.merchantName;
    vc.credit = self.credit;
    [self.navigationController pushViewController:vc animated:YES];
}

-(void)onTermsBtn:(id)sender{
    CGRect frame = ((AppDelegate*)[UIApplication sharedApplication].delegate).window.frame;
    TermsAndConditionsView* view = [[TermsAndConditionsView alloc]initWithFrame:frame];
    view.tosUrl = self.credit.tosUrl;
    [view manuallyLayoutSubviews];
    [((AppDelegate*)[UIApplication sharedApplication].delegate).window addSubview:view];
}




@end
