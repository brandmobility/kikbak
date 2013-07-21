//
//  RedeemViewController.m
//  kikit
//
//  Created by Ian Barile on 3/9/13.
//  Copyright (c) 2013 Ian Barile. All rights reserved.
//

#import "RedeemGiftViewController.h"
#import "UIDevice+Screen.h"
#import "RedeemView.h"
#import "AppDelegate.h"
#import "QRCodeReader.h"
#import "Gift.h"
#import "Credit.h"
#import "Location.h"
#import "RedeemGiftRequest.h"
#import "RewardCollection.h"
#import "Distance.h"
#import "NotificationContstants.h"
#import "ImagePersistor.h"
#import "util.h"
#import <QuartzCore/QuartzCore.h>
#import "UIButton+Util.h"
#import "RedeemGiftSuccessViewController.h"
#import "BarcodeImageRequest.h"
#import "TermsAndConditionsView.h"


@interface RedeemGiftViewController (){
    double distanceToLocation;
}

@property(nonatomic,strong) Location* location;

@property(nonatomic,strong) UIImageView* giftImage;
@property(nonatomic,strong) UIImageView* giftGradient;

@property(nonatomic, strong) UILabel* retailerName;
@property(nonatomic, strong) UIImageView* mapIcon;
@property(nonatomic, strong) UILabel* distance;
@property(nonatomic, strong) UIButton* mapBtn;
@property(nonatomic, strong) UIImageView* callIcon;
@property(nonatomic, strong) UIButton* callBtn;
@property(nonatomic, strong) UIImageView* webIcon;
@property(nonatomic, strong) UIButton* webBtn;

@property (nonatomic,strong) UIView* friendBackground;
@property (nonatomic,strong) UIImageView* giftImageDropShadow;
@property (nonatomic,strong) UIImageView* seperator;
@property (nonatomic,strong) UIImageView* friendImage;
@property (nonatomic,strong) UILabel* friendName;
@property (nonatomic,strong) UILabel* caption;


@property (nonatomic,strong) UILabel* giftDescription;
@property (nonatomic,strong) UILabel* giftDetails;

@property (nonatomic,strong) NSNumber* value;
@property (nonatomic,strong) NSString* giftType;

@property (nonatomic,strong) UIButton* termsBtn;

@property (nonatomic,strong) UIButton* redeemBtn;


-(void)createSubviews;
-(void)manuallyLayoutSubviews;

-(IBAction)onRedeemBtn:(id)sender;


-(NSDictionary*)setupGiftRequest;
-(void)updateDistance;

-(void) onLocationUpdate:(NSNotification*)notification;
-(void) onImageDownloaded:(NSNotification*)notification;
-(void) onRedeemGiftSuccess:(NSNotification*)notification;
-(void) onRedeemGiftError:(NSNotification*)notification;

-(void) onBarcodeGeneratedSuccess:(NSNotification*)notification;
-(void) onBarcodeGeneratedError:(NSNotification*)notification;


@end

@implementation RedeemGiftViewController



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
	// Do any additional setup after loading the view.
    self.title = NSLocalizedString(@"Redeem", nil);
    self.location = [self.gift.location anyObject];
    self.view.backgroundColor = [UIColor whiteColor];
        
    self.navigationItem.hidesBackButton = YES;
    self.navigationItem.leftBarButtonItem = [UIButton blackBackBtn:self];
    
    [self createSubviews];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

-(void)viewWillAppear:(BOOL)animated{
    [super viewWillAppear:animated];
    [[NSNotificationCenter defaultCenter]addObserver:self selector:@selector(onLocationUpdate:) name:kKikbakLocationUpdate object:nil];
    [[NSNotificationCenter defaultCenter]addObserver:self selector:@selector(onRedeemGiftSuccess:) name:kKikbakRedeemGiftSuccess object:nil];
    [[NSNotificationCenter defaultCenter]addObserver:self selector:@selector(onRedeemGiftError:) name:kKikbakRedeemGiftError object:nil];
    [[NSNotificationCenter defaultCenter]addObserver:self selector:@selector(onImageDownloaded:) name:kKikbakImageDownloaded object:nil];
    [[NSNotificationCenter defaultCenter]addObserver:self selector:@selector(onBarcodeGeneratedError:) name:kKikbakBarcodeError object:nil];
    [[NSNotificationCenter defaultCenter]addObserver:self selector:@selector(onBarcodeGeneratedSuccess:) name:kKikbakBarcodeSuccess object:nil];
    
    [self updateDistance];

    
    if(self.gift){
        NSString* imagePath = [ImagePersistor imageFileExists:self.gift.fbImageId imageType:UGC_GIVE_IMAGE_TYPE];
        if(imagePath != nil){
            self.giftImage.image = [[UIImage alloc]initWithContentsOfFile:imagePath];
        }
        
        imagePath = [ImagePersistor imageFileExists:self.gift.fbFriendId imageType:FRIEND_IMAGE_TYPE];
        if(imagePath != nil){
            self.friendImage.image = [[UIImage alloc]initWithContentsOfFile:imagePath];
        }
        
        self.friendName.text = self.gift.friendName;
        self.caption.text = self.gift.caption;
        self.retailerName.text = self.gift.merchantName;
    }
    
    [self manuallyLayoutSubviews];
}

-(void)viewDidAppear:(BOOL)animated{
    [super viewDidAppear:animated];

}

-(void)viewDidDisappear:(BOOL)animated{
    [[NSNotificationCenter defaultCenter]removeObserver:self name:kKikbakImageDownloaded object:nil];
    [[NSNotificationCenter defaultCenter]removeObserver:self name:kKikbakLocationUpdate object:nil];
    [[NSNotificationCenter defaultCenter]removeObserver:self name:kKikbakRedeemGiftSuccess object:nil];
    [[NSNotificationCenter defaultCenter]removeObserver:self name:kKikbakRedeemGiftError object:nil];
    [[NSNotificationCenter defaultCenter]removeObserver:self name:kKikbakBarcodeSuccess object:nil];
    [[NSNotificationCenter defaultCenter]removeObserver:self name:kKikbakBarcodeError object:nil];
    [super viewDidDisappear:animated];
}

-(void)viewDidLayoutSubviews{
//    [self manuallyLayoutSubviews];
}


-(void)createSubviews{

    self.giftImage = [[UIImageView alloc]initWithFrame:CGRectMake(0, 0, 320,250)];
    self.giftImage.image = [UIImage imageNamed:@"lg photo"];
    [self.view addSubview:self.giftImage];
    
    self.giftGradient = [[UIImageView alloc]initWithFrame:CGRectMake(0, 124, 320, 126)];
    self.giftGradient.image = [UIImage imageNamed:@"grd_bottom_img"];
    [self.view addSubview:self.giftGradient];

    
    self.retailerName = [[UILabel alloc]initWithFrame:CGRectMake(14, 183, 316, 26)];
    self.retailerName.font = [UIFont fontWithName:@"HelveticaNeue" size:24];
    self.retailerName.textColor = UIColorFromRGB(0xFFFFFF);
    self.retailerName.text = self.gift.merchantName;
    self.retailerName.textAlignment = NSTextAlignmentLeft;
    self.retailerName.backgroundColor = [UIColor clearColor];
    [self.view addSubview:self.retailerName];
    
    self.mapIcon = [[UIImageView alloc]initWithFrame:CGRectMake(14, 214, 10, 14)];
    self.mapIcon.image = [UIImage imageNamed:@"ic_map_give"];
    [self.view addSubview:self.mapIcon];
    
    self.distance = [[UILabel alloc] initWithFrame:CGRectMake(30, 212, 70, 18)];
    self.distance.font = [UIFont fontWithName:@"HelveticaNeue-Medium" size:18];
    self.distance.textColor = UIColorFromRGB(0xFFFFFF);
    self.distance.text = [NSString stringWithFormat:NSLocalizedString(@"miles away", nil),
                          [Distance distanceToInMiles:
                           [[CLLocation alloc]initWithLatitude:
                            self.location.latitude.doubleValue
                                                     longitude:self.location.longitude.doubleValue]]];
    self.distance.backgroundColor = [UIColor clearColor];
    [self.view addSubview:self.distance];
    
    self.mapBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    self.mapBtn.frame = CGRectMake(14, 210, 70, 30);
    self.mapBtn.backgroundColor = [UIColor clearColor];
    [self.mapBtn addTarget:self action:@selector(onMapBtn:) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:self.mapBtn];
    
    self.webIcon = [[UIImageView alloc]initWithFrame:CGRectMake(109, 211, 19, 19)];
    self.webIcon.image = [UIImage imageNamed:@"ic_web_give"];
    [self.view addSubview:self.webIcon];
    
    
    self.webBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    self.webBtn.backgroundColor = [UIColor clearColor];
    self.webBtn.frame = CGRectMake(99, 209, 30, 30);
    self.webBtn.backgroundColor = [UIColor clearColor];
    [self.webBtn addTarget:self action:@selector(onWebBtn:) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:self.webBtn];
    
    
    self.callIcon = [[UIImageView alloc]initWithFrame:CGRectMake(145, 211, 15, 18)];
    self.callIcon.image = [UIImage imageNamed:@"ic_phone_give"];
    [self.view addSubview: self.callIcon];
    
    self.callBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    self.callBtn.frame = CGRectMake(140, 209, 30, 30);
    self.callBtn.backgroundColor = [UIColor clearColor];
    [self.callBtn addTarget:self action:@selector(onCallBtn:) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:self.callBtn];
    
    self.friendBackground = [[UIView alloc]initWithFrame:CGRectMake(0, 250, 320, 95)];
    self.friendBackground.backgroundColor = [UIColor colorWithPatternImage:[UIImage imageNamed:@"bg_redeem_gift_friend"]];
    [self.view addSubview:self.friendBackground];
    
    self.seperator = [[UIImageView alloc]initWithFrame:CGRectMake(0, 341, 320, 4)];
    self.seperator.image = [UIImage imageNamed:@"separator_redeem_gift"];
    [self.view addSubview:self.seperator];
    
    self.giftImageDropShadow = [[UIImageView alloc]initWithFrame:CGRectMake(0, 250, 320, 4)];
    self.giftImageDropShadow.image = [UIImage imageNamed:@"grd_redeem_gift_dropshadow"];
    [self.view addSubview:self.giftImageDropShadow];

    
    self.friendImage = [[UIImageView alloc]initWithFrame:CGRectMake(11, 14, 44, 44)];
    self.friendImage.layer.cornerRadius = 5;
    self.friendImage.layer.masksToBounds = YES;
    self.friendImage.layer.borderWidth = 1;
    self.friendImage.layer.borderColor= [UIColorFromRGB(0xBABABA) CGColor];
    [self.friendBackground addSubview:self.friendImage];
    
    self.friendName = [[UILabel alloc]initWithFrame:CGRectMake(64, 14, 239, 18)];
    self.friendName.font = [UIFont fontWithName:@"HelveticaNeue-Bold" size:16];
    self.friendName.textColor = UIColorFromRGB(0x3a3a3a);
    self.friendName.text = @"Jessica Rabbit";
    self.friendName.backgroundColor = [UIColor clearColor];
    self.friendName.textAlignment = NSTextAlignmentLeft;
    [self.friendBackground addSubview:self.friendName];
    
    self.caption = [[UILabel alloc]initWithFrame:CGRectMake(64, 38, 228, 40)];
    self.caption.font = [UIFont fontWithName:@"HelveticaNeue" size:13];
    self.caption.textColor = UIColorFromRGB(0x7e7e7e);
    self.caption.text = @"This is a message from my friends it's telling me all about the product they think is cool";;
    self.caption.backgroundColor = [UIColor clearColor];
    self.caption.textAlignment = NSTextAlignmentLeft;
    self.caption.lineBreakMode = NSLineBreakByWordWrapping;
    self.caption.numberOfLines = 2;
    [self.friendBackground addSubview:self.caption];
    
    self.giftDescription = [[UILabel alloc]initWithFrame:CGRectMake(0, 361, 320, 34)];
    self.giftDescription.backgroundColor = [UIColor clearColor];
    self.giftDescription.textAlignment = NSTextAlignmentCenter;
    self.giftDescription.font = [UIFont fontWithName:@"HelveticaNeue-Bold" size:31];
    self.giftDescription.textColor = UIColorFromRGB(0x3a3a3a);
    self.giftDescription.text = self.gift.desc;
    [self.view addSubview:self.giftDescription];
    
    self.giftDetails = [[UILabel alloc]initWithFrame:CGRectMake(0, 398, 320, 14)];
    self.giftDetails.backgroundColor = [UIColor clearColor];
    self.giftDetails.textAlignment = NSTextAlignmentCenter;
    self.giftDetails.font = [UIFont fontWithName:@"HelveticaNeue" size:13];
    self.giftDetails.textColor = UIColorFromRGB(0x3a3a3a);
    self.giftDetails.text = self.gift.detailedDesc;
    [self.view addSubview:self.giftDetails];
    
    self.termsBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    self.termsBtn.frame = CGRectMake(11, 425, 150, 14);
    [self.termsBtn setTitle:NSLocalizedString(@"Terms and Conditions", nil) forState:UIControlStateNormal];
    self.termsBtn.titleLabel.font = [UIFont fontWithName:@"HelveticaNeue-Light" size:12];
    [self.termsBtn setTitleColor:UIColorFromRGB(0x686868) forState:UIControlStateNormal];
    self.termsBtn.contentHorizontalAlignment = UIControlContentHorizontalAlignmentLeft;
    [self.termsBtn addTarget:self action:@selector(onTermsBtn:) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:self.termsBtn];
    
    
    self.redeemBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    [self.redeemBtn setBackgroundImage:[UIImage imageNamed:@"btn_blue"] forState:UIControlStateNormal];
    [self.redeemBtn setTitle:NSLocalizedString(@"Redeem in Store", nil) forState:UIControlStateNormal];
    [self.redeemBtn setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
    self.redeemBtn.titleLabel.font = [UIFont fontWithName:@"HelveticaNeue-Medium" size:15];
    self.redeemBtn.frame = CGRectMake(11, 453, 298, 40);
    [self.redeemBtn addTarget:self action:@selector(onRedeemBtn:) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:self.redeemBtn];
}

-(void)manuallyLayoutSubviews{
    if (![UIDevice hasFourInchDisplay]) {
        self.giftImage.frame = CGRectMake(0, 0, 320,188);
        self.giftGradient.frame = CGRectMake(0, 82, 320, 106);
        self.retailerName.frame = CGRectMake(14, 128, 316, 26);
        self.mapIcon.frame = CGRectMake(14, 159, 10, 14);
        self.distance.frame = CGRectMake(30, 157, 70, 18);
        self.mapBtn.frame = CGRectMake(14, 155, 70, 30);
        self.webIcon.frame = CGRectMake(109, 156, 19, 19);
        self.webBtn.frame = CGRectMake(99, 154, 30, 30);
        self.callIcon.frame = CGRectMake(145, 156, 15, 18);
        self.callBtn.frame = CGRectMake(140, 154, 30, 30);
        self.giftImageDropShadow.frame = CGRectMake(0, 188, 320, 4);
        self.friendBackground.frame = CGRectMake(0, 188, 320, 88);
        self.seperator.frame = CGRectMake(0, 272, 320, 4);
        self.friendImage.frame = CGRectMake(11, 11, 44, 44);
        self.friendName.frame = CGRectMake(64, 11, 239, 18);
        self.caption.frame = CGRectMake(64, 34, 228, 40);
        self.giftDescription.frame = CGRectMake(0, 282, 320, 34);
        self.giftDetails.frame = CGRectMake(0, 318, 320, 14);
        self.termsBtn.frame = CGRectMake(11, 343, 150, 14);
        self.redeemBtn.frame = CGRectMake(11, 365, 298, 40);
    }
}

-(IBAction)onRedeemBtn:(id)sender{

//    RedeemGiftSuccessViewController* vc = [[RedeemGiftSuccessViewController alloc]init];
//    vc.gift = self.gift;
//    [self.navigationController pushViewController:vc animated:YES];
    
//    ZXingWidgetController *widController = [[ZXingWidgetController alloc] initWithDelegate:self showCancel:YES OneDMode:NO];
//    
//    NSMutableSet *readers = [[NSMutableSet alloc ] init];
//    QRCodeReader* qrcodeReader = [[QRCodeReader alloc] init];
//    [readers addObject:qrcodeReader];
//    
//    widController.readers = readers;
//    [self presentViewController:widController animated:YES completion:nil];
//    if(self.reward.kikbak != nil){
//        RedeemKikbakRequest* rkr = [[RedeemKikbakRequest alloc]init];
//        rkr.kikbak = self.reward.kikbak;
//        [rkr restRequest:[self setupKikbakRequest]];
//    }
    
    self.value = self.gift.value;
    self.giftType = self.gift.discountType;
    
    if(self.gift != nil){
//        RedeemGiftRequest *request = [[RedeemGiftRequest alloc]init];
//        request.gift = self.gift;
//        [request restRequest:[self setupGiftRequest]];
        BarcodeImageRequest* request = [[BarcodeImageRequest alloc]init];
        request.allocatedGiftId = self.gift.giftId;
        [request requestBarcode];
    }
}

-(void)onTermsBtn:(id)sender{
    CGRect frame = ((AppDelegate*)[UIApplication sharedApplication].delegate).window.frame;
    TermsAndConditionsView* view = [[TermsAndConditionsView alloc]initWithFrame:frame];
    view.tosUrl = self.gift.tosUrl;
    [view manuallyLayoutSubviews];
    [((AppDelegate*)[UIApplication sharedApplication].delegate).window addSubview:view];
}


- (void)zxingController:(ZXingWidgetController*)controller didScanResult:(NSString *)result {
    [self dismissViewControllerAnimated:NO completion:nil];
    
    CGRect frame = ((AppDelegate*)[UIApplication sharedApplication].delegate).window.frame;
    RedeemView* redeemView = [[RedeemView alloc]initWithFrame:frame];
    [redeemView manuallyLayoutSubviews];
    [((AppDelegate*)[UIApplication sharedApplication].delegate).window addSubview:redeemView];
    
    RedeemGiftRequest *request = [[RedeemGiftRequest alloc]init];
    NSMutableDictionary* dict = [[NSMutableDictionary alloc]initWithCapacity:3];
    [dict setObject:self.gift.giftId forKey:@"id"];
    Location* location = [self.gift.location anyObject];
    [dict setObject:location.locationId forKey:@"locationId"];
    [dict setObject:self.gift.friendUserId forKey:@"friendId"];
    [dict setObject:@"zdfdw" forKey:@"verificationCode"];
    [request restRequest:dict];
    
    self.gift.location = nil;
    NSManagedObjectContext* context = ((AppDelegate*)[UIApplication sharedApplication].delegate).managedObjectContext;
    [context deleteObject:self.gift];
    
    NSError* error;
    [context save:&error];
    
    if(error){
        NSLog(@"Unresolved error %@, %@", error, [error userInfo]);
    }
    
    self.gift = nil;
}

- (void)zxingControllerDidCancel:(ZXingWidgetController*)controller {
    [self dismissViewControllerAnimated:YES completion:nil];
    
    RedeemGiftRequest *request = [[RedeemGiftRequest alloc]init];
    [request restRequest:[self setupGiftRequest]];
    
    self.gift.location = nil;
    NSManagedObjectContext* context = ((AppDelegate*)[UIApplication sharedApplication].delegate).managedObjectContext;
    [context deleteObject:self.gift];
    
    NSError* error;
    [context save:&error];
    
    if(error){
        NSLog(@"Unresolved error %@, %@", error, [error userInfo]);
    }
    
    self.gift = nil;
}

-(NSDictionary*)setupGiftRequest{
    NSMutableDictionary* dict = [[NSMutableDictionary alloc]initWithCapacity:3];
    [dict setObject:self.gift.giftId forKey:@"id"];
    Location* location = [self.gift.location anyObject];
    [dict setObject:location.locationId forKey:@"locationId"];
    [dict setObject:self.gift.friendUserId forKey:@"friendUserId"];
    [dict setObject:@"fwttt" forKey:@"verificationCode"];
    
    return dict;
}


-(void)updateDistance{

    //todo move to a function
    distanceToLocation = [Distance distanceToInFeet:[[CLLocation alloc]initWithLatitude:self.location.latitude.doubleValue
                                                                              longitude:self.location.longitude.doubleValue]];
}

#pragma mark - NSNotification
-(void) onLocationUpdate:(NSNotification*)notification{
    
}

-(void) onImageDownloaded:(NSNotification*)notification{
    
}

-(void) onRedeemGiftSuccess:(NSNotification*)notification{
    RedeemGiftSuccessViewController* vc = [[RedeemGiftSuccessViewController alloc]init];
    vc.validationCode = [notification object];
    vc.merchantName = self.retailerName.text;
    vc.value = self.value;
    vc.giftType = self.giftType;
    vc.optionalDesc = self.giftDetails.text;
    [self.navigationController pushViewController:vc animated:YES];

}

-(void) onRedeemGiftError:(NSNotification*)notification{
    
}

-(void) onBarcodeGeneratedSuccess:(NSNotification*)notification{
    RedeemGiftSuccessViewController* vc = [[RedeemGiftSuccessViewController alloc]init];
    vc.merchantName = self.retailerName.text;
    vc.value = self.value;
    vc.giftType = self.giftType;
    vc.validationType = self.gift.validationType;
    vc.optionalDesc = self.giftDetails.text;
    vc.imagePath = [[notification object] objectForKey:@"imagePath"];
    vc.validationCode = [[notification object] objectForKey:@"barcode"];
    [self.navigationController pushViewController:vc animated:YES];
    
}

-(void) onBarcodeGeneratedError:(NSNotification*)notification{
    
}


#pragma mark - on back btn
-(IBAction)onBackBtn:(id)sender{
    [self.navigationController popViewControllerAnimated:YES];
}


@end
