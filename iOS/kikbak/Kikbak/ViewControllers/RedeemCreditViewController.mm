//
//  RedeemCreditViewController.m
//  Kikbak
//
//  Created by Ian Barile on 5/25/13.
//  Copyright (c) 2013 Ian Barile. All rights reserved.
//

#import "RedeemCreditViewController.h"
#import "CreditChooserViewController.h"
#import "NotificationContstants.h"
#import "QRCodeReader.h"
#import "util.h"
#import "Credit.h"
#import "UIDevice+Screen.h"
#import "RedeemCreditRequest.h"
#import "RedeemCreditSuccessViewController.h"
#import "Location.h"
#import "TermsAndConditionsView.h"
#import "AppDelegate.h"

@interface RedeemCreditViewController ()

@property(nonatomic,strong)NSNumber* creditToUse;

@property (nonatomic,strong) UIImageView* retailerImage;
@property (nonatomic,strong) UIImageView* dropShadow;
@property (nonatomic,strong) UIImageView* imageGradient;
@property (nonatomic,strong) UILabel* retailerName;
@property (nonatomic,strong) UILabel* redeemCount;
@property (nonatomic,strong) UIImageView* imageSeparator;

@property (nonatomic,strong) UILabel* amountToApply;
@property (nonatomic,strong) UIImageView* topAmountSeparator;
@property (nonatomic,strong) UILabel* creditAmount;
@property (nonatomic,strong) UIButton* changeAmountBtn;
@property (nonatomic,strong) UIImageView* bottomAmountSeparator;

@property (nonatomic,strong) UILabel* warning;

@property (nonatomic,strong) UIButton* termsBtn;
@property (nonatomic,strong) UIButton* redeemBtn;

-(void)createSubviews;
-(void)manuallyLayoutSubviews;

-(IBAction)onRedeem:(id)sender;
-(IBAction)onChangeAmount:(id)sender;

-(void) onRedeemKikbakSuccess:(NSNotification*)notification;
-(void) onRedeemKikbakError:(NSNotification*)notification;

@end

@implementation RedeemCreditViewController

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
    
    self.creditToUse = self.credit.value;
    [self createSubviews];
    [self manuallyLayoutSubviews];
}


-(void)viewWillAppear:(BOOL)animated{
    [super viewWillAppear:animated];
    
    [[NSNotificationCenter defaultCenter]addObserver:self selector:@selector(onRedeemKikbakSuccess:) name:kKikbakRedeemCreditSuccess object:nil];
    [[NSNotificationCenter defaultCenter]addObserver:self selector:@selector(onRedeemKikbakError:) name:kKikbakRedeemCreditError object:nil];
}

-(void)viewWillDisappear:(BOOL)animated{
    [super viewWillDisappear:animated];
 
    [[NSNotificationCenter defaultCenter]removeObserver:self name:kKikbakRedeemCreditSuccess object:nil];
    [[NSNotificationCenter defaultCenter]removeObserver:self name:kKikbakRedeemCreditError object:nil];

}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

-(void)manuallyLayoutSubviews{
    if(![UIDevice hasFourInchDisplay]){
        self.retailerImage.frame = CGRectMake(0, 0, 320, 219);
        self.imageGradient.frame = CGRectMake(0, 105, 320, 114);
        self.retailerName.frame = CGRectMake(11, 159, 298, 25);
        self.redeemCount.frame = CGRectMake(11, 189, 298, 14);
        self.imageSeparator.frame = CGRectMake(0, 217, 320, 2);
        self.amountToApply.frame = CGRectMake(11, 236, 298, 14);
        self.topAmountSeparator.frame = CGRectMake(11, 255, 298, 1);
        self.creditAmount.frame = CGRectMake(11, 265, 150, 38);
        self.changeAmountBtn.frame = CGRectMake(241, 268, 68, 30);
        self.bottomAmountSeparator.frame = CGRectMake(11, 308, 298, 1);
        self.warning.frame = CGRectMake(22, 315, 276, 28);
        self.termsBtn.frame = CGRectMake(11, 347, 150, 14);
        self.redeemBtn.frame = CGRectMake(11, 366, 298, 40);
    }
}

-(void)createSubviews{
    self.retailerImage = [[UIImageView alloc]initWithImage:[UIImage imageNamed:@"img"]];
    self.retailerImage.frame = CGRectMake(0, 0, 320, 290);
    [self.view addSubview:self.retailerImage];
    
    self.dropShadow = [[UIImageView alloc]initWithFrame:CGRectMake(0, 0, 320, 4)];
    self.dropShadow.image = [UIImage imageNamed:@"grd_navbar_drop_shadow"];
    [self.view addSubview:self.dropShadow];
    
    self.imageGradient = [[UIImageView alloc]initWithImage:[UIImage imageNamed:@"grd_redeem_credit_img"]];
    self.imageGradient.frame = CGRectMake(0, 140, 320, 150);
    [self.view addSubview:self.imageGradient];
    
    self.retailerName = [[UILabel alloc]initWithFrame:CGRectMake(11, 232, 298, 25)];
    self.retailerName.font = [UIFont fontWithName:@"HelveticaNeue" size:24];
    self.retailerName.textColor = [UIColor whiteColor];
    self.retailerName.text = self.credit.merchantName;
    self.retailerName.backgroundColor = [UIColor clearColor];
    self.retailerName.textAlignment = NSTextAlignmentLeft;
    [self.view addSubview:self.retailerName];
    
    self.redeemCount = [[UILabel alloc]initWithFrame:CGRectMake(11, 262, 298, 14)];
    self.redeemCount.text = [NSString stringWithFormat:NSLocalizedString(@"Redeemed By Friends", nil), [self.credit.redeeemedGiftsCount integerValue]];
    self.redeemCount.font = [UIFont fontWithName:@"HelveticaNeue-Bold" size:14];
    self.redeemCount.textColor = [UIColor whiteColor];
    self.redeemCount.backgroundColor = [UIColor clearColor];
    self.redeemCount.textAlignment = NSTextAlignmentLeft;
    [self.view addSubview:self.redeemCount];
    
    self.imageSeparator = [[UIImageView alloc]initWithFrame:CGRectMake(0, 289, 320, 2)];
    self.imageSeparator.image = [UIImage imageNamed:@"separator_dots"];
    [self.view addSubview:self.imageSeparator];
    
    self.amountToApply = [[UILabel alloc]initWithFrame:CGRectMake(11, 307, 298, 14)];
    self.amountToApply.font = [UIFont fontWithName:@"HelveticaNeue" size:12];
    self.amountToApply.backgroundColor = [UIColor clearColor];
    self.amountToApply.textColor = UIColorFromRGB(0x3a3a3a);
    self.amountToApply.textAlignment = NSTextAlignmentLeft;
    self.amountToApply.text = NSLocalizedString(@"Credit To Apply", nil);
    [self.view addSubview:self.amountToApply];
    
    self.topAmountSeparator = [[UIImageView alloc]initWithFrame:CGRectMake(11, 326, 298, 1)];
    self.topAmountSeparator.image = [UIImage imageNamed:@"separator_gray_line"];
    [self.view addSubview:self.topAmountSeparator];

    self.creditAmount = [[UILabel alloc]initWithFrame:CGRectMake(11, 335, 150, 38)];
    self.creditAmount.backgroundColor = [UIColor clearColor];
    self.creditAmount.textColor = UIColorFromRGB(0x3a3a3a);
    self.creditAmount.textAlignment = NSTextAlignmentLeft;
    self.creditAmount.text = [NSString stringWithFormat:NSLocalizedString(@"Redeem Amount", nil),[self.creditToUse doubleValue]];
    self.creditAmount.font = [UIFont fontWithName:@"HelveticaNeue-Bold" size:36];
    [self.view addSubview:self.creditAmount];
    
    self.changeAmountBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    [self.changeAmountBtn setTitle:@"Change" forState:UIControlStateNormal];
    [self.changeAmountBtn setBackgroundImage:[UIImage imageNamed:@"btn_change"] forState:UIControlStateNormal];
    [self.changeAmountBtn setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
    self.changeAmountBtn.titleLabel.font = [UIFont fontWithName:@"HelveticaNeue-Bold" size:12];
    self.changeAmountBtn.frame = CGRectMake(241, 338, 68, 30);
    [self.changeAmountBtn addTarget:self action:@selector(onChangeAmount:) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:self.changeAmountBtn];
    
    self.bottomAmountSeparator = [[UIImageView alloc]initWithFrame:CGRectMake(11, 378, 298, 1)];
    self.bottomAmountSeparator.image = [UIImage imageNamed:@"separator_gray_line"];
    [self.view addSubview:self.bottomAmountSeparator];

    self.warning = [[UILabel alloc]initWithFrame:CGRectMake(22, 395, 276, 28)];
    self.warning.backgroundColor = [UIColor clearColor];
    self.warning.textColor = UIColorFromRGB(0x898989);
    self.warning.font = [UIFont fontWithName:@"HelveticaNeue" size:11];
    self.warning.numberOfLines = 2;
    self.warning.text = NSLocalizedString(@"Credit Warning", nil);
    self.warning.textAlignment = NSTextAlignmentLeft;
    [self.view addSubview:self.warning];
    
    self.termsBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    self.termsBtn.frame = CGRectMake(11, 434, 150, 14);
    [self.termsBtn setTitle:NSLocalizedString(@"Terms and Conditions", nil) forState:UIControlStateNormal];
    self.termsBtn.titleLabel.font = [UIFont fontWithName:@"HelveticaNeue-Light" size:12];
    [self.termsBtn setTitleColor:UIColorFromRGB(0x686868) forState:UIControlStateNormal];
    self.termsBtn.contentHorizontalAlignment = UIControlContentHorizontalAlignmentLeft;
    [self.termsBtn addTarget:self action:@selector(onTermsBtn:) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:self.termsBtn];
    
    
    self.redeemBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    self.redeemBtn.frame = CGRectMake(11, 453, 298, 40);
    [self.redeemBtn setBackgroundImage:[UIImage imageNamed:@"btn_blue"] forState:UIControlStateNormal];
    [self.redeemBtn setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
    [self.redeemBtn setTitle:NSLocalizedString(@"Redeem QR Code", nil) forState:UIControlStateNormal];
    [self.redeemBtn addTarget:self action:@selector(onRedeem:) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:self.redeemBtn];
    
}


#pragma mark - button handlers
-(IBAction)onChangeAmount:(id)sender{
    CreditChooserViewController* vc = [[CreditChooserViewController alloc]init];
    vc.credit = self.credit.value;
    vc.updateDelegate = self;
    [self.navigationController pushViewController:vc animated:YES];
}

-(IBAction)onRedeem:(id)sender{
    ZXingWidgetController *widController = [[ZXingWidgetController alloc] initWithDelegate:self showCancel:YES OneDMode:NO];
    
    NSMutableSet *readers = [[NSMutableSet alloc ] init];
    QRCodeReader* qrcodeReader = [[QRCodeReader alloc] init];
    [readers addObject:qrcodeReader];
    
    widController.readers = readers;
    [self presentViewController:widController animated:YES completion:nil];
}

-(void)onTermsBtn:(id)sender{
    CGRect frame = ((AppDelegate*)[UIApplication sharedApplication].delegate).window.frame;
    TermsAndConditionsView* view = [[TermsAndConditionsView alloc]initWithFrame:frame];
    view.tosUrl = self.credit.tosUrl;
    [view manuallyLayoutSubviews];
    [((AppDelegate*)[UIApplication sharedApplication].delegate).window addSubview:view];
}


#pragma mark - NSNotification Handlers
-(void) onRedeemKikbakSuccess:(NSNotification*)notification{
    RedeemCreditSuccessViewController* vc = [[RedeemCreditSuccessViewController alloc] init];
    vc.creditUsed = self.creditToUse;
    vc.merchantName = self.retailerName.text;
    vc.validationCode = [[notification object] objectForKey:@"authorizationCode"];
    vc.imagePath = [[notification object] objectForKey:@"imagePath"];
    [self.navigationController pushViewController:vc animated:YES];
    
}

-(void) onRedeemKikbakError:(NSNotification*)notification{
    
}

#pragma mark - UserCreditUpdate
-(void)onUpdateAmount:(NSNumber*)amount{
    self.creditToUse = amount;
    self.creditAmount.text = [NSString stringWithFormat:NSLocalizedString(@"Redeem Amount", nil),[self.creditToUse doubleValue]];
}

#pragma mark - zxing qrcode reader
- (void)zxingController:(ZXingWidgetController*)controller didScanResult:(NSString *)result{
    NSMutableDictionary* dict = [[NSMutableDictionary alloc]initWithCapacity:3];
    [dict setObject:self.credit.creditId forKey:@"id"];
    Location* location = [self.credit.location anyObject];
    [dict setObject:location.locationId forKey:@"locationId"];
    [dict setObject:self.creditToUse forKey:@"amount"];
    [dict setObject:result forKey:@"verificationCode"];
    
    if(self.credit != nil){
        RedeemCreditRequest* rcr = [[RedeemCreditRequest alloc]init];
        rcr.credit = self.credit;
        [rcr restRequest:dict];
    }
}

- (void)zxingControllerDidCancel:(ZXingWidgetController*)controller{
    
}

@end
