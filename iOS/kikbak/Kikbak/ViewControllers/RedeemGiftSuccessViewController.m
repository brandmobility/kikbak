//
//  RedeemGiftSuccessViewController.m
//  Kikbak
//
//  Created by Ian Barile on 6/25/13.
//  Copyright (c) 2013 Ian Barile. All rights reserved.
//

#import "RedeemGiftSuccessViewController.h"
#import "UIDevice+Screen.h"
#import "Gift.h"
#import "util.h"
#import "UIButton+Util.h"
#import "GiveViewController.h"
#import "OfferService.h"

@interface RedeemGiftSuccessViewController ()

@property (nonatomic, strong) UIView* retailerBG;
@property (nonatomic, strong) UIImageView* dropShadow;
@property (nonatomic, strong) UILabel* retailerName;

@property (nonatomic, strong) UIView* successBG;
@property (nonatomic, strong) UILabel* success;
@property (nonatomic, strong) UILabel* claimedGift;
@property (nonatomic, strong) UILabel* showScreen;
@property (nonatomic, strong) UIImageView* dottedSeperator;

@property (nonatomic, strong) UILabel* offer;
@property (nonatomic, strong) UILabel* desc;
@property (nonatomic, strong) UILabel* details;
@property (nonatomic, strong) UIImageView* seperator;

@property (nonatomic, strong) UIImageView* validationImage;
@property (nonatomic, strong) UILabel* couponCode;

@property (nonatomic, strong) UILabel* share;
@property (nonatomic, strong) UILabel* earn;

@property (nonatomic, strong) UIButton* giveBtn;

-(void)createSubviews;
-(void)manuallyLayoutSubviews;

-(IBAction)onGiveBtn:(id)sender;

@end

@implementation RedeemGiftSuccessViewController

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
        self.retailerBG.frame = CGRectMake(0, 0, 320, 59);
        self.dropShadow.frame = CGRectMake(0, 0, 320, 4);
        self.retailerName.frame = CGRectMake(0, 17, 320, 34);
        self.successBG.frame = CGRectMake(0, 59, 320, 50);

        self.success.frame = CGRectMake(50, 10, 320, 15);
        self.success.font = [UIFont fontWithName:@"HelveticaNeue-Bold" size:13];
        self.success.textAlignment = NSTextAlignmentLeft;
        self.claimedGift.frame = CGRectMake(111, 10, 320, 15);
        self.claimedGift.textAlignment = NSTextAlignmentLeft;
        self.showScreen.frame = CGRectMake(0, 27, 320, 15);
        self.dottedSeperator.frame = CGRectMake(0, 109, 320, 2);
        
        self.offer.frame = CGRectMake(0, 122, 320, 28);
        self.desc.frame = CGRectMake(0, 146, 320, 48);
        self.details.frame = CGRectMake(0, 184, 320, 26);
        self.seperator.frame = CGRectMake(11, 208, 298, 1);
        self.validationImage.frame = CGRectMake(60, 232, 200, 75);
        self.couponCode.frame = CGRectMake(0, 307, 320, 12);

        if( [self.validationType compare:@"qrcode"] == NSOrderedSame){
            self.validationImage.frame = CGRectMake(42, 220, 100, 100);
            self.couponCode.frame = CGRectMake(180, 255, 150, 23);
            self.couponCode.textAlignment = NSTextAlignmentLeft;
            self.couponCode.font = [UIFont fontWithName:@"HelveticaNeue-Bold" size:22];
        }
        
        self.earn.frame = CGRectMake(0, 325, 320, 15);
        self.earn.frame = CGRectMake(0, 342, 320, 15);
        self.giveBtn.frame = CGRectMake(11, 365, 298, 40);
    }
}

-(void)createSubviews{
    self.view.backgroundColor = [UIColor whiteColor];
    
    self.retailerBG = [[UIView alloc]initWithFrame:CGRectMake(0, 0, 320, 88)];
    self.retailerBG.backgroundColor = [UIColor colorWithPatternImage:[UIImage imageNamed:@"bg_offwhite_eggshell"]];
    [self.view addSubview:self.retailerBG];
    
    self.dropShadow = [[UIImageView alloc]initWithFrame:CGRectMake(0, 0, 320, 4)];
    self.dropShadow.image = [UIImage imageNamed:@"grd_navbar_drop_shadow"];
    [self.view addSubview:self.dropShadow];
    
    self.retailerName = [[UILabel alloc]initWithFrame:CGRectMake(0, 31, 320, 34)];
    self.retailerName.font = [UIFont fontWithName:@"HelveticaNeue-Light" size:31];
    self.retailerName.textAlignment = NSTextAlignmentCenter;
    self.retailerName.textColor = UIColorFromRGB(0x3a3a3a);
    self.retailerName.backgroundColor = [UIColor clearColor];
    self.retailerName.text = self.merchantName;
    [self.view addSubview:self.retailerName];
    
    self.successBG = [[UIView alloc]initWithFrame:CGRectMake(0, 88, 320, 86)];
    self.successBG.backgroundColor = [UIColor colorWithPatternImage:[UIImage imageNamed:@"bg_green"]];
    [self.view addSubview:self.successBG];
    
    self.success = [[UILabel alloc] initWithFrame:CGRectMake(0, 16, 320, 23)];
    self.success.font = [UIFont fontWithName:@"HelveticaNeue-Bold" size:21];
    self.success.textAlignment = NSTextAlignmentCenter;
    self.success.textColor = UIColorFromRGB(0x16521b);
    self.success.text = NSLocalizedString(@"Success", nil);
    self.success.backgroundColor = [UIColor clearColor];
    [self.successBG addSubview:self.success];
    
    self.claimedGift = [[UILabel alloc] initWithFrame:CGRectMake(0, 46, 320, 15)];
    self.claimedGift.font = [UIFont fontWithName:@"HelveticaNeue" size:13];
    self.claimedGift.textAlignment = NSTextAlignmentCenter;
    self.claimedGift.textColor = UIColorFromRGB(0x16521b);
    self.claimedGift.text = NSLocalizedString(@"Claimed Gift", nil);
    self.claimedGift.backgroundColor = [UIColor clearColor];
    [self.successBG addSubview:self.claimedGift];
    
    
    self.showScreen = [[UILabel alloc]initWithFrame:CGRectMake(0, 62, 320, 15)];
    self.showScreen.font = [UIFont fontWithName:@"HelveticaNeue" size:13];
    self.showScreen.textAlignment = NSTextAlignmentCenter;
    self.showScreen.textColor = UIColorFromRGB(0x16521b);
    self.showScreen.text = NSLocalizedString(@"Show Screen", nil);
    self.showScreen.backgroundColor = [UIColor clearColor];
    [self.successBG addSubview:self.showScreen];
    
    self.dottedSeperator = [[UIImageView alloc]initWithFrame:CGRectMake(0, 174, 320, 2)];
    self.dottedSeperator.image = [UIImage imageNamed:@"separater_dots_gift_success"];
    [self.view addSubview:self.dottedSeperator];
    
    self.offer = [[UILabel alloc]initWithFrame:CGRectMake(0, 190, 320, 28)];
    self.offer.text = NSLocalizedString(@"Gift", nil);
    self.offer.font = [UIFont fontWithName:@"HelveticaNeue-Bold" size:26];
    self.offer.textColor = UIColorFromRGB(0x9c9c9c);
    self.offer.textAlignment = NSTextAlignmentCenter;
    self.offer.backgroundColor = [UIColor clearColor];
    [self.view addSubview:self.offer];
    
    self.desc = [[UILabel alloc]initWithFrame:CGRectMake(0, 215, 320, 44)];
    if ([self.giftType compare:@"percentage"] == NSOrderedSame) {
        self.desc.text = [NSString stringWithFormat:NSLocalizedString(@"gift percent", nil), [self.value integerValue]];
    }
    else{
        self.desc.text = [NSString stringWithFormat:NSLocalizedString(@"amount off", nil), [self.value integerValue]];
    }
    self.desc.textAlignment = NSTextAlignmentCenter;
    self.desc.backgroundColor = [UIColor clearColor];
    self.desc.textColor = UIColorFromRGB(0x3a3a3a);
    self.desc.font = [UIFont fontWithName:@"HelveticaNeue-Bold" size:41];
    [self.view addSubview:self.desc];
    
    self.details = [[UILabel alloc]initWithFrame:CGRectMake(0, 259, 320, 15)];
    self.details.text = self.optionalDesc;
    self.details.textAlignment = NSTextAlignmentCenter;
    self.details.backgroundColor = [UIColor clearColor];
    self.details.textColor = UIColorFromRGB(0x3a3a3a);
    self.details.font = [UIFont fontWithName:@"HelveticaNeue" size:13];
    [self.view addSubview:self.details];
    
    self.seperator = [[UIImageView alloc]initWithFrame:CGRectMake(11, 406, 298, 1)];
    self.seperator.image = [UIImage imageNamed:@"separator_gray_line"];
    [self.view addSubview:self.seperator];
    
    self.validationImage = [[UIImageView alloc]initWithFrame:CGRectMake(60, 295, 200, 75)];
    self.validationImage.image = [UIImage imageWithContentsOfFile:self.imagePath];
    [self.view addSubview:self.validationImage];
    
    self.couponCode = [[UILabel alloc] initWithFrame:CGRectMake(0, 376, 320, 12)];
    self.couponCode.text = self.validationCode;
    self.couponCode.textAlignment = NSTextAlignmentCenter;
    self.couponCode.backgroundColor = [UIColor clearColor];
    self.couponCode.textColor = UIColorFromRGB(0x3a3a3a);
    self.couponCode.font = [UIFont fontWithName:@"HelveticaNeue" size:11];
    [self.view addSubview:self.couponCode];
    
    if( [self.validationType compare:@"qrcode"] == NSOrderedSame){
        self.validationImage.frame = CGRectMake(35, 285, 100, 100);
        self.couponCode.frame = CGRectMake(185, 324, 150, 33);
        self.couponCode.textAlignment = NSTextAlignmentLeft;
        self.couponCode.font = [UIFont fontWithName:@"HelveticaNeue-Bold" size:30];
    }
    
    self.share = [[UILabel alloc] initWithFrame:CGRectMake(0, 415, 320, 15)];
    self.share.text = NSLocalizedString(@"Share with friends", nil);
    self.share.textAlignment = NSTextAlignmentCenter;
    self.share.backgroundColor = [UIColor clearColor];
    self.share.textColor = UIColorFromRGB(0x3a3a3a);
    self.share.font = [UIFont fontWithName:@"HelveticaNeue" size:13];
    [self.view addSubview:self.share];

    self.earn = [[UILabel alloc] initWithFrame:CGRectMake(0, 429, 320, 15)];
    self.earn.text = NSLocalizedString(@"Earn for friend", nil);
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
    self.giveBtn.frame = CGRectMake(11, 453, 298, 40);
    [self.giveBtn addTarget:self action:@selector(onGiveBtn:) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:self.giveBtn];
}

#pragma mark - btn actions
-(IBAction)onBackBtn:(id)sender{
    [self.navigationController popToRootViewControllerAnimated:YES];
}

-(IBAction)onGiveBtn:(id)sender{
    GiveViewController* vc = [[GiveViewController alloc]init];
    vc.offer = [OfferService findOfferById:self.offerId];
    [self.navigationController pushViewController:vc animated:YES];
}

@end
