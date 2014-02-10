//
//  ShareViewController.m
//  kikit
//
//  Created by Ian Barile on 3/15/13.
//  Copyright (c) 2013 Ian Barile. All rights reserved.
//

#import "GiveViewController.h"
#import "FBConstants.h"
#import "UIDevice+Screen.h"
#import "AppDelegate.h"
#import <QuartzCore/QuartzCore.h>
#import "Flurry.h"
#import "ShareSuccessView.h"
#import "UIImage+Manipulate.h"
#import "ShareExperienceRequest.h"
#import "Offer.h"
#import "Location.h"
#import "Distance.h"
#import "ImagePersistor.h"
#import "Util.h"
#import "KikbakOpenGraphProtocols.h"
#import "SpinnerView.h"
#import "NotificationContstants.h"
#import "TermsAndConditionsView.h"
#import "UIButton+Util.h"
#import "ImageUploadRequest.h"
#import "FBCouponObject.h"
#import "ShareResult.h"
#import "UIDevice+OSVersion.h"
#import "Flurry.h"
#import "TwitterApi.h"
#import "LocationManager.h"


#define DEFAULT_CONTAINER_VIEW_HEIGHT 50
#define PHOTO_TAG  1000
#define CAPTION_TAG  1001
#define CALL_URL_TAG 1002

const double TEXT_EDIT_CONTAINER_ORIGIN_Y = 243.0;
const double TEXT_EDIT_CONTAINER_ORIGIN_Y_35_SCREEN = 170.0;

static int offsetForIOS6 = 44;

@interface GiveViewController (){
    BOOL shareViaEmail;
    BOOL shareViaSMS;
    BOOL shareViaTwitter;
    BOOL photoTaken;
    BOOL adjustTextview;
}

@property (nonatomic, strong) Location* location;

@property(nonatomic, strong)SpinnerView* spinnerView;

@property(nonatomic, strong) UIImageView* giveImage;
@property(nonatomic, strong) UIImageView* imageOverlay;
@property(nonatomic, strong) UILabel* takePhoto;
@property(nonatomic, strong) UIButton* takePictureBtn;


@property(nonatomic, strong) UILabel* retailerName;
@property(nonatomic, strong) UIImageView* mapIcon;
@property(nonatomic, strong) UILabel* distance;
@property(nonatomic, strong) UIButton* mapBtn;
@property(nonatomic, strong) UIImageView* callIcon;
@property(nonatomic, strong) UIButton* callBtn;
@property(nonatomic, strong) UIImageView* webIcon;
@property(nonatomic, strong) UIButton* webBtn;


@property(nonatomic, strong) UIView* captionContainerView;
@property(nonatomic, strong) UIImageView* captionGradient;
@property(nonatomic, strong) HPGrowingTextView* captionTextView;

@property(nonatomic, strong) UIImageView* dottedSeperator;

@property(nonatomic, strong) UIImageView* giftIcon;
@property(nonatomic, strong) UILabel* giftDesctription;
@property(nonatomic, strong) UILabel* giftDetailedDescription;
@property(nonatomic, strong) UIImageView* seperator;
@property(nonatomic, strong) UIImageView* rewardIcon;
@property(nonatomic, strong) UILabel* rewardDescription;
@property(nonatomic, strong) UILabel* rewardDetailedDescription;
@property(nonatomic, strong) UIButton* termsBtn;

@property(nonatomic, strong) UIButton* giveBtn;

@property(nonatomic, strong) NSString* imageUrl;

@property(nonatomic, strong) NSString* employeeName;
@property(nonatomic, strong) NSNumber* chosenlocation;

@property(nonatomic, strong) UIImage* imageToPost;


-(void)createSubviews;
-(void)manuallyLayoutSubviews;
-(void)adjustRetailerInfo:(CGFloat)delta;

-(IBAction)onTakePhotoBtn:(id)sender;
-(IBAction)onGiveGift:(id)sender;
-(IBAction)onTerms:(id)sender;

-(IBAction)onBackBtn:(id)sender;
-(IBAction)onMapBtn:(id)sender;
-(IBAction)onCallBtn:(id)sender;
-(IBAction)onWebBtn:(id)sender;

-(void) onShareSuccess:(NSNotification*)notification;
-(void) onShareError:(NSNotification*)notification;
-(void) onImageUploadSuccess:(NSNotification*)notification;
-(void) onImageUploadError:(NSNotification*)notification;
-(void) onLocationUpdate:(NSNotification*)notification;
-(void) onFBStoryPostSuccess:(NSNotification*)notification;
-(void) onFBStoryPostError:(NSNotification *)notification;
-(void) onTwitterPostSuccess:(NSNotification*)notification;
-(void) onTwitterPostError:(NSNotification*)notification;

-(void) updateDisance;

-(IBAction)keyboardWillShow:(NSNotification*)notification;
-(IBAction)keyboardWillHide:(NSNotification*)notification;

@end

@implementation GiveViewController

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
    self.title = NSLocalizedString(@"Give_tab_bar", nil);
	// Do any additional setup after loading the view.
    shareViaEmail = NO;
    shareViaSMS = NO;
    shareViaTwitter = NO;
    photoTaken = NO;
    adjustTextview = NO;
    
    if (self.offer.location.count > 0) {
        self.location = [self.offer.location anyObject];
        for( Location* location in self.offer.location){
            CLLocation* current = [[CLLocation alloc]initWithLatitude:[self.location.latitude doubleValue] longitude:[self.location.longitude doubleValue]];
            CLLocation* next = [[CLLocation alloc]initWithLatitude:[location.latitude doubleValue] longitude:[location.longitude doubleValue]];
            if ([Distance distanceToInFeet:current] > [Distance distanceToInFeet:next]) {
                self.location = location;
            }
        }
    }
    
    if( [UIDevice osVersion7orGreater] ){
        self.edgesForExtendedLayout = UIRectEdgeNone;
        offsetForIOS6 = 0;
    }
    
    
    [self createSubviews];
    [self manuallyLayoutSubviews];
    [self updateDisance];
    
    
    NSString* imagePath = [ImagePersistor imageFileExists:self.offer.merchantId imageType:DEFAULT_MERCHANT_IMAGE_TYPE];
    if(imagePath != nil){
        self.giveImage.image = self.imageToPost = [[UIImage alloc]initWithContentsOfFile:imagePath];
    }
    
    if( ![UIDevice hasFourInchDisplay] ){
        CGRect retina35CropRect = CGRectMake(0, 74, 640, self.giveImage.image.size.height-148);
        self.giveImage.image = [self.imageToPost imageCropToRect:retina35CropRect];
    }
        
    self.navigationItem.hidesBackButton = YES;
    self.navigationItem.leftBarButtonItem = [UIButton blackBackBtn:self];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

-(void)viewWillAppear:(BOOL)animated{
    [super viewWillAppear:animated];
    
    [Flurry logEvent:[NSString stringWithFormat:@"%@ %@", @"give ", self.retailerName.text]];
    
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(keyboardWillShow:)
                                                 name:UIKeyboardWillShowNotification
                                               object:nil];
    
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(keyboardWillHide:)
                                                 name:UIKeyboardWillHideNotification 
                                               object:nil];
    
    [[NSNotificationCenter defaultCenter]addObserver:self selector:@selector(onLocationUpdate:) name:kKikbakLocationUpdate object:nil];
    [[NSNotificationCenter defaultCenter]addObserver:self selector:@selector(onShareSuccess:) name:kKikbakShareSuccess object:nil];
    [[NSNotificationCenter defaultCenter]addObserver:self selector:@selector(onShareError:) name:kKikbakShareError object:nil];
    [[NSNotificationCenter defaultCenter]addObserver:self selector:@selector(onImageUploadSuccess:) name:kKikbakImagePostSuccess object:nil];
    [[NSNotificationCenter defaultCenter]addObserver:self selector:@selector(onImageUploadError:) name:kKikbakImagePostError object:nil];
    [[NSNotificationCenter defaultCenter]addObserver:self selector:@selector(onFBStoryPostSuccess:) name:kKikbakFBStoryPostSuccess object:nil];
    [[NSNotificationCenter defaultCenter]addObserver:self selector:@selector(onFBStoryPostError:) name:kKikbakFBStoryPostError object:nil];
    [[NSNotificationCenter defaultCenter]addObserver:self selector:@selector(onTwitterPostSuccess:) name:kKikbakTwitterSuccess object:nil];
    [[NSNotificationCenter defaultCenter]addObserver:self selector:@selector(onTwitterPostError:) name:kKikbakTwitterError object:nil];
    [[NSNotificationCenter defaultCenter]addObserver:self selector:@selector(onImageDownloaded:) name:kKikbakImageDownloaded object:nil];
}

-(void)viewDidAppear:(BOOL)animated{
    [super viewDidAppear:animated];
    
}

-(void)viewDidDisappear:(BOOL)animated{
    [super viewDidDisappear:animated];
    [[NSNotificationCenter defaultCenter]removeObserver:self name:UIKeyboardWillHideNotification object:nil];
    [[NSNotificationCenter defaultCenter]removeObserver:self name:UIKeyboardWillShowNotification object:nil];
    [[NSNotificationCenter defaultCenter]removeObserver:self name:kKikbakLocationUpdate object:nil];
    [[NSNotificationCenter defaultCenter]removeObserver:self name:kKikbakShareSuccess object:nil];
    [[NSNotificationCenter defaultCenter]removeObserver:self name:kKikbakShareError object:nil];
    [[NSNotificationCenter defaultCenter]removeObserver:self name:kKikbakImagePostSuccess object:nil];
    [[NSNotificationCenter defaultCenter]removeObserver:self name:kKikbakImagePostError object:nil];
    [[NSNotificationCenter defaultCenter]removeObserver:self name:kKikbakFBStoryPostSuccess object:nil];
    [[NSNotificationCenter defaultCenter]removeObserver:self name:kKikbakFBStoryPostError object:nil];
    [[NSNotificationCenter defaultCenter]removeObserver:self name:kKikbakTwitterSuccess object:nil];
    [[NSNotificationCenter defaultCenter]removeObserver:self name:kKikbakTwitterError object:nil];
    [[NSNotificationCenter defaultCenter]removeObserver:self name:kKikbakImageDownloaded object:nil];
}

-(void)viewDidLayoutSubviews{
  //  [self manuallyLayoutSubviews];
}

#pragma mark - Layout
-(void)manuallyLayoutSubviews{
    if(![UIDevice hasFourInchDisplay]){
        self.giveImage.frame = CGRectMake(0, 0 + offsetForIOS6, 320, 218);
        self.imageOverlay.frame = CGRectMake(0, 0 + offsetForIOS6, 320, 218);
        [self.takePhoto removeFromSuperview];
        self.takePictureBtn.frame = CGRectMake(112, 20 + offsetForIOS6, 95, 95);
        self.retailerName.frame = CGRectMake(14, 121 + offsetForIOS6, 316, 28);
        self.mapIcon.frame = CGRectMake(14, 152 + offsetForIOS6, 10, 14);
        self.distance.frame = CGRectMake(30, 151 + offsetForIOS6, 70, 18);
        self.mapBtn.frame = CGRectMake(14, 149 + offsetForIOS6, 70, 30);
        self.webIcon.frame = CGRectMake(109, 153 + offsetForIOS6, 16, 16);
        self.webBtn.frame = CGRectMake(99, 148 + offsetForIOS6, 30, 30);
        self.callIcon.frame = CGRectMake(145, 152 + offsetForIOS6, 12, 16);
        self.callBtn.frame = CGRectMake(140, 148 + offsetForIOS6, 30, 30);
        self.captionContainerView.frame = CGRectMake(0, TEXT_EDIT_CONTAINER_ORIGIN_Y_35_SCREEN + offsetForIOS6, 320, 48);
        self.captionTextView.frame = CGRectMake(10, 8, 300, 32);
        self.dottedSeperator.frame = CGRectMake(0, 216 + offsetForIOS6, 320, 2);
        self.giftDesctription.frame = CGRectMake(0, 230 + offsetForIOS6, 320, 33);
        self.giftIcon.frame = CGRectMake(self.giftIcon.frame.origin.x, 237 + offsetForIOS6, 19, 18);
        self.giftDetailedDescription.frame = CGRectMake(0, 264 + offsetForIOS6, 320, 15);
        self.seperator.frame = CGRectMake(11, 287 + offsetForIOS6, 298, 1);
        self.rewardDescription.frame = CGRectMake(0, 292 + offsetForIOS6, 320, 30);
        self.rewardIcon.frame = CGRectMake(self.rewardIcon.frame.origin.x, 298 + offsetForIOS6, 19, 18);
        self.rewardDetailedDescription.frame = CGRectMake(0, 322 + offsetForIOS6, 320, 15);
        self.termsBtn.frame = CGRectMake(11, 344 + offsetForIOS6, 150, 16);
        self.giveBtn.frame = CGRectMake(11, 366 + offsetForIOS6, 298, 40);
        
        if( [self.offer.offerType compare:@"give_only"] == NSOrderedSame ){
            self.giftDesctription.frame = CGRectMake(0, 250 + offsetForIOS6, 320, 33);
            self.giftIcon.frame = CGRectMake(self.giftIcon.frame.origin.x, 257 + offsetForIOS6, 19, 18);
            self.giftDetailedDescription.frame = CGRectMake(0, 284 + offsetForIOS6, 320, 15);

//            self.giftDesctription.frame = CGRectMake(0, 335 + offsetForIOS6, 320, 33);
//            self.giftIcon.frame = CGRectMake((self.view.frame.size.width/2) - (gdSize.width/2)- 24, 342 + offsetForIOS6, 19, 18);
//            self.giftDetailedDescription.frame = CGRectMake(0, 368 + offsetForIOS6, 320, 15);
        }
    }
}

-(void)createSubviews{
    
    self.view.backgroundColor = UIColorFromRGB(0xFFFFFF);
    
    self.giveImage = [[UIImageView alloc]initWithFrame:CGRectMake(0, 0 + offsetForIOS6, 320, 292)];
    [self.view addSubview:self.giveImage];
    
    
    self.imageOverlay = [[UIImageView alloc]initWithFrame:CGRectMake(0, 0 + offsetForIOS6, 320, 292)];
    self.imageOverlay.image = [UIImage imageNamed:@"grd_give_default_photo_gradient"];
    [self.view addSubview:self.imageOverlay];
    
    self.takePhoto = [[UILabel alloc]initWithFrame:CGRectMake(0, 44 + offsetForIOS6, 320, 16)];
    self.takePhoto.text = NSLocalizedString(@"Take a photo", nil);
    self.takePhoto.font = [UIFont fontWithName:@"HelveticaNeue-Medium" size:16];
    self.takePhoto.textColor = [UIColor whiteColor];
    self.takePhoto.textAlignment = NSTextAlignmentCenter;
    self.takePhoto.backgroundColor = [UIColor clearColor];
    [self.view addSubview:self.takePhoto];
    
    self.takePictureBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    [self.takePictureBtn setImage:[UIImage imageNamed:@"ic_camera"] forState:UIControlStateNormal];
    self.takePictureBtn.frame = CGRectMake(112, 68 + offsetForIOS6, 95, 95);
    [self.takePictureBtn addTarget:self action:@selector(onTakePhotoBtn:) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:self.takePictureBtn];
    
    
    self.retailerName = [[UILabel alloc]initWithFrame:CGRectMake(14, 192 + offsetForIOS6, 316, 28)];
    self.retailerName.font = [UIFont fontWithName:@"HelveticaNeue" size:24];
    self.retailerName.textColor = UIColorFromRGB(0xFFFFFF);
    self.retailerName.text = self.offer.merchantName;
    self.retailerName.textAlignment = NSTextAlignmentLeft;
    self.retailerName.backgroundColor = [UIColor clearColor];
    [self.view addSubview:self.retailerName];
    
    self.mapIcon = [[UIImageView alloc]initWithFrame:CGRectMake(14, 225 + offsetForIOS6, 10, 14)];
    self.mapIcon.image = [UIImage imageNamed:@"ic_map_give"];
    [self.view addSubview:self.mapIcon];
    
    self.distance = [[UILabel alloc] initWithFrame:CGRectMake(30, 223 + offsetForIOS6, 70, 18)];
    self.distance.font = [UIFont fontWithName:@"HelveticaNeue-Medium" size:18];
    self.distance.textColor = UIColorFromRGB(0xFFFFFF);
    self.distance.backgroundColor = [UIColor clearColor];
    [self.view addSubview:self.distance];
    
    
    self.mapBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    self.mapBtn.frame = CGRectMake(14, 211 + offsetForIOS6, 70, 30);
    self.mapBtn.backgroundColor = [UIColor clearColor];
    [self.mapBtn addTarget:self action:@selector(onMapBtn:) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:self.mapBtn];
    
    self.webIcon = [[UIImageView alloc]initWithFrame:CGRectMake(109, 224 + offsetForIOS6, 16, 16)];
    self.webIcon.image = [UIImage imageNamed:@"ic_web_give"];
    [self.view addSubview:self.webIcon];
    

    self.webBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    self.webBtn.backgroundColor = [UIColor clearColor];
    self.webBtn.frame = CGRectMake(99, 210 + offsetForIOS6, 30, 30);
    self.webBtn.backgroundColor = [UIColor clearColor];
    [self.webBtn addTarget:self action:@selector(onWebBtn:) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:self.webBtn];
    
    
    self.callIcon = [[UIImageView alloc]initWithFrame:CGRectMake(150, 224 + offsetForIOS6, 12, 16)];
    self.callIcon.image = [UIImage imageNamed:@"ic_phone_give"];
    [self.view addSubview: self.callIcon];
    
    self.callBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    self.callBtn.frame = CGRectMake(145, 210 + offsetForIOS6, 30, 30);
    self.callBtn.backgroundColor = [UIColor clearColor];
    [self.callBtn addTarget:self action:@selector(onCallBtn:) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:self.callBtn];
    
    
    self.captionContainerView = [[UIView alloc]initWithFrame:CGRectMake(0, TEXT_EDIT_CONTAINER_ORIGIN_Y + offsetForIOS6, 320, 48)];
    [self.view addSubview:self.captionContainerView];
    
    self.captionGradient = [[UIImageView alloc]initWithFrame:self.captionContainerView.frame];
    self.captionGradient.image = [UIImage imageNamed:@"grd_give_caption"];
    [self.captionContainerView addSubview:self.captionGradient];
    
    CGRect capTextFr = CGRectMake(10, 8, 300, 32);
    if([UIDevice osVersion7orGreater]){
        capTextFr.size.height = 42;
    }
    self.captionTextView = [[HPGrowingTextView alloc]initWithFrame:capTextFr];
    self.captionTextView.text = NSLocalizedString(@"add comment", nil);
    self.captionTextView.contentInset = UIEdgeInsetsMake(9, 0, 8, 0);
    self.captionTextView.minNumberOfLines = 1;
	self.captionTextView.maxNumberOfLines = 2;
	self.captionTextView.returnKeyType = UIReturnKeyDone; 
	self.captionTextView.font = [UIFont fontWithName:@"HelveticaNeue-Italic" size:14.0f];
    self.captionTextView.textColor = UIColorFromRGB(0x9f9f9f);
	self.captionTextView.delegate = self;
    self.captionTextView.layer.cornerRadius = 5;
    self.captionTextView.layer.masksToBounds = YES;
    self.captionTextView.internalTextView.layer.cornerRadius = 5;
    self.captionTextView.internalTextView.layer.masksToBounds = YES;
    self.captionTextView.backgroundColor = [UIColor whiteColor];
    [self.captionContainerView addSubview:self.captionTextView];
    
//    self.dottedSeperator = [[UIImageView alloc] initWithFrame:CGRectMake(0, 290 + offsetForIOS6, 320, 2)];
//    self.dottedSeperator.image = [UIImage imageNamed:@"separator_dots"];
//    [self.view addSubview:self.dottedSeperator];
    
    
    self.giftDesctription = [[UILabel alloc]initWithFrame:CGRectMake(0, 305 + offsetForIOS6, 320, 33)];
    self.giftDesctription.font = [UIFont fontWithName:@"HelveticaNeue-Medium" size:27];
    self.giftDesctription.text = [NSString stringWithFormat:NSLocalizedString(@"Give description", nil), self.offer.giftDescription];
    self.giftDesctription.textColor = UIColorFromRGB(0x2A80E6);
    self.giftDesctription.textAlignment = NSTextAlignmentCenter;
    self.giftDesctription.backgroundColor = [UIColor clearColor];
    [self.view addSubview:self.giftDesctription];
    
    CGSize gdSize = [self.giftDesctription.text sizeWithFont:self.giftDesctription.font];
    self.giftIcon = [[UIImageView alloc]initWithFrame:CGRectMake((self.view.frame.size.width/2) - (gdSize.width/2)- 24, 312 + offsetForIOS6, 19, 18)];
    self.giftIcon.image = [UIImage imageNamed:@"ic_gift"];
    [self.view addSubview:self.giftIcon];

    self.giftDetailedDescription = [[UILabel alloc]initWithFrame:CGRectMake(0, 338 + offsetForIOS6, 320, 15)];
    self.giftDetailedDescription.font = [UIFont fontWithName:@"HelveticaNeue" size:13];
    self.giftDetailedDescription.text = self.offer.giftDescriptionOptional;
    self.giftDetailedDescription.textColor = UIColorFromRGB(0x898989);
    self.giftDetailedDescription.textAlignment = NSTextAlignmentCenter;
    self.giftDetailedDescription.backgroundColor = [UIColor clearColor];
    [self.view addSubview:self.giftDetailedDescription];

    if( [self.offer.offerType compare:@"both"] == NSOrderedSame){
        self.seperator = [[UIImageView alloc]initWithFrame:CGRectMake(11, 364 + offsetForIOS6, 298, 1)];
        self.seperator.image = [UIImage imageNamed:@"separator_gray_line"];
        [self.view addSubview:self.seperator];

        self.rewardDescription = [[UILabel alloc]initWithFrame:CGRectMake(0, 370 + offsetForIOS6, 320, 30)];
        self.rewardDescription.font = [UIFont fontWithName:@"HelveticaNeue-Medium" size:25];
        self.rewardDescription.text = [NSString stringWithFormat:NSLocalizedString(@"Get description", nil), self.offer.kikbakDescription];
        self.rewardDescription.textColor = UIColorFromRGB(0x3a3a3a);
        self.rewardDescription.textAlignment = NSTextAlignmentCenter;
        self.rewardDescription.backgroundColor = [UIColor clearColor];
        [self.view addSubview:self.rewardDescription];

        gdSize = [self.rewardDescription.text sizeWithFont:self.rewardDescription.font];
        self.rewardIcon = [[UIImageView alloc]initWithFrame:CGRectMake((self.view.frame.size.width/2) - (gdSize.width/2)- 24, 376 + offsetForIOS6, 20, 20)];
        self.rewardIcon.image = [UIImage imageNamed:@"ic_give_trophy"];
        [self.view addSubview:self.rewardIcon];
        
        self.rewardDetailedDescription = [[UILabel alloc]initWithFrame:CGRectMake(0, 401 + offsetForIOS6, 320, 15)];
        self.rewardDetailedDescription.font = [UIFont fontWithName:@"HelveticaNeue" size:13];
        self.rewardDetailedDescription.text = self.offer.kikbakDescriptionOptional;
        self.rewardDetailedDescription.textColor = UIColorFromRGB(0x898989);
        self.rewardDetailedDescription.textAlignment = NSTextAlignmentCenter;
        self.rewardDetailedDescription.backgroundColor = [UIColor clearColor];
        [self.view addSubview:self.rewardDetailedDescription];
    }
    else if( [self.offer.offerType compare:@"give_only"] == NSOrderedSame ){
        self.giftDesctription.frame = CGRectMake(0, 335 + offsetForIOS6, 320, 33);
        self.giftIcon.frame = CGRectMake((self.view.frame.size.width/2) - (gdSize.width/2)- 24, 342 + offsetForIOS6, 19, 18);
        self.giftDetailedDescription.frame = CGRectMake(0, 368 + offsetForIOS6, 320, 15);
    }
    
    self.termsBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    self.termsBtn.frame = CGRectMake(11, 424 + offsetForIOS6, 150, 16);
    [self.termsBtn setTitle:NSLocalizedString(@"Terms and Conditions", nil) forState:UIControlStateNormal];
    self.termsBtn.titleLabel.font = [UIFont fontWithName:@"HelveticaNeue-Light" size:12];
    [self.termsBtn setTitleColor:UIColorFromRGB(0x686868) forState:UIControlStateNormal];
    self.termsBtn.titleLabel.textAlignment = NSTextAlignmentLeft;
    [self.termsBtn addTarget:self action:@selector(onTerms:) forControlEvents:UIControlEventTouchUpInside];
    self.termsBtn.contentHorizontalAlignment = UIControlContentHorizontalAlignmentLeft;
    [self.view addSubview:self.termsBtn];

    
    self.giveBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    [self.giveBtn setBackgroundImage:[UIImage imageNamed:@"btn_blue"] forState:UIControlStateNormal];
    [self.giveBtn setTitle:NSLocalizedString(@"give to friends", nil) forState:UIControlStateNormal];
    [self.giveBtn setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
    self.giveBtn.titleLabel.font = [UIFont fontWithName:@"HelveticaNeue-Medium" size:15];
    self.giveBtn.frame = CGRectMake(11, 453 + offsetForIOS6, 298, 40);
    [self.giveBtn addTarget:self action:@selector(onGiveGift:) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:self.giveBtn];
}

-(void)adjustRetailerInfo:(CGFloat)delta{
    CGRect fr = self.mapIcon.frame;
    fr.origin.y -= delta;
    self.mapIcon.frame = fr;
    fr = self.mapBtn.frame;
    fr.origin.y -= delta;
    self.mapBtn.frame=fr;
    fr = self.distance.frame;
    fr.origin.y -= delta;
    self.distance.frame = fr;
    
    fr = self.webIcon.frame;
    fr.origin.y -= delta;
    self.webIcon.frame = fr;
    fr = self.webBtn.frame;
    fr.origin.y -= delta;
    self.webBtn.frame = fr;
    
    fr = self.retailerName.frame;
    fr.origin.y -= delta;
    self.retailerName.frame = fr;
    
    if( photoTaken ){
        CGRect frOverlay = self.imageOverlay.frame;
        frOverlay.origin.y = fr.origin.y - 20 + offsetForIOS6;
        frOverlay.size.height = self.giveImage.frame.size.height - frOverlay.origin.y + offsetForIOS6;
        self.imageOverlay.frame = frOverlay;
    }
    
    fr = self.callIcon.frame;
    fr.origin.y -= delta;
    self.callIcon.frame = fr;
    fr = self.callBtn.frame;
    fr.origin.y -= delta;
    self.callBtn.frame = fr;
    
}

#pragma mark - btn actions
-(IBAction)onBackBtn:(id)sender{
    [self.navigationController popToRootViewControllerAnimated:YES];

}

-(IBAction)onGiveGift:(id)sender{
    CGRect frame = ((AppDelegate*)[UIApplication sharedApplication].delegate).window.frame;
    if([self.captionTextView.text compare:NSLocalizedString(@"add comment", nil)] == NSOrderedSame){
        self.captionTextView.text = @"";
    }
    
    [Flurry logEvent:@"share offer"];

    
    frame.origin.y += offsetForIOS6;
    if( photoTaken == YES){
        self.spinnerView = [[SpinnerView alloc]initWithFrame:frame];
        [self.spinnerView startActivity];
        [((AppDelegate*)[UIApplication sharedApplication].delegate).window addSubview:self.spinnerView];

        ImageUploadRequest* request = [[ImageUploadRequest alloc]init];
        request.image = self.imageToPost;
        [request postImage];
    }
    else{
        self.imageUrl = self.offer.giveImageUrl;
        ShareChannelSelectorView* view = [[ShareChannelSelectorView alloc]initWithFrame:frame];
        view.locations = self.offer.location;
        view.showEmployeeName = [self.offer.hasEmployeeProgram boolValue];
        [view createsubviews];
        view.delegate = self;
        [self.view addSubview:view];
    }
}

-(IBAction)onTakePhotoBtn:(id)sender{
    [self resignTextView];
    
    [Flurry logEvent:@"take picture"];

    if([UIImagePickerController isSourceTypeAvailable:UIImagePickerControllerSourceTypeCamera]){
        UIImagePickerController* picker = [[UIImagePickerController alloc]init];
        //picker.view.frame = self.view.frame;
        picker.sourceType = UIImagePickerControllerSourceTypeCamera;
        picker.delegate = self;
        
        UIImageView* square = [[UIImageView alloc]initWithFrame:CGRectMake(0, 0, 320, 485)];
        if([UIDevice hasFourInchDisplay]){
            
            square.image = [UIImage imageNamed:@"camera_screen_area"];
            square.frame = CGRectMake(0, 68, 320, 427);
        }
        else{
            square.image = [UIImage imageNamed:@"camera_screen_area"];
            square.frame =  CGRectMake(0, 0, 320, 395);
        }
        picker.cameraOverlayView = square;
        
        [Flurry logEvent:@"PhotoEvent" timed:YES];
        
        [self presentViewController:picker animated:YES completion:nil];
    }
    else{
        //for simulator testing
        UIImagePickerController* picker = [[UIImagePickerController alloc]init];
        picker.view.frame = self.view.frame;
        picker.sourceType = UIImagePickerControllerSourceTypePhotoLibrary;
        picker.delegate = self;
        
        [self presentViewController:picker animated:YES completion:nil];
    }
}

-(IBAction)onTerms:(id)sender{
    CGRect frame = ((AppDelegate*)[UIApplication sharedApplication].delegate).window.frame;
    TermsAndConditionsView* view = [[TermsAndConditionsView alloc]initWithFrame:frame];
    view.tosUrl = self.offer.termsOfService;
    [view manuallyLayoutSubviews];
    [((AppDelegate*)[UIApplication sharedApplication].delegate).window addSubview:view];
}

-(IBAction)onMapBtn:(id)sender{
    NSString *stringURL = [NSString stringWithFormat:@"http://maps.apple.com/maps?q=%@,%@",
                                    self.location.latitude, self.location.longitude];
    NSURL *url = [NSURL URLWithString:stringURL];
    [[UIApplication sharedApplication] openURL:url];
}

-(IBAction)onCallBtn:(id)sender{
    NSURL* url = [[NSURL alloc]initWithString:[NSString stringWithFormat:@"tel:%@",self.location.phoneNumber]];
    if(![[UIApplication sharedApplication] canOpenURL:url]){
        UIAlertView* alert = [[UIAlertView alloc]initWithTitle:@"Hmmm..." message:@"You need to be on an iPhone to make a call" delegate:nil cancelButtonTitle:@"Ok" otherButtonTitles:nil];
        alert.tag = CALL_URL_TAG;
        [alert show];
    }
    else{
        [[UIApplication sharedApplication]openURL:url];
    }
}


-(IBAction)onWebBtn:(id)sender{
    [[UIApplication sharedApplication]openURL:[[NSURL alloc]initWithString:self.offer.merchantUrl]];
}

#pragma mark - AlertView Delegate Methods
- (void)alertView:(UIAlertView *)alertView didDismissWithButtonIndex:(NSInteger)buttonIndex{
}


#pragma mark - image picker delegates
- (void)imagePickerController:(UIImagePickerController *)picker didFinishPickingMediaWithInfo:(NSDictionary *)info{
    UIImage* grd = [UIImage imageNamed:@"grd_give_img"];//
    self.imageOverlay.image = grd;
    CGRect fr = self.imageOverlay.frame;
    fr.origin.y =  self.retailerName.frame.origin.y - 40 + offsetForIOS6;//self.captionContainerView.frame.origin.y - grd.size.height;
    fr.size.height = self.giveImage.frame.size.height - fr.origin.y + offsetForIOS6;
    self.imageOverlay.frame = fr;

    [self.takePhoto removeFromSuperview];
    self.takePictureBtn.frame = CGRectMake(265, 0  + offsetForIOS6, 55, 55);
    [self.takePictureBtn setImage:[UIImage imageNamed:@"ic_post_give_camera"] forState:UIControlStateNormal];
    

    UIImage* image = [info valueForKey:UIImagePickerControllerOriginalImage];
    image = [image imageByScalingAndCroppingForSize:CGSizeMake(640, 853)];

    CGRect cropRect = CGRectMake(8, 94, 624, 624);
    self.imageToPost = [image imageCropToRect:cropRect];
    self.giveImage.image = self.imageToPost;
    if( ![UIDevice hasFourInchDisplay]){
        cropRect = CGRectMake(0, 198, 640, 436);
        self.giveImage.image = [image imageCropToRect:cropRect];
        self.giveImage.frame = CGRectMake(0, 0, 320, 218);
    }
    
    [self dismissViewControllerAnimated:YES completion:nil];
    photoTaken = YES;
}

- (void)imagePickerControllerDidCancel:(UIImagePickerController *)picker{
    
    [self dismissViewControllerAnimated:YES completion:nil];
}


#pragma mark - keyboard options
-(IBAction)keyboardWillShow:(NSNotification*)notification{
    
    if(!adjustTextview){
        return;
    }
    
    CGRect keyboardBounds;
    [[notification.userInfo valueForKey:UIKeyboardFrameEndUserInfoKey] getValue: &keyboardBounds];
    NSNumber *duration = [notification.userInfo objectForKey:UIKeyboardAnimationDurationUserInfoKey];
    NSNumber *curve = [notification.userInfo objectForKey:UIKeyboardAnimationCurveUserInfoKey];
    
    // Need to translate the bounds to account for rotation.
    keyboardBounds = [self.view convertRect:keyboardBounds toView:nil];
    
	// get a rect for the textView frame
	CGRect containerFrame = self.captionContainerView.frame;
    containerFrame.origin.y = self.view.bounds.size.height - (keyboardBounds.size.height + self.captionContainerView.frame.size.height);
	// animations settings
	[UIView beginAnimations:nil context:NULL];
    [UIView setAnimationDelay:0.08];
	[UIView setAnimationBeginsFromCurrentState:YES];
    [UIView setAnimationDuration:[duration doubleValue]];
    [UIView setAnimationCurve:(UIViewAnimationCurve)[curve intValue]];
	
	// set views with new info
    if( self.captionContainerView.frame.origin.y != containerFrame.origin.y){
        CGFloat delta = self.captionContainerView.frame.origin.y - containerFrame.origin.y;
        [self adjustRetailerInfo:delta];
    }
	self.captionContainerView.frame = containerFrame;
	
	// commit animations
	[UIView commitAnimations];
}


-(IBAction)keyboardWillHide:(NSNotification*)notification{
    NSNumber *duration = [notification.userInfo objectForKey:UIKeyboardAnimationDurationUserInfoKey];
    NSNumber *curve = [notification.userInfo objectForKey:UIKeyboardAnimationCurveUserInfoKey];
	
    if( adjustTextview == NO ){
        return;
    }
    
	// get a rect for the textView frame
	CGRect containerFrame = self.captionContainerView.frame;
    if( [UIDevice hasFourInchDisplay] ){
        containerFrame.origin.y = (containerFrame.size.height > DEFAULT_CONTAINER_VIEW_HEIGHT)?TEXT_EDIT_CONTAINER_ORIGIN_Y - (containerFrame.size.height - DEFAULT_CONTAINER_VIEW_HEIGHT) + offsetForIOS6: TEXT_EDIT_CONTAINER_ORIGIN_Y + offsetForIOS6;
    }
    else{
        containerFrame.origin.y = (containerFrame.size.height > DEFAULT_CONTAINER_VIEW_HEIGHT)? TEXT_EDIT_CONTAINER_ORIGIN_Y_35_SCREEN - (containerFrame.size.height - DEFAULT_CONTAINER_VIEW_HEIGHT) + offsetForIOS6: TEXT_EDIT_CONTAINER_ORIGIN_Y_35_SCREEN + offsetForIOS6;
    }
	
	// animations settings
	[UIView beginAnimations:nil context:NULL];
	[UIView setAnimationBeginsFromCurrentState:YES];
    [UIView setAnimationDuration:[duration doubleValue]];
    [UIView setAnimationCurve:(UIViewAnimationCurve)[curve intValue]];
    
	// set views with new info
    // set views with new info
    if(self.captionContainerView.frame.origin.y != containerFrame.origin.y){
        CGFloat delta = self.captionContainerView.frame.origin.y - containerFrame.origin.y ;
        [self adjustRetailerInfo:delta];
    }
	self.captionContainerView.frame = containerFrame;
    [self.view bringSubviewToFront:self.captionContainerView];
	
	// commit animations
	[UIView commitAnimations];
    
}

#pragma mark - HPGrowingTextView delegate methods
-(void)resignTextView
{
	[self.captionTextView resignFirstResponder];
    adjustTextview = NO;
}


- (BOOL)growingTextViewShouldReturn:(HPGrowingTextView *)growingTextView{
    if( [growingTextView.text compare:NSLocalizedString(@"", nil)] == NSOrderedSame ){
        growingTextView.text = NSLocalizedString(@"add comment", nil);
        growingTextView.textColor = UIColorFromRGB(0x9f9f9f);
    }
    [Flurry logEvent:@"add comment"];
    [self resignTextView];
    return YES;
}


- (void)growingTextView:(HPGrowingTextView *)growingTextView willChangeHeight:(float)height
{
    float diff = (growingTextView.frame.size.height - height);
    
	CGRect r = self.captionContainerView.frame;
    r.size.height -= diff;
    r.origin.y += diff;
    [self adjustRetailerInfo:-diff];
	self.captionContainerView.frame = r;
    self.captionGradient.frame = r;
}

- (BOOL)growingTextViewShouldBeginEditing:(HPGrowingTextView *)growingTextView{
    adjustTextview = YES;
    if( [growingTextView.text compare:NSLocalizedString(@"add comment", nil)] == NSOrderedSame ){
        growingTextView.text = @"";
    }
    growingTextView.font = [UIFont fontWithName:@"HelveticaNeue-Light" size:14.0];
    growingTextView.textColor = [UIColor blackColor];
    
    return YES;
}


#pragma mark - NSNotification Center 
-(void) onShareSuccess:(NSNotification*)notification{
    ShareResult* result = [notification object];
    if( shareViaEmail ){
        if( [MFMailComposeViewController canSendMail]){
            MFMailComposeViewController* picker = [[MFMailComposeViewController alloc]init];
            picker.mailComposeDelegate = self;
            [picker setSubject:result.subject];
            [picker setMessageBody:result.body isHTML:NO];
            
            [self presentViewController:picker animated:YES completion:nil];
        }
        [self.spinnerView removeFromSuperview];
    }
    else if( shareViaSMS ){
        if( [MFMessageComposeViewController canSendText] ){
            MFMessageComposeViewController* message = [[MFMessageComposeViewController alloc]init];
            message.messageComposeDelegate = self;
            [message setBody:result.body];
            [self presentViewController:message animated:YES completion:nil];
        }
        [self.spinnerView removeFromSuperview];
    }
    else if( shareViaTwitter){
        TwitterApi* api = [[TwitterApi alloc]init];
        [api postToTimeline:result.body];
    }
    else{
//        [FBSettings setLoggingBehavior:[NSSet setWithObject:FBLoggingBehaviorFBRequests]];
        FBCouponObject* obj = [[FBCouponObject alloc]init];
        obj.caption = self.captionTextView.text;
        obj.merchant = self.retailerName.text;
        obj.locationId = self.chosenlocation;
        obj.employeeName = self.employeeName;
        obj.landingUrl = result.landingUrl;
        obj.gift = self.offer.giftDescription;
        obj.detailedDescription = self.offer.giftDescriptionOptional;
        [obj postCoupon:self.imageUrl];
    }
    
}

-(void) onShareError:(NSNotification*)notification{
    dispatch_async(dispatch_get_main_queue(), ^{
        UIAlertView* alert = [[UIAlertView alloc]initWithTitle:@"Hmmm..." message:NSLocalizedString(@"Unreachable", nil) delegate:nil cancelButtonTitle:@"Ok" otherButtonTitles:nil];
        alert.tag = CALL_URL_TAG;
        [alert show];
        [self.spinnerView removeFromSuperview];
    });
}


-(void) onImageUploadSuccess:(NSNotification*)notification{
    [self.spinnerView removeFromSuperview];
    
    self.imageUrl = [notification object];
    CGRect frame = ((AppDelegate*)[UIApplication sharedApplication].delegate).window.frame;
    frame.origin.y += offsetForIOS6;
    ShareChannelSelectorView* view = [[ShareChannelSelectorView alloc]initWithFrame:frame];
    view.locations = self.offer.location;
    view.showEmployeeName = [self.offer.hasEmployeeProgram boolValue];
    [view createsubviews];
    view.delegate = self;
    [self.view addSubview:view];
}

-(void) onImageUploadError:(NSNotification*)notification{
    dispatch_async(dispatch_get_main_queue(), ^{
        UIAlertView* alert = [[UIAlertView alloc]initWithTitle:@"Hmmm..." message:NSLocalizedString(@"Unreachable", nil) delegate:nil cancelButtonTitle:@"Ok" otherButtonTitles:nil];
        alert.tag = CALL_URL_TAG;
        [alert show];
        [self.spinnerView removeFromSuperview];
    });
}

-(void) onLocationUpdate:(NSNotification*)notification{
    [self updateDisance];
}

-(void) updateDisance{
    CLLocation* current = [[CLLocation alloc]initWithLatitude:self.location.latitude.doubleValue longitude:self.location.longitude.doubleValue];
    self.distance.text = [NSString stringWithFormat:NSLocalizedString(@"miles away", nil),
                          [Distance distanceToInMiles:current]];
}

-(void) onFBStoryPostSuccess:(NSNotification*)notification{
    [Flurry logEvent:@"ShareEvent" timed:YES];
    
    dispatch_async(dispatch_get_main_queue(), ^{
        CGRect frame = ((AppDelegate*)[UIApplication sharedApplication].delegate).window.frame;
        ShareSuccessView* shareView = [[ShareSuccessView alloc]initWithFrame:frame];
        shareView.delegate = self;
        [shareView manuallyLayoutSubviews];
        [((AppDelegate*)[UIApplication sharedApplication].delegate).window addSubview:shareView];
    
        [self.spinnerView removeFromSuperview];
    });

}

-(void) onFBStoryPostError:(NSNotification *)notification{
    dispatch_async(dispatch_get_main_queue(), ^{
        UIAlertView* alert = [[UIAlertView alloc]initWithTitle:@"Hmmm..." message:NSLocalizedString(@"Unreachable", nil) delegate:nil cancelButtonTitle:@"Ok" otherButtonTitles:nil];
        alert.tag = CALL_URL_TAG;
        [alert show];
        
        [self.spinnerView removeFromSuperview];
    });
}

-(void) onTwitterPostSuccess:(NSNotification*)notification{
    [Flurry logEvent:@"TwitterEvent" timed:YES];
    
    dispatch_async(dispatch_get_main_queue(), ^{
    
        CGRect frame = ((AppDelegate*)[UIApplication sharedApplication].delegate).window.frame;
        ShareSuccessView* shareView = [[ShareSuccessView alloc]initWithFrame:frame];
        shareView.delegate = self;
        [shareView manuallyLayoutSubviews];
        [((AppDelegate*)[UIApplication sharedApplication].delegate).window addSubview:shareView];
        
        [self.spinnerView removeFromSuperview];
    });
}

-(void) onTwitterPostError:(NSNotification*)notification{
    
    dispatch_async(dispatch_get_main_queue(), ^{
        [self.spinnerView removeFromSuperview];
        UIAlertView* alert = [[UIAlertView alloc]initWithTitle:@"Hmmm..." message:NSLocalizedString(@"Twitter Error", nil) delegate:nil cancelButtonTitle:@"Ok" otherButtonTitles:nil];
        [alert show];
    });
}


-(void)onImageDownloaded:(NSNotification*)notification{
    NSString* imagePath = [ImagePersistor imageFileExists:self.offer.merchantId imageType:DEFAULT_MERCHANT_IMAGE_TYPE];
    if(imagePath != nil){
        self.giveImage.image = self.imageToPost = [[UIImage alloc]initWithContentsOfFile:imagePath];
    }
}

#pragma mark - ShareComplete Delegate
-(void) onShareFinished{
    [self.navigationController popToRootViewControllerAnimated:YES];
}

#pragma mark - ShareChannelSelector
-(void)onEmailSelected:(NSNumber*)locationId withEmployeeName:(NSString*)name{
    CGRect frame = ((AppDelegate*)[UIApplication sharedApplication].delegate).window.frame;
    self.spinnerView = [[SpinnerView alloc]initWithFrame:frame];
    [self.spinnerView startActivity];
    [((AppDelegate*)[UIApplication sharedApplication].delegate).window addSubview:self.spinnerView];
    
    //todo: upload email
    shareViaEmail = YES;
    shareViaSMS = NO;
    shareViaTwitter = NO;
    ShareExperienceRequest* request = [[ShareExperienceRequest alloc]init];
    NSMutableDictionary* dict = [[NSMutableDictionary alloc]initWithCapacity:5];
    
    [dict setObject:self.offer.merchantId forKey:@"merchantId"];
    if( locationId != nil){
        [dict setObject:locationId forKey:@"locationId"];
    }
    [dict setObject:self.offer.offerId forKey:@"offerId"];
    [dict setObject:self.imageUrl forKey:@"imageUrl"];
    [dict setObject:@"email" forKey:@"type"];
    [dict setObject:@"ios" forKey:@"platform"];
    [dict setObject:name forKey:@"employeeId"];
    
    CLLocation* currentLocation = ((AppDelegate*)[UIApplication sharedApplication].delegate).locationMgr.currentLocation;
    [dict setObject:[NSNumber numberWithDouble:currentLocation.coordinate.latitude ] forKey:@"latitude"];
    [dict setObject:[NSNumber numberWithDouble:currentLocation.coordinate.longitude ] forKey:@"longitude"];

    
    if( [self.captionTextView.text compare:NSLocalizedString(@"add comment", nil)] == NSOrderedSame ){
        [dict setObject:@"" forKey:@"caption"];
    }
    else{
        [dict setObject:self.captionTextView.text forKey:@"caption"];
    }
    [request restRequest:dict];

}

-(void)onSmsSelected:(NSNumber*)locationId withEmployeeName:(NSString*)name{
    CGRect frame = ((AppDelegate*)[UIApplication sharedApplication].delegate).window.frame;
    self.spinnerView = [[SpinnerView alloc]initWithFrame:frame];
    [self.spinnerView startActivity];
    [((AppDelegate*)[UIApplication sharedApplication].delegate).window addSubview:self.spinnerView];
    
    shareViaEmail = NO;
    shareViaSMS = YES;
    shareViaTwitter = NO;
    ShareExperienceRequest* request = [[ShareExperienceRequest alloc]init];
    NSMutableDictionary* dict = [[NSMutableDictionary alloc]initWithCapacity:5];
    
    [dict setObject:self.offer.merchantId forKey:@"merchantId"];
    if( locationId != nil){
        [dict setObject:locationId forKey:@"locationId"];
    }
    [dict setObject:self.offer.offerId forKey:@"offerId"];
    [dict setObject:self.imageUrl forKey:@"imageUrl"];
    [dict setObject:@"sms" forKey:@"type"];
    [dict setObject:@"ios" forKey:@"platform"];
    [dict setObject:name forKey:@"employeeId"];
    
    CLLocation* currentLocation = ((AppDelegate*)[UIApplication sharedApplication].delegate).locationMgr.currentLocation;
    [dict setObject:[NSNumber numberWithDouble:currentLocation.coordinate.latitude ] forKey:@"latitude"];
    [dict setObject:[NSNumber numberWithDouble:currentLocation.coordinate.longitude ] forKey:@"longitude"];

    
    if( [self.captionTextView.text compare:NSLocalizedString(@"add comment", nil)] == NSOrderedSame ){
        [dict setObject:@"" forKey:@"caption"];
    }
    else{
        [dict setObject:self.captionTextView.text forKey:@"caption"];
    }
    [request restRequest:dict];
}

-(void)onFBSelected:(NSNumber*)locationId withEmployeeName:(NSString*)name{
    CGRect frame = ((AppDelegate*)[UIApplication sharedApplication].delegate).window.frame;
    self.spinnerView = [[SpinnerView alloc]initWithFrame:frame];
    [self.spinnerView startActivity];
    [((AppDelegate*)[UIApplication sharedApplication].delegate).window addSubview:self.spinnerView];

    shareViaEmail = NO;
    shareViaSMS = NO;
    shareViaTwitter = NO;
    
    self.employeeName = name;
    self.chosenlocation = locationId;
    
    ShareExperienceRequest* request = [[ShareExperienceRequest alloc]init];
    NSMutableDictionary* dict = [[NSMutableDictionary alloc]initWithCapacity:5];
    [dict setObject:self.offer.merchantId forKey:@"merchantId"];
    if( locationId != nil){
        [dict setObject:locationId forKey:@"locationId"];
    }
    [dict setObject:self.offer.offerId forKey:@"offerId"];
    [dict setObject:name forKey:@"employeeId"];
    [dict setObject:self.imageUrl forKey:@"imageUrl"];
    [dict setObject:@"fb" forKey:@"type"];
    [dict setObject:@"ios" forKey:@"platform"];
    
    CLLocation* currentLocation = ((AppDelegate*)[UIApplication sharedApplication].delegate).locationMgr.currentLocation;
    [dict setObject:[NSNumber numberWithDouble:currentLocation.coordinate.latitude ] forKey:@"latitude"];
    [dict setObject:[NSNumber numberWithDouble:currentLocation.coordinate.longitude ] forKey:@"longitude"];

    
    if( [self.captionTextView.text compare:NSLocalizedString(@"add comment", nil)] == NSOrderedSame ){
        [dict setObject:@"" forKey:@"caption"];
    }
    else{
        [dict setObject:self.captionTextView.text forKey:@"caption"];
    }
    [request restRequest:dict];
    
}

-(void)onTwitterSelected:(NSNumber*)locationId withEmployeeName:(NSString*)name{
    CGRect frame = ((AppDelegate*)[UIApplication sharedApplication].delegate).window.frame;
    self.spinnerView = [[SpinnerView alloc]initWithFrame:frame];
    [self.spinnerView startActivity];
    [((AppDelegate*)[UIApplication sharedApplication].delegate).window addSubview:self.spinnerView];

    shareViaEmail = NO;
    shareViaSMS = NO;
    shareViaTwitter = YES;
    
    ShareExperienceRequest* request = [[ShareExperienceRequest alloc]init];
    NSMutableDictionary* dict = [[NSMutableDictionary alloc]initWithCapacity:5];
    [dict setObject:self.offer.merchantId forKey:@"merchantId"];
    if( locationId != nil){
        [dict setObject:locationId forKey:@"locationId"];
    }
    [dict setObject:self.offer.offerId forKey:@"offerId"];
    [dict setObject:name forKey:@"employeeId"];
    [dict setObject:self.imageUrl forKey:@"imageUrl"];
    [dict setObject:@"twitter" forKey:@"type"];
    [dict setObject:@"ios" forKey:@"platform"];
    
    CLLocation* currentLocation = ((AppDelegate*)[UIApplication sharedApplication].delegate).locationMgr.currentLocation;
    [dict setObject:[NSNumber numberWithDouble:currentLocation.coordinate.latitude ] forKey:@"latitude"];
    [dict setObject:[NSNumber numberWithDouble:currentLocation.coordinate.longitude ] forKey:@"longitude"];
    
    if( [self.captionTextView.text compare:NSLocalizedString(@"add comment", nil)] == NSOrderedSame ){
        [dict setObject:@"" forKey:@"caption"];
    }
    else{
        [dict setObject:self.captionTextView.text forKey:@"caption"];
    }
    [request restRequest:dict];
}

#pragma mark - MFMailComposer Delegates
// Dismisses the email composition interface when users tap Cancel or Send. Proceeds to update the message field with the result of the operation.
- (void)mailComposeController:(MFMailComposeViewController*)controller didFinishWithResult:(MFMailComposeResult)result error:(NSError*)error
{
	// Notifies users about errors associated with the interface
	switch (result)
	{
		case MFMailComposeResultCancelled:
			break;
		case MFMailComposeResultSaved:
			break;
		case MFMailComposeResultSent:
			break;
		case MFMailComposeResultFailed:
			break;
		default:
			break;
	}
	[self dismissViewControllerAnimated:YES completion:nil];
}

- (void)messageComposeViewController:(MFMessageComposeViewController *)controller didFinishWithResult:(MessageComposeResult)result{

    switch (result) {
        case MessageComposeResultCancelled:
        case MessageComposeResultSent:
        case MessageComposeResultFailed:
            break;
            
        default:
            break;
    }
    [self dismissViewControllerAnimated:YES completion:nil];
}

@end
