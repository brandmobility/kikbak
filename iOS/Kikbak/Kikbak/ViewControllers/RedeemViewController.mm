//
//  RedeemViewController.m
//  kikit
//
//  Created by Ian Barile on 3/9/13.
//  Copyright (c) 2013 Ian Barile. All rights reserved.
//

#import "RedeemViewController.h"
#import "UIDevice+Screen.h"
#import "RedeemView.h"
#import "AppDelegate.h"
#import "QRCodeReader.h"
#import "Gift.h"
#import "Kikbak.h"
#import "Location.h"
#import "RedeemGiftRequest.h"
#import "RedeemKikbakRequest.h"
#import "RewardCollection.h"
#import "Distance.h"
#import "NotificationContstants.h"
#import "ImagePersistor.h"
#import "util.h"
#import <QuartzCore/QuartzCore.h>



@interface RedeemViewController (){
    double distanceToLocation;
}

@property (nonatomic,strong) UIView* offerBackground;
@property (nonatomic,strong) UIImageView* navDropShadow;
@property (nonatomic,strong) UIImageView* backgroundDropShadow;
@property (nonatomic,strong) UIImageView* friendBorder;
@property (nonatomic,strong) UIImageView* friendImage;
@property (nonatomic,strong) UILabel* friendName;
@property (nonatomic,strong) UILabel* friendMessage;
@property (nonatomic,strong) UIView* imageFrame;
@property (nonatomic,strong) UIImageView* giftImage;
@property (nonatomic,strong) UIImageView* ribbonBack;
@property (nonatomic,strong) UIImageView* ribbonFront;
@property (nonatomic,strong) UILabel* retailerName;
@property (nonatomic,strong) UIImageView* mapIcon;
@property (nonatomic,strong) UILabel* distance;
@property (nonatomic,strong) UIImageView* callIcon;
@property (nonatomic,strong) UILabel* call;
@property (nonatomic,strong) UIImageView* webIcon;
@property (nonatomic,strong) UILabel* web;
@property (nonatomic,strong) UILabel* offer;
@property (nonatomic,strong) UILabel* details;
@property (nonatomic,strong) UIButton* termsBtn;
@property (nonatomic,strong) UIButton* learnBtn;

@property (nonatomic,strong) UIButton* redeemBtn;


-(void)createSubviews;
-(void)manuallyLayoutSubviews;

-(NSDictionary*)setupKikbakRequest;
-(NSDictionary*)setupGiftRequest;
-(void)updateDistance;

-(void) onLocationUpdate:(NSNotification*)notification;
-(void) onRedeemGiftSuccess:(NSNotification*)notification;
-(void) onRedeemGiftError:(NSNotification*)notification;
-(void) onRedeemKikbakSuccess:(NSNotification*)notification;
-(void) onRedeemKikbakError:(NSNotification*)notification;
@end

@implementation RedeemViewController



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
    [[NSNotificationCenter defaultCenter]addObserver:self selector:@selector(onRedeemKikbakSuccess:) name:kKikbakRedeemKikbakSuccess object:nil];
    [[NSNotificationCenter defaultCenter]addObserver:self selector:@selector(onRedeemKikbakError:) name:kKikbakRedeemKikbakError object:nil];
    [[NSNotificationCenter defaultCenter]addObserver:self selector:@selector(onOfferUpdate:) name:kKikbakImageDownloaded object:nil];
    
    [self updateDistance];
    
    if(self.reward.gift){
        NSString* imagePath = [ImagePersistor imageFileExists:self.reward.gift.fbImageId imageType:GIVE_IMAGE_TYPE];
        if(imagePath != nil){
            self.giftImage.image = [[UIImage alloc]initWithContentsOfFile:imagePath];
        }
        
        imagePath = [ImagePersistor imageFileExists:self.reward.gift.fbFriendId imageType:FRIEND_IMAGE_TYPE];
        if(imagePath != nil){
            self.friendImage.image = [[UIImage alloc]initWithContentsOfFile:imagePath];
        }
    }
}

-(void)viewDidAppear:(BOOL)animated{
    [super viewDidAppear:animated];

    [self manuallyLayoutSubviews];
}

-(void)viewDidDisappear:(BOOL)animated{
    [[NSNotificationCenter defaultCenter]removeObserver:self name:kKikbakImageDownloaded object:nil];
    [[NSNotificationCenter defaultCenter]removeObserver:self name:kKikbakLocationUpdate object:nil];
    [[NSNotificationCenter defaultCenter]removeObserver:self name:kKikbakRedeemGiftSuccess object:nil];
    [[NSNotificationCenter defaultCenter]removeObserver:self name:kKikbakRedeemGiftError object:nil];
    [[NSNotificationCenter defaultCenter]removeObserver:self name:kKikbakRedeemKikbakSuccess object:nil];
    [[NSNotificationCenter defaultCenter]removeObserver:self name:kKikbakRedeemKikbakError object:nil];
    [super viewDidDisappear:animated];
}

-(void)viewDidLayoutSubviews{
    [self manuallyLayoutSubviews];
}


-(void)createSubviews{
    self.view.backgroundColor = UIColorFromRGB(0xf5f5f5);
    
    self.offerBackground = [[UIView alloc]initWithFrame:CGRectMake(0,0, 320, 442)];
    self.offerBackground.backgroundColor = UIColorFromRGB(0xF0F0F0);
    [self.view addSubview:self.offerBackground];
    
    self.navDropShadow = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"navbar_dropshadow"]];
    self.navDropShadow.frame = CGRectMake(0, 0, 320, 3);
    [self.view addSubview:self.navDropShadow];
    
    self.backgroundDropShadow = [[UIImageView alloc]initWithImage:[UIImage imageNamed:@"navbar_dropshadow"]];
    self.backgroundDropShadow.frame = CGRectMake(0, 442, 320, 6);
    [self.view addSubview:self.backgroundDropShadow];
    
    UIImage* friendBorderImage = [UIImage imageNamed:@"profile_pic_overlay"];
    self.friendBorder = [[UIImageView alloc]initWithImage:friendBorderImage];
    self.friendBorder.frame = CGRectMake(11, 16, friendBorderImage.size.width, friendBorderImage.size.height);
    [self.offerBackground addSubview:self.friendBorder];
    
    self.friendImage = [[UIImageView alloc]initWithFrame:CGRectMake(12, 17,
                                                                   friendBorderImage.size.width-2,
                                                                   friendBorderImage.size.height-2)];
    self.friendImage.layer.cornerRadius = 7;
    self.friendImage.layer.masksToBounds = YES;
    [self.offerBackground addSubview:self.friendImage];
    
    self.friendName = [[UILabel alloc]initWithFrame:CGRectMake(81, 19, 239, 18)];
    self.friendName.font = [UIFont fontWithName:@"HelveticaNeue-Light" size:16];
    self.friendName.textColor = UIColorFromRGB(0x3a3a3a);
    self.friendName.text = @"Jessica Rabbit";
    self.friendName.backgroundColor = [UIColor clearColor];
    self.friendName.textAlignment = NSTextAlignmentLeft;
    [self.offerBackground addSubview:self.friendName];
    
    self.friendMessage = [[UILabel alloc]initWithFrame:CGRectMake(81, 38, 228, 40)];
    self.friendMessage.font = [UIFont fontWithName:@"HelveticaNeue" size:14];
    self.friendMessage.textColor = UIColorFromRGB(0x3a3a3a);
    self.friendMessage.text = @"This is a message from my friends it's telling me all about the product they think is cool";;
    self.friendMessage.backgroundColor = [UIColor clearColor];
    self.friendMessage.textAlignment = NSTextAlignmentLeft;
    self.friendMessage.lineBreakMode = NSLineBreakByWordWrapping;
    self.friendMessage.numberOfLines = 2;
    [self.offerBackground addSubview:self.friendMessage];
    
    
    self.imageFrame = [[UIView alloc]initWithFrame:CGRectMake(35, 90, 250, 250)];
    self.imageFrame.backgroundColor = [UIColor whiteColor];
    self.imageFrame.layer.shadowColor = [UIColor blackColor].CGColor;
    self.imageFrame.layer.shadowOpacity = 0.4;
    self.imageFrame.layer.shadowOffset = CGSizeMake(1, 1);
    [self.offerBackground addSubview:self.imageFrame];
    
    self.giftImage = [[UIImageView alloc]initWithFrame:CGRectMake(45, 100, 230, 230)];
    self.giftImage.backgroundColor = [UIColor greenColor];
    [self.offerBackground addSubview:self.giftImage];
    
    UIImage* rbBack = [UIImage imageNamed:@"ribbon_back"];
    self.ribbonBack = [[UIImageView alloc]initWithImage:rbBack];
    self.ribbonBack.frame = CGRectMake(11, 256, rbBack.size.width, rbBack.size.height);
    [self.offerBackground addSubview:self.ribbonBack];
    
    UIImage* rbFront = [UIImage imageNamed:@"ribbon_front"];
    self.ribbonFront = [[UIImageView alloc]initWithImage:rbFront];
    self.ribbonFront.frame = CGRectMake(11, 256, rbFront.size.width, rbFront.size.height);
    [self.offerBackground addSubview:self.ribbonFront];
    
    
    self.retailerName = [[UILabel alloc]initWithFrame:CGRectMake(11+rbBack.size.width+13, 260, rbFront.size.width, 22)];
    self.retailerName.font = [UIFont fontWithName:@"HelveticaNeue-Light" size:20];
    self.retailerName.textColor = UIColorFromRGB(0xf5f5f5);
    self.retailerName.text = NSLocalizedString(@"Test Retailer", nil);
    self.retailerName.backgroundColor = [UIColor clearColor];
    [self.offerBackground addSubview:self.retailerName];
    
    self.mapIcon = [[UIImageView alloc]initWithFrame:CGRectMake(43, 289, 13, 12)];
    self.mapIcon.image = [UIImage imageNamed:@"map_marker"];
    [self.offerBackground addSubview:self.mapIcon];
    
    self.distance = [[UILabel alloc] initWithFrame:CGRectMake(57, 289, 50, 13)];
    self.distance.font = [UIFont fontWithName:@"HelveticaNeue-Light" size:12];
    self.distance.textColor = UIColorFromRGB(0xf5f5f5);
    self.distance.text = @"test";
//    self.distance.text = [NSString stringWithFormat:NSLocalizedString(@"miles away", nil),
//                          [Distance distanceToInMiles:
//                           [[CLLocation alloc]initWithLatitude:
//                            self.location.latitude.doubleValue
//                                                     longitude:self.location.longitude.doubleValue]]];
    self.distance.backgroundColor = [UIColor clearColor];
    [self.offerBackground addSubview:self.distance];
    
    self.callIcon = [[UIImageView alloc]initWithFrame:CGRectMake(122, 289, 13, 12)];
    self.callIcon.image = [UIImage imageNamed:@"phone_icon"];
    [self.offerBackground addSubview:self.callIcon];
    
    self.call = [[UILabel alloc]initWithFrame:CGRectMake(135, 289, 20, 13)];
    self.call.font = [UIFont fontWithName:@"HelveticaNeue-Light" size:12];
    self.call.textColor = UIColorFromRGB(0xf5f5f5);
    self.call.text = NSLocalizedString(@"call", nil);
    self.call.backgroundColor = [UIColor clearColor];
    [self.offerBackground addSubview:self.call];
    
    self.webIcon = [[UIImageView alloc]initWithFrame:CGRectMake(199, 289, 12, 12)];
    self.webIcon.image = [UIImage imageNamed:@"web_icon"];
    [self.offerBackground addSubview:self.webIcon];
    
    self.web = [[UILabel alloc]initWithFrame:CGRectMake(215, 289, 30, 13)];
    self.web.font = [UIFont fontWithName:@"HelveticaNeue-Light" size:12];
    self.web.textColor = UIColorFromRGB(0xf5f5f5);
    self.web.text = NSLocalizedString(@"web", nil);
    self.web.backgroundColor = [UIColor clearColor];
    [self.offerBackground addSubview:self.web];
    
    self.offer = [[UILabel alloc]initWithFrame:CGRectMake(0, 344, 320, 54)];
    self.offer.font = [UIFont fontWithName:@"HelveticaNeue-Bold" size:50];
    self.offer.textColor = UIColorFromRGB(0x3a3a3a);
    self.offer.text = @"$50 off";
    self.offer.textAlignment = NSTextAlignmentCenter;
    self.offer.backgroundColor = [UIColor clearColor];
    [self.offerBackground addSubview:self.offer];
    
    self.details = [[UILabel alloc] initWithFrame:CGRectMake(0, 394, 320, 20)];
    self.details.font = [UIFont fontWithName:@"HelveticaNeue" size:18];
    self.details.textColor = UIColorFromRGB(0x3a3a3a);
    self.details.text = @"with 1 year contract";
    self.details.textAlignment = NSTextAlignmentCenter;
    self.details.backgroundColor = [UIColor clearColor];
    [self.offerBackground addSubview:self.details];
    
    self.termsBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    self.termsBtn.frame = CGRectMake(10, 424, 150, 14);
    [self.termsBtn setTitle:NSLocalizedString(@"Terms and Conditions", nil) forState:UIControlStateNormal];
    self.termsBtn.titleLabel.font = [UIFont fontWithName:@"HelveticaNeue-Light" size:12];
    [self.termsBtn setTitleColor:UIColorFromRGB(0x686868) forState:UIControlStateNormal];
    self.termsBtn.titleLabel.textAlignment = NSTextAlignmentLeft;
    self.termsBtn.contentHorizontalAlignment = UIControlContentHorizontalAlignmentLeft;
    [self.offerBackground addSubview:self.termsBtn];
    
    self.learnBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    self.learnBtn.frame = CGRectMake(160, 424, 150, 14);
    [self.learnBtn setTitle:NSLocalizedString(@"Learn More", nil) forState:UIControlStateNormal];
    self.learnBtn.titleLabel.font = [UIFont fontWithName:@"HelveticaNeue-Light" size:12];
    [self.learnBtn setTitleColor:UIColorFromRGB(0x686868) forState:UIControlStateNormal];
    self.learnBtn.titleLabel.textAlignment = NSTextAlignmentRight;
    self.learnBtn.contentHorizontalAlignment = UIControlContentHorizontalAlignmentRight;
    [self.offerBackground addSubview:self.learnBtn];
    
    
    self.redeemBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    [self.redeemBtn setBackgroundImage:[UIImage imageNamed:@"give_bkg_button"] forState:UIControlStateNormal];
    [self.redeemBtn setTitle:NSLocalizedString(@"Redeem", nil) forState:UIControlStateNormal];
    [self.redeemBtn setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
    self.redeemBtn.titleLabel.font = [UIFont fontWithName:@"HelveticaNeue-Medium" size:15];
    self.redeemBtn.frame = CGRectMake(10, 453, 300, 44);
    [self.redeemBtn addTarget:self action:@selector(onGiveGift:) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:self.redeemBtn];
}

-(void)manuallyLayoutSubviews{
    if (![UIDevice hasFourInchDisplay]) {
        self.offerBackground.frame = CGRectMake(0, 0, 320, 351);
        self.backgroundDropShadow.frame = CGRectMake(0, 351, 320, 6);
        self.friendBorder.frame = CGRectMake(11, 9, 64, 64);
        self.friendImage.frame = CGRectMake(81, 14, 239, 18);
        self.friendName.frame = CGRectMake(81, 14, 239, 18);
        self.friendMessage.frame = CGRectMake(81, 33, 228, 40);
        self.imageFrame.frame = CGRectMake(35, 79, 250, 198);
        self.giftImage.frame = CGRectMake(45, 89, 230, 178);
        self.ribbonBack.frame = CGRectMake(11, 200,24,63);
        self.ribbonFront.frame = CGRectMake(11, 200, 247, 55);
        self.retailerName.frame = CGRectMake(43, 204, 247, 22);
        self.mapIcon.frame = CGRectMake(43, 233, 13, 12);
        self.distance.frame = CGRectMake(57, 233, 50, 12);
        self.callIcon.frame = CGRectMake(122, 233, 13, 12);
        self.call.frame = CGRectMake(135, 233, 20, 12);
        self.webIcon.frame = CGRectMake(199, 233, 13, 12);
        self.web.frame = CGRectMake(215, 233, 30, 12);
        self.offer.frame = CGRectMake(0, 282, 320, 36);
        self.offer.font = [UIFont fontWithName:@"HelveticaNeue-Bold" size:35];
        self.details.frame = CGRectMake(0, 314, 320, 20);
        self.details.font = [UIFont fontWithName:@"HelveticaNeue" size:14];
        self.termsBtn.frame = CGRectMake(10, 335, 150, 14);
        self.learnBtn.frame = CGRectMake(160, 335, 150, 14);
        self.redeemBtn.frame = CGRectMake(10, 362, 300, 44);
        
    }
}

-(void)onRedeem:(id)sender{

//    ZXingWidgetController *widController = [[ZXingWidgetController alloc] initWithDelegate:self showCancel:YES OneDMode:NO];
//    
//    NSMutableSet *readers = [[NSMutableSet alloc ] init];
//    QRCodeReader* qrcodeReader = [[QRCodeReader alloc] init];
//    [readers addObject:qrcodeReader];
//    
//    widController.readers = readers;
//    [self presentViewController:widController animated:YES completion:nil];
    if(self.reward.kikbak != nil){
        RedeemKikbakRequest* rkr = [[RedeemKikbakRequest alloc]init];
        rkr.kikbak = self.reward.kikbak;
        [rkr restRequest:[self setupKikbakRequest]];
    }
    if(self.reward.gift != nil){
        RedeemGiftRequest *request = [[RedeemGiftRequest alloc]init];
        request.gift = self.reward.gift;
        [request restRequest:[self setupGiftRequest]];
    }
}

-(void)onTermsAndConditions:(id)sender{
    
}


- (void)zxingController:(ZXingWidgetController*)controller didScanResult:(NSString *)result {
    [self dismissViewControllerAnimated:NO completion:nil];
    
    CGRect frame = ((AppDelegate*)[UIApplication sharedApplication].delegate).window.frame;
    RedeemView* redeemView = [[RedeemView alloc]initWithFrame:frame];
    [redeemView manuallyLayoutSubviews];
    [((AppDelegate*)[UIApplication sharedApplication].delegate).window addSubview:redeemView];
    
    RedeemGiftRequest *request = [[RedeemGiftRequest alloc]init];
    NSMutableDictionary* dict = [[NSMutableDictionary alloc]initWithCapacity:3];
    [dict setObject:self.reward.gift.giftId forKey:@"id"];
    Location* location = [self.reward.gift.location anyObject];
    [dict setObject:location.locationId forKey:@"locationId"];
    [dict setObject:self.reward.gift.friendUserId forKey:@"friendId"];
    [dict setObject:@"zdfdw" forKey:@"verificationCode"];
    [request restRequest:dict];
    
    self.reward.gift.location = nil;
    NSManagedObjectContext* context = ((AppDelegate*)[UIApplication sharedApplication].delegate).managedObjectContext;
    [context deleteObject:self.reward.gift];
    
    NSError* error;
    [context save:&error];
    
    if(error){
        NSLog(@"Unresolved error %@, %@", error, [error userInfo]);
    }
    
    self.reward.gift = nil;
}

- (void)zxingControllerDidCancel:(ZXingWidgetController*)controller {
    [self dismissViewControllerAnimated:YES completion:nil];
    
    RedeemGiftRequest *request = [[RedeemGiftRequest alloc]init];
    [request restRequest:[self setupGiftRequest]];
    
    self.reward.gift.location = nil;
    NSManagedObjectContext* context = ((AppDelegate*)[UIApplication sharedApplication].delegate).managedObjectContext;
    [context deleteObject:self.reward.gift];
    
    NSError* error;
    [context save:&error];
    
    if(error){
        NSLog(@"Unresolved error %@, %@", error, [error userInfo]);
    }
    
    self.reward.gift = nil;
}

-(NSDictionary*)setupKikbakRequest{
    NSMutableDictionary* dict = [[NSMutableDictionary alloc]initWithCapacity:4];
    [dict setObject:self.reward.kikbak.kikbakId forKey:@"id"];
    Location* location = [self.reward.kikbak.location anyObject];
    [dict setObject:location.locationId forKey:@"locationId"];
    [dict setObject:self.reward.kikbak.value forKey:@"amount"];
    [dict setObject:@"zdfdw" forKey:@"verificationCode"];
    
    return dict;
}

-(NSDictionary*)setupGiftRequest{
    NSMutableDictionary* dict = [[NSMutableDictionary alloc]initWithCapacity:3];
    [dict setObject:self.reward.gift.giftId forKey:@"id"];
    Location* location = [self.reward.gift.location anyObject];
    [dict setObject:location.locationId forKey:@"locationId"];
    [dict setObject:self.reward.gift.friendUserId forKey:@"friendUserId"];
    [dict setObject:@"zdfdw" forKey:@"verificationCode"];
    
    return dict;
}


-(void)updateDistance{

    //todo move to a function
    Location* location;
    if(self.reward.kikbak){
        location = [self.reward.gift.location anyObject];
    }
    else if( self.reward.gift){
        location = [self.reward.gift.location anyObject];
    }
    
    distanceToLocation = [Distance distanceToInFeet:[[CLLocation alloc]initWithLatitude:location.latitude.doubleValue longitude:location.longitude.doubleValue]];
}

#pragma mark - NSNotification
-(void) onLocationUpdate:(NSNotification*)notification{
    
}

-(void) onRedeemGiftSuccess:(NSNotification*)notification{
    
}

-(void) onRedeemGiftError:(NSNotification*)notification{
    
}

-(void) onRedeemKikbakSuccess:(NSNotification*)notification{
    
}

-(void) onRedeemKikbakError:(NSNotification*)notification{
    
}


@end
