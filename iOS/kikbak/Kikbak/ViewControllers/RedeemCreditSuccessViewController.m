//
//  RedeemCreditSuccessViewController.m
//  Kikbak
//
//  Created by Ian Barile on 6/25/13.
//  Copyright (c) 2013 Ian Barile. All rights reserved.
//

#import "RedeemCreditSuccessViewController.h"
#import "util.h"
#import "Credit.h"
#import "UIDevice+Screen.h"
#import "UIDevice+OSVersion.h"
#import "UIButton+Util.h"
#import "GiveViewController.h"
#import "OfferService.h"

static int offsetForIOS6 = 44;

@interface RedeemCreditSuccessViewController ()

@property (nonatomic, strong) UIView* retailerBG;
@property (nonatomic, strong) UIImageView* dropShadow;
@property (nonatomic, strong) UILabel* retailerName;

@property (nonatomic, strong) UIView* successBG;
@property (nonatomic, strong) UILabel* success;
@property (nonatomic, strong) UILabel* claimedCredit;
@property (nonatomic, strong) UILabel* showScreen;
@property (nonatomic, strong) UIImageView* dottedSeperator;

@property (nonatomic, strong) UILabel* offer;
@property (nonatomic, strong) UILabel* desc;
@property (nonatomic, strong) UILabel* details;
@property (nonatomic, strong) UIImageView* seperator;

@property (nonatomic, strong) UIImageView* qrCode;
@property (nonatomic, strong) UILabel* couponCode;

@property (nonatomic, strong) UILabel* share;
@property (nonatomic, strong) UILabel* earn;

@property (nonatomic, strong) UIButton* giveBtn;

-(void)createSubviews;
-(void)manuallyLayoutSubviews;

-(IBAction)onGiveBtn:(id)sender;

@end

@implementation RedeemCreditSuccessViewController

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

    self.navigationItem.hidesBackButton = YES;
    self.navigationItem.leftBarButtonItem = [UIButton blackBackBtn:self];
    
    self.title = NSLocalizedString(@"Success", nil);
    
    if( [UIDevice osVersion7orGreater] ){
        self.edgesForExtendedLayout = UIRectEdgeNone;
        offsetForIOS6 = 0;
    }
    
    [self createSubviews];
    [self manuallyLayoutSubviews];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

-(void)manuallyLayoutSubviews{
    if(![UIDevice hasFourInchDisplay]){
        self.retailerBG.frame = CGRectMake(0, 0+offsetForIOS6, 320, 59);
        self.dropShadow.frame = CGRectMake(0, 0+offsetForIOS6, 320, 4);
        self.retailerName.frame = CGRectMake(0, 17+offsetForIOS6, 320, 34);
        
        
        self.successBG.frame = CGRectMake(0, 59+offsetForIOS6, 320, 50);
        
        self.success.frame = CGRectMake(50, 10, 320, 15);
        self.success.font = [UIFont fontWithName:@"HelveticaNeue-Bold" size:13];
        self.success.textAlignment = NSTextAlignmentLeft;
        self.claimedCredit.frame = CGRectMake(111, 10, 320, 15);
        self.claimedCredit.textAlignment = NSTextAlignmentLeft;
        self.showScreen.frame = CGRectMake(0, 27, 320, 15);
        self.dottedSeperator.frame = CGRectMake(0, 109+offsetForIOS6, 320, 2);
        
        self.offer.frame = CGRectMake(0, 122+offsetForIOS6, 320, 28);
        self.desc.frame = CGRectMake(0, 146+offsetForIOS6, 320, 48);
        self.details.frame = CGRectMake(0, 184+offsetForIOS6, 320, 26);
        self.couponCode.frame = CGRectMake(0, 307+offsetForIOS6, 320, 12);
        
        self.qrCode.frame = CGRectMake(42, 210+offsetForIOS6, 100, 100);
        self.couponCode.frame = CGRectMake(185, 245+offsetForIOS6, 150, 34);
        self.couponCode.textAlignment = NSTextAlignmentLeft;
        self.couponCode.font = [UIFont fontWithName:@"HelveticaNeue-Bold" size:30];

        self.seperator.frame = CGRectMake(11, 318+offsetForIOS6, 298, 1);
        
        self.share.frame = CGRectMake(0, 325+offsetForIOS6, 320, 15);
        self.earn.frame = CGRectMake(0, 342+offsetForIOS6, 320, 15);
        
        self.giveBtn.frame = CGRectMake(11, 365+offsetForIOS6, 298, 40);
        
    }
}

-(void)createSubviews{
    self.view.backgroundColor = [UIColor whiteColor];
    
    self.retailerBG = [[UIView alloc]initWithFrame:CGRectMake(0, 0+offsetForIOS6, 320, 88)];
    self.retailerBG.backgroundColor = [UIColor colorWithPatternImage:[UIImage imageNamed:@"bg_offwhite_eggshell"]];
    [self.view addSubview:self.retailerBG];
    
    self.dropShadow = [[UIImageView alloc]initWithFrame:CGRectMake(0, 0+offsetForIOS6, 320, 4)];
    self.dropShadow.image = [UIImage imageNamed:@"grd_navbar_drop_shadow"];
    [self.view addSubview:self.dropShadow];
    
    self.retailerName = [[UILabel alloc]initWithFrame:CGRectMake(0, 31+offsetForIOS6, 320, 34)];
    self.retailerName.font = [UIFont fontWithName:@"HelveticaNeue-Light" size:31];
    self.retailerName.textAlignment = NSTextAlignmentCenter;
    self.retailerName.textColor = UIColorFromRGB(0x3a3a3a);
    self.retailerName.backgroundColor = [UIColor clearColor];
    self.retailerName.text = self.merchantName;
    [self.view addSubview:self.retailerName];
    
    self.successBG = [[UIView alloc]initWithFrame:CGRectMake(0, 88+offsetForIOS6, 320, 86)];
    self.successBG.backgroundColor = [UIColor colorWithPatternImage:[UIImage imageNamed:@"bg_blue"]];
    [self.view addSubview:self.successBG];
    
    self.success = [[UILabel alloc] initWithFrame:CGRectMake(0, 16, 320, 23)];
    self.success.font = [UIFont fontWithName:@"HelveticaNeue-Bold" size:21];
    self.success.textAlignment = NSTextAlignmentCenter;
    self.success.textColor = UIColorFromRGB(0x0f4689);
    self.success.text = NSLocalizedString(@"Success", nil);
    self.success.backgroundColor = [UIColor clearColor];
    [self.successBG addSubview:self.success];
    
    self.claimedCredit = [[UILabel alloc] initWithFrame:CGRectMake(0, 46, 320, 15)];
    self.claimedCredit.font = [UIFont fontWithName:@"HelveticaNeue" size:13];
    self.claimedCredit.textAlignment = NSTextAlignmentCenter;
    self.claimedCredit.textColor = UIColorFromRGB(0x063e82);
    self.claimedCredit.text = NSLocalizedString(@"Claimed Credit", nil);
    self.claimedCredit.backgroundColor = [UIColor clearColor];
    [self.successBG addSubview:self.claimedCredit];
    
    
    self.showScreen = [[UILabel alloc]initWithFrame:CGRectMake(0, 62, 320, 15)];
    self.showScreen.font = [UIFont fontWithName:@"HelveticaNeue" size:13];
    self.showScreen.textAlignment = NSTextAlignmentCenter;
    self.showScreen.textColor = UIColorFromRGB(0x063e82);
    self.showScreen.text = NSLocalizedString(@"Show Screen", nil);
    self.showScreen.backgroundColor = [UIColor clearColor];
    [self.successBG addSubview:self.showScreen];
    
    self.dottedSeperator = [[UIImageView alloc]initWithFrame:CGRectMake(0, 174+offsetForIOS6, 320, 2)];
    self.dottedSeperator.image = [UIImage imageNamed:@"separater_dots_credit_success"];
    [self.view addSubview:self.dottedSeperator];
    
    self.offer = [[UILabel alloc]initWithFrame:CGRectMake(0, 194+offsetForIOS6, 320, 28)];
    self.offer.text = NSLocalizedString(@"Redeem Success Credit Label", nil);
    self.offer.font = [UIFont fontWithName:@"HelveticaNeue-Bold" size:26];
    self.offer.textColor = UIColorFromRGB(0x9c9c9c);
    self.offer.textAlignment = NSTextAlignmentCenter;
    self.offer.backgroundColor = [UIColor clearColor];
    [self.view addSubview:self.offer];
    
    
    NSNumberFormatter *numberFormatter;
    numberFormatter = [[NSNumberFormatter alloc] init];
    numberFormatter.numberStyle           = NSNumberFormatterDecimalStyle;
    numberFormatter.maximumFractionDigits = 2;
    numberFormatter.minimumFractionDigits = 2;
    numberFormatter.decimalSeparator      = @".";
    numberFormatter.usesGroupingSeparator = NO;
    numberFormatter.allowsFloats = YES;
    
    self.desc = [[UILabel alloc]initWithFrame:CGRectMake(0, 220+offsetForIOS6, 320, 42)];
    self.desc.text = [NSString stringWithFormat:NSLocalizedString(@"$%@", nil), [numberFormatter stringFromNumber:self.creditUsed]];
    self.desc.textAlignment = NSTextAlignmentCenter;
    self.desc.backgroundColor = [UIColor clearColor];
    self.desc.textColor = UIColorFromRGB(0x3a3a3a);
    self.desc.font = [UIFont fontWithName:@"HelveticaNeue-Bold" size:41];
    [self.view addSubview:self.desc];
    
    self.details = [[UILabel alloc]initWithFrame:CGRectMake(0, 255+offsetForIOS6, 320, 26)];
    self.details.text = NSLocalizedString(@"Your Purchase", nil);
    self.details.textAlignment = NSTextAlignmentCenter;
    self.details.backgroundColor = [UIColor clearColor];
    self.details.textColor = UIColorFromRGB(0x3a3a3a);
    self.details.font = [UIFont fontWithName:@"HelveticaNeue" size:13];
    [self.view addSubview:self.details];

    
    self.qrCode = [[UIImageView alloc]initWithFrame:CGRectMake(40, 285+offsetForIOS6, 109, 109)];
    self.qrCode.image = [UIImage imageWithContentsOfFile:self.imagePath];
    [self.view addSubview:self.qrCode];
    
    self.couponCode = [[UILabel alloc] initWithFrame:CGRectMake(180, 324+offsetForIOS6, 320, 46)];
    self.couponCode.text = self.validationCode;
    self.couponCode.textAlignment = NSTextAlignmentLeft;
    self.couponCode.backgroundColor = [UIColor clearColor];
    self.couponCode.textColor = UIColorFromRGB(0x3a3a3a);
    self.couponCode.font = [UIFont fontWithName:@"HelveticaNeue-Bold" size:41];
    [self.view addSubview:self.couponCode];
    
    self.seperator = [[UIImageView alloc]initWithFrame:CGRectMake(11, 406+offsetForIOS6, 298, 1)];
    self.seperator.image = [UIImage imageNamed:@"separator_gray_line"];
    [self.view addSubview:self.seperator];
    
    self.share = [[UILabel alloc] initWithFrame:CGRectMake(0, 415+offsetForIOS6, 320, 15)];
    self.share.text = NSLocalizedString(@"Share with friends", nil);
    self.share.textAlignment = NSTextAlignmentCenter;
    self.share.backgroundColor = [UIColor clearColor];
    self.share.textColor = UIColorFromRGB(0x3a3a3a);
    self.share.font = [UIFont fontWithName:@"HelveticaNeue" size:13];
    [self.view addSubview:self.share];
    
    self.earn = [[UILabel alloc] initWithFrame:CGRectMake(0, 429+offsetForIOS6, 320, 15)];
    self.earn.text = NSLocalizedString(@"Earn more credit", nil);
    self.earn.textAlignment = NSTextAlignmentCenter;
    self.earn.backgroundColor = [UIColor clearColor];
    self.earn.textColor = UIColorFromRGB(0x3a3a3a);
    self.earn.font = [UIFont fontWithName:@"HelveticaNeue" size:13];
    [self.view addSubview:self.earn];
    
    self.giveBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    [self.giveBtn setBackgroundImage:[UIImage imageNamed:@"btn_blue"] forState:UIControlStateNormal];
    [self.giveBtn setTitle:NSLocalizedString(@"Give to Friends", nil) forState:UIControlStateNormal];
    [self.giveBtn setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
    self.giveBtn.titleLabel.font = [UIFont fontWithName:@"HelveticaNeue-Medium" size:15];
    self.giveBtn.frame = CGRectMake(11, 453+offsetForIOS6, 298, 40);
    [self.giveBtn addTarget:self action:@selector(onGiveBtn:) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:self.giveBtn];
}

#pragma mark - on back btn
-(IBAction)onBackBtn:(id)sender{
    [self.navigationController popToRootViewControllerAnimated:YES];
}

-(IBAction)onGiveBtn:(id)sender{
    GiveViewController* vc = [[GiveViewController alloc]init];
    vc.offer = [OfferService findOfferById:self.offerId];
    [self.navigationController pushViewController:vc animated:YES];
}

@end
