//
//  RedeemGiftSuccessViewController.m
//  Kikbak
//
//  Created by Ian Barile on 6/25/13.
//  Copyright (c) 2013 Ian Barile. All rights reserved.
//

#import "RedeemGiftSuccessViewController.h"
#import "UIDevice+Screen.h"
#import "UIDevice+OSVersion.h"
#import "Gift.h"
#import "util.h"
#import "UIButton+Util.h"
#import "GiveViewController.h"
#import "OfferService.h"

static int offsetForIOS6 = 44;

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
@property (nonatomic, strong) UIButton* couponBtn;

-(void)createSubviews;
-(void)manuallyLayoutSubviews;

-(IBAction)onGiveBtn:(id)sender;
-(IBAction)onCouponBtn:(id)sender;

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
        self.retailerBG.frame = CGRectMake(0, 0 + offsetForIOS6, 320, 59);
        self.dropShadow.frame = CGRectMake(0, 0 + offsetForIOS6, 320, 4);
        self.retailerName.frame = CGRectMake(0, 17 + offsetForIOS6, 320, 34);
        self.successBG.frame = CGRectMake(0, 59 + offsetForIOS6, 320, 50);

        self.success.frame = CGRectMake(50, 10, 320, 15);
        self.success.font = [UIFont fontWithName:@"HelveticaNeue-Bold" size:13];
        self.success.textAlignment = NSTextAlignmentLeft;
        self.claimedGift.frame = CGRectMake(111, 10, 320, 15);
        self.claimedGift.textAlignment = NSTextAlignmentLeft;
        self.showScreen.frame = CGRectMake(0, 27, 320, 15);
        self.dottedSeperator.frame = CGRectMake(0, 109 + offsetForIOS6, 320, 2);
        
        self.offer.frame = CGRectMake(0, 122 + offsetForIOS6, 320, 28);
        self.desc.frame = CGRectMake(0, 146 + offsetForIOS6, 320, 48);
        self.details.frame = CGRectMake(0, 184 + offsetForIOS6, 320, 26);
        
        self.validationImage.frame = CGRectMake(60, 222 + offsetForIOS6, 200, 75);
        self.couponCode.frame = CGRectMake(0, 302 + offsetForIOS6, 320, 12);
        
        if( self.online == true ){
            self.couponCode.font = [UIFont fontWithName:@"HelveticaNeue-Bold" size:30];
            CGRect fr = self.couponCode.frame;
            fr.origin.y = 240 + offsetForIOS6;
            fr.origin.x = 11;
            fr.size.height = 40;
            self.couponCode.frame = fr;
            self.couponCode.textColor = [UIColor redColor];
            self.couponBtn.frame = CGRectMake(250, 240 + offsetForIOS6, 60, 40);
        }

        if( [self.validationType compare:@"qrcode"] == NSOrderedSame){
            self.validationImage.frame = CGRectMake(42, 210 + offsetForIOS6, 100, 100);
            self.couponCode.frame = CGRectMake(185, 245 + offsetForIOS6, 150, 34);
            self.couponCode.textAlignment = NSTextAlignmentLeft;
            self.couponCode.font = [UIFont fontWithName:@"HelveticaNeue-Bold" size:30];
        }

        self.seperator.frame = CGRectMake(11, 318 + offsetForIOS6, 298, 1);
        
        self.share.frame = CGRectMake(0, 325 + offsetForIOS6, 320, 15);
        self.earn.frame = CGRectMake(0, 342 + offsetForIOS6, 320, 15);
        self.giveBtn.frame = CGRectMake(11, 365 + offsetForIOS6, 298, 40);
    }
}

-(void)createSubviews{
    self.view.backgroundColor = [UIColor whiteColor];
    
    self.retailerBG = [[UIView alloc]initWithFrame:CGRectMake(0, 0 + offsetForIOS6, 320, 88)];
    self.retailerBG.backgroundColor = [UIColor colorWithPatternImage:[UIImage imageNamed:@"bg_offwhite_eggshell"]];
    [self.view addSubview:self.retailerBG];
    
    self.dropShadow = [[UIImageView alloc]initWithFrame:CGRectMake(0, 0 + offsetForIOS6, 320, 4)];
    self.dropShadow.image = [UIImage imageNamed:@"grd_navbar_drop_shadow"];
    [self.view addSubview:self.dropShadow];
    
    self.retailerName = [[UILabel alloc]initWithFrame:CGRectMake(0, 31 + offsetForIOS6, 320, 34)];
    self.retailerName.font = [UIFont fontWithName:@"HelveticaNeue-Light" size:31];
    self.retailerName.textAlignment = NSTextAlignmentCenter;
    self.retailerName.textColor = UIColorFromRGB(0x3a3a3a);
    self.retailerName.backgroundColor = [UIColor clearColor];
    self.retailerName.text = self.merchantName;
    [self.view addSubview:self.retailerName];
    
    self.successBG = [[UIView alloc]initWithFrame:CGRectMake(0, 88 + offsetForIOS6, 320, 86)];
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
    
    
//    self.showScreen = [[UILabel alloc]initWithFrame:CGRectMake(0, 62, 320, 15)];
//    self.showScreen.font = [UIFont fontWithName:@"HelveticaNeue" size:13];
//    self.showScreen.textAlignment = NSTextAlignmentCenter;
//    self.showScreen.textColor = UIColorFromRGB(0x16521b);
//    self.showScreen.text = NSLocalizedString(@"Show Screen", nil);
//    self.showScreen.backgroundColor = [UIColor clearColor];
//    [self.successBG addSubview:self.showScreen];
    
    self.dottedSeperator = [[UIImageView alloc]initWithFrame:CGRectMake(0, 174 + offsetForIOS6, 320, 2)];
    self.dottedSeperator.image = [UIImage imageNamed:@"separater_dots_gift_success"];
    [self.view addSubview:self.dottedSeperator];
    
    self.offer = [[UILabel alloc]initWithFrame:CGRectMake(0, 190 + offsetForIOS6, 320, 28)];
    self.offer.text = NSLocalizedString(@"Offer", nil);
    self.offer.font = [UIFont fontWithName:@"HelveticaNeue-Bold" size:26];
    self.offer.textColor = UIColorFromRGB(0x9c9c9c);
    self.offer.textAlignment = NSTextAlignmentCenter;
    self.offer.backgroundColor = [UIColor clearColor];
    [self.view addSubview:self.offer];
    
    self.desc = [[UILabel alloc]initWithFrame:CGRectMake(0, 215 + offsetForIOS6, 320, 44)];
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
    
    self.details = [[UILabel alloc]initWithFrame:CGRectMake(0, 259 + offsetForIOS6, 320, 15)];
    self.details.text = self.optionalDesc;
    self.details.textAlignment = NSTextAlignmentCenter;
    self.details.backgroundColor = [UIColor clearColor];
    self.details.textColor = UIColorFromRGB(0x3a3a3a);
    self.details.font = [UIFont fontWithName:@"HelveticaNeue" size:13];
    [self.view addSubview:self.details];
    
    
    if( self.online == false ){
        self.seperator = [[UIImageView alloc]initWithFrame:CGRectMake(11, 406 + offsetForIOS6, 298, 1)];
        self.seperator.image = [UIImage imageNamed:@"separator_gray_line"];
        [self.view addSubview:self.seperator];

        
        self.validationImage = [[UIImageView alloc]initWithFrame:CGRectMake(60, 295 + offsetForIOS6, 200, 75)];
        self.validationImage.image = [UIImage imageWithContentsOfFile:self.imagePath];
        [self.view addSubview:self.validationImage];
        
        self.share = [[UILabel alloc] initWithFrame:CGRectMake(0, 415 + offsetForIOS6, 320, 15)];
        self.share.text = NSLocalizedString(@"Share with friends", nil);
        self.share.textAlignment = NSTextAlignmentCenter;
        self.share.backgroundColor = [UIColor clearColor];
        self.share.textColor = UIColorFromRGB(0x3a3a3a);
        self.share.font = [UIFont fontWithName:@"HelveticaNeue" size:13];
        [self.view addSubview:self.share];
        
        self.earn = [[UILabel alloc] initWithFrame:CGRectMake(0, 429 + offsetForIOS6, 320, 15)];
        self.earn.text = NSLocalizedString(@"Earn for friend", nil);
        self.earn.textAlignment = NSTextAlignmentCenter;
        self.earn.backgroundColor = [UIColor clearColor];
        self.earn.textColor = UIColorFromRGB(0x3a3a3a);
        self.earn.font = [UIFont fontWithName:@"HelveticaNeue" size:13];
        [self.view addSubview:self.earn];
    }
    
    self.couponCode = [[UILabel alloc] initWithFrame:CGRectMake(0, 376 + offsetForIOS6, 320, 12)];
    self.couponCode.text = self.validationCode;
    self.couponCode.textAlignment = NSTextAlignmentCenter;
    self.couponCode.backgroundColor = [UIColor clearColor];
    self.couponCode.textColor = UIColorFromRGB(0x3a3a3a);
    self.couponCode.font = [UIFont fontWithName:@"HelveticaNeue" size:11];
    [self.view addSubview:self.couponCode];
    
    if( self.online == true ){
        self.couponCode.font = [UIFont fontWithName:@"HelveticaNeue-Bold" size:30];
        CGRect fr = self.couponCode.frame;
        fr.origin.y = 300 + offsetForIOS6;
        fr.origin.x = 11;
        fr.size.height = 40;
        self.couponCode.frame = fr;
        self.couponCode.textAlignment = NSTextAlignmentLeft;
        self.couponCode.textColor = [UIColor redColor];
        
        self.couponBtn = [UIButton buttonWithType:UIButtonTypeCustom];
        [self.couponBtn setBackgroundImage:[UIImage imageNamed:@"btn_blue"] forState:UIControlStateNormal];
        [self.couponBtn setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
        self.couponBtn.titleLabel.font = [UIFont fontWithName:@"HelveticaNeue-Medium" size:15];
        [self.couponBtn setTitle:NSLocalizedString(@"Copy", nil) forState:UIControlStateNormal];
        self.couponBtn.frame = CGRectMake(250, 300 + offsetForIOS6, 60, 40);
        [self.couponBtn addTarget:self action:@selector(onCouponBtn:) forControlEvents:UIControlEventTouchUpInside];
        [self.view addSubview:self.couponBtn];

    }
    
    if( [self.validationType compare:@"qrcode"] == NSOrderedSame){
        self.validationImage.frame = CGRectMake(35, 285 + offsetForIOS6, 100, 100);
        self.couponCode.frame = CGRectMake(185, 324 + offsetForIOS6, 150, 33);
        self.couponCode.textAlignment = NSTextAlignmentLeft;
        self.couponCode.font = [UIFont fontWithName:@"HelveticaNeue-Bold" size:30];
    }
    
    
    if( [OfferService findOfferById:self.offerId] ){   
        self.giveBtn = [UIButton buttonWithType:UIButtonTypeCustom];
        [self.giveBtn setBackgroundImage:[UIImage imageNamed:@"btn_blue"] forState:UIControlStateNormal];
        if( self.online){
            [self.giveBtn setTitle:NSLocalizedString(@"Use Online", nil) forState:UIControlStateNormal];
        }
        else{
            [self.giveBtn setTitle:NSLocalizedString(@"Give to Friends", nil) forState:UIControlStateNormal];
        }
        [self.giveBtn setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
        self.giveBtn.titleLabel.font = [UIFont fontWithName:@"HelveticaNeue-Medium" size:15];
        self.giveBtn.frame = CGRectMake(11, 453 + offsetForIOS6, 298, 40);
        [self.giveBtn addTarget:self action:@selector(onGiveBtn:) forControlEvents:UIControlEventTouchUpInside];
        [self.view addSubview:self.giveBtn];
    }
}

#pragma mark - btn actions
-(IBAction)onBackBtn:(id)sender{
    [self.navigationController popToRootViewControllerAnimated:YES];
}

-(IBAction)onGiveBtn:(id)sender{
    if( self.online ){
        NSURL* url = [[NSURL alloc]initWithString:@"https://m.verizonwireless.com/shop"];
        if(![[UIApplication sharedApplication] canOpenURL:url]){
            UIAlertView* alert = [[UIAlertView alloc]initWithTitle:@"Hmmm..." message:@"You need to be on an iPhone to make a call" delegate:nil cancelButtonTitle:@"Ok" otherButtonTitles:nil];
            [alert show];
        }
        else{
            [[UIApplication sharedApplication]openURL:url];
        }
    }
    else{
        GiveViewController* vc = [[GiveViewController alloc]init];
        vc.offer = [OfferService findOfferById:self.offerId];
        [self.navigationController pushViewController:vc animated:YES];
    }
}

-(IBAction)onCouponBtn:(id)sender{
    UIPasteboard *pb = [UIPasteboard generalPasteboard];
    [pb setString:self.couponCode.text];
}

@end
