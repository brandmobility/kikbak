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

#define DEFAULT_CONTAINER_VIEW_HEIGHT 50
#define PHOTO_TAG  1000
#define CAPTION_TAG  1001
#define CALL_URL_TAG 1002

const double TEXT_EDIT_CONTAINER_ORIGIN_Y = 243.0;
const double TEXT_EDIT_CONTAINER_ORIGIN_Y_35_SCREEN = 170.0;

@interface GiveViewController (){
    BOOL shareViaEmail;
    BOOL shareViaSMS;
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
@property(nonatomic, strong) UILabel* giftDescriptionOptional;
@property(nonatomic, strong) UIImageView* seperator;
@property(nonatomic, strong) UILabel* rewardDescription;
@property(nonatomic, strong) UILabel* rewardDescriptionOptional;
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
    self.title = NSLocalizedString(@"Give", nil);
	// Do any additional setup after loading the view.
    shareViaEmail = NO;
    shareViaSMS = NO;
    photoTaken = NO;
    adjustTextview = NO;
    
    if (self.offer.location.count > 0) {
        self.location = [self.offer.location anyObject];
    }
    
    
    [self createSubviews];
    [self manuallyLayoutSubviews];
    [self updateDisance];
    
    
    NSString* imagePath = [ImagePersistor imageFileExists:self.offer.merchantId imageType:DEFAULT_GIVE_IMAGE_TYPE];
    if(imagePath != nil){
        self.giveImage.image = self.imageToPost = [[UIImage alloc]initWithContentsOfFile:imagePath];
    }
    
    if( ![UIDevice hasFourInchDisplay] ){
        CGRect retina35CropRect = CGRectMake(0, 74, 640, 436);
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
    [[NSNotificationCenter defaultCenter]removeObserver:self name:kKikbakImageDownloaded object:nil];
}

-(void)viewDidLayoutSubviews{
  //  [self manuallyLayoutSubviews];
}

#pragma mark - Layout
-(void)manuallyLayoutSubviews{
    if(![UIDevice hasFourInchDisplay]){
        self.giveImage.frame = CGRectMake(0, 0, 320, 218);
        self.imageOverlay.frame = CGRectMake(0, 0, 320, 218);
        [self.takePhoto removeFromSuperview];
        self.takePictureBtn.frame = CGRectMake(112, 20, 95, 95);
        self.retailerName.frame = CGRectMake(14, 123, 316, 26);
        self.mapIcon.frame = CGRectMake(14, 152, 10, 14);
        self.distance.frame = CGRectMake(30, 151, 70, 18);
        self.mapBtn.frame = CGRectMake(14, 149, 70, 30);
        self.webIcon.frame = CGRectMake(109, 148, 19, 19);
        self.webBtn.frame = CGRectMake(99, 148, 30, 30);
        self.callIcon.frame = CGRectMake(145, 148, 15, 18);
        self.callBtn.frame = CGRectMake(140, 148, 30, 30);
        self.captionContainerView.frame = CGRectMake(0, TEXT_EDIT_CONTAINER_ORIGIN_Y_35_SCREEN, 320, 48);
        self.captionTextView.frame = CGRectMake(10, 8, 300, 32);
        self.dottedSeperator.frame = CGRectMake(0, 216, 320, 2);
        self.giftDesctription.frame = CGRectMake(0, 230, 320, 33);
        self.giftIcon.frame = CGRectMake(self.giftIcon.frame.origin.x, 237, 19, 18);
        self.giftDescriptionOptional.frame = CGRectMake(0, 264, 320, 15);
        self.seperator.frame = CGRectMake(11, 287, 298, 1);
        self.rewardDescription.frame = CGRectMake(0, 292, 320, 30);
        self.rewardDescriptionOptional.frame = CGRectMake(0, 322, 320, 15);
        self.termsBtn.frame = CGRectMake(11, 344, 150, 16);
        self.giveBtn.frame = CGRectMake(11, 366, 298, 40);
    }
}

-(void)createSubviews{
    
    self.view.backgroundColor = UIColorFromRGB(0xFFFFFF);
    
    self.giveImage = [[UIImageView alloc]initWithFrame:CGRectMake(0, 0, 320, 292)];
    [self.view addSubview:self.giveImage];
    
    
    self.imageOverlay = [[UIImageView alloc]initWithFrame:CGRectMake(0, 0, 320, 292)];
    self.imageOverlay.image = [UIImage imageNamed:@"grd_give_default_photo_gradient"];
    [self.view addSubview:self.imageOverlay];
    
    self.takePhoto = [[UILabel alloc]initWithFrame:CGRectMake(0, 44, 320, 16)];
    self.takePhoto.text = NSLocalizedString(@"Take a photo", nil);
    self.takePhoto.font = [UIFont fontWithName:@"HelveticaNeue-Medium" size:16];
    self.takePhoto.textColor = [UIColor whiteColor];
    self.takePhoto.textAlignment = NSTextAlignmentCenter;
    self.takePhoto.backgroundColor = [UIColor clearColor];
    [self.view addSubview:self.takePhoto];
    
    self.takePictureBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    [self.takePictureBtn setImage:[UIImage imageNamed:@"ic_camera"] forState:UIControlStateNormal];
    self.takePictureBtn.frame = CGRectMake(112, 68, 95, 95);
    [self.takePictureBtn addTarget:self action:@selector(onTakePhotoBtn:) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:self.takePictureBtn];
    
    
    self.retailerName = [[UILabel alloc]initWithFrame:CGRectMake(14, 194, 316, 26)];
    self.retailerName.font = [UIFont fontWithName:@"HelveticaNeue" size:24];
    self.retailerName.textColor = UIColorFromRGB(0xFFFFFF);
    self.retailerName.text = self.offer.merchantName;
    self.retailerName.textAlignment = NSTextAlignmentLeft;
    self.retailerName.backgroundColor = [UIColor clearColor];
    [self.view addSubview:self.retailerName];
    
    self.mapIcon = [[UIImageView alloc]initWithFrame:CGRectMake(14, 225, 10, 14)];
    self.mapIcon.image = [UIImage imageNamed:@"ic_map_give"];
    [self.view addSubview:self.mapIcon];
    
    self.distance = [[UILabel alloc] initWithFrame:CGRectMake(30, 223, 70, 18)];
    self.distance.font = [UIFont fontWithName:@"HelveticaNeue-Medium" size:18];
    self.distance.textColor = UIColorFromRGB(0xFFFFFF);
    self.distance.backgroundColor = [UIColor clearColor];
    [self.view addSubview:self.distance];
    
    
    self.mapBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    self.mapBtn.frame = CGRectMake(14, 211, 70, 30);
    self.mapBtn.backgroundColor = [UIColor clearColor];
    [self.mapBtn addTarget:self action:@selector(onMapBtn:) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:self.mapBtn];
    
    self.webIcon = [[UIImageView alloc]initWithFrame:CGRectMake(109, 221, 19, 19)];
    self.webIcon.image = [UIImage imageNamed:@"ic_web_give"];
    [self.view addSubview:self.webIcon];
    

    self.webBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    self.webBtn.backgroundColor = [UIColor clearColor];
    self.webBtn.frame = CGRectMake(99, 210, 30, 30);
    self.webBtn.backgroundColor = [UIColor clearColor];
    [self.webBtn addTarget:self action:@selector(onWebBtn:) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:self.webBtn];
    
    
    self.callIcon = [[UIImageView alloc]initWithFrame:CGRectMake(150, 221, 15, 19)];
    self.callIcon.image = [UIImage imageNamed:@"ic_phone_give"];
    [self.view addSubview: self.callIcon];
    
    self.callBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    self.callBtn.frame = CGRectMake(145, 210, 30, 30);
    self.callBtn.backgroundColor = [UIColor clearColor];
    [self.callBtn addTarget:self action:@selector(onCallBtn:) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:self.callBtn];
    
    
    self.captionContainerView = [[UIView alloc]initWithFrame:CGRectMake(0, TEXT_EDIT_CONTAINER_ORIGIN_Y, 320, 48)];
    [self.view addSubview:self.captionContainerView];
    
    self.captionGradient = [[UIImageView alloc]initWithFrame:self.captionContainerView.frame];
    self.captionGradient.image = [UIImage imageNamed:@"grd_give_caption"];
    [self.captionContainerView addSubview:self.captionGradient];
    
    self.captionTextView = [[HPGrowingTextView alloc]initWithFrame:CGRectMake(10, 8, 300, 32)];
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
    
    self.dottedSeperator = [[UIImageView alloc] initWithFrame:CGRectMake(0, 290, 320, 2)];
    self.dottedSeperator.image = [UIImage imageNamed:@"separator_dots"];
    [self.view addSubview:self.dottedSeperator];
    
    
    self.giftDesctription = [[UILabel alloc]initWithFrame:CGRectMake(0, 305, 320, 33)];
    self.giftDesctription.font = [UIFont fontWithName:@"HelveticaNeue-Medium" size:27];
    self.giftDesctription.text = self.offer.giftDescription;
    self.giftDesctription.textColor = UIColorFromRGB(0x2A80E6);
    self.giftDesctription.textAlignment = NSTextAlignmentCenter;
    self.giftDesctription.backgroundColor = [UIColor clearColor];
    [self.view addSubview:self.giftDesctription];
    
    CGSize gdSize = [self.giftDesctription.text sizeWithFont:self.giftDesctription.font];
    self.giftIcon = [[UIImageView alloc]initWithFrame:CGRectMake((self.view.frame.size.width/2) - (gdSize.width/2)- 24, 312, 19, 18)];
    self.giftIcon.image = [UIImage imageNamed:@"ic_gift"];
    [self.view addSubview:self.giftIcon];

    self.giftDescriptionOptional = [[UILabel alloc]initWithFrame:CGRectMake(0, 338, 320, 15)];
    self.giftDescriptionOptional.font = [UIFont fontWithName:@"HelveticaNeue" size:13];
    self.giftDescriptionOptional.text = self.offer.giftDescriptionOptional;
    self.giftDescriptionOptional.textColor = UIColorFromRGB(0x898989);
    self.giftDescriptionOptional.textAlignment = NSTextAlignmentCenter;
    self.giftDescriptionOptional.backgroundColor = [UIColor clearColor];
    [self.view addSubview:self.giftDescriptionOptional];

    self.seperator = [[UIImageView alloc]initWithFrame:CGRectMake(11, 364, 298, 1)];
    self.seperator.image = [UIImage imageNamed:@"separator_gray_line"];
    [self.view addSubview:self.seperator];

    self.rewardDescription = [[UILabel alloc]initWithFrame:CGRectMake(0, 370, 320, 30)];
    self.rewardDescription.font = [UIFont fontWithName:@"HelveticaNeue-Medium" size:25];
    self.rewardDescription.text = self.offer.kikbakDescription;
    self.rewardDescription.textColor = UIColorFromRGB(0x3a3a3a);
    self.rewardDescription.textAlignment = NSTextAlignmentCenter;
    self.rewardDescription.backgroundColor = [UIColor clearColor];
    [self.view addSubview:self.rewardDescription];

    
    self.rewardDescriptionOptional = [[UILabel alloc]initWithFrame:CGRectMake(0, 401, 320, 15)];
    self.rewardDescriptionOptional.font = [UIFont fontWithName:@"HelveticaNeue" size:13];
    self.rewardDescriptionOptional.text = self.offer.kikbakDescriptionOptional;
    self.rewardDescriptionOptional.textColor = UIColorFromRGB(0x898989);
    self.rewardDescriptionOptional.textAlignment = NSTextAlignmentCenter;
    self.rewardDescriptionOptional.backgroundColor = [UIColor clearColor];
    [self.view addSubview:self.rewardDescriptionOptional];
    
    self.termsBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    self.termsBtn.frame = CGRectMake(11, 424, 150, 16);
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
    self.giveBtn.frame = CGRectMake(11, 453, 298, 40);
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
        frOverlay.origin.y = fr.origin.y - 20;
        frOverlay.size.height = self.giveImage.frame.size.height - frOverlay.origin.y;
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
        [view createsubviews];
        view.delegate = self;
        [self.view addSubview:view];
    }
}

-(IBAction)onTakePhotoBtn:(id)sender{
    [self resignTextView];

    if([UIImagePickerController isSourceTypeAvailable:UIImagePickerControllerSourceTypeCamera]){
        UIImagePickerController* picker = [[UIImagePickerController alloc]init];
        picker.view.frame = self.view.frame;
        picker.sourceType = UIImagePickerControllerSourceTypeCamera;
        picker.delegate = self;
        
        UIView* overlay = [[UIView alloc]initWithFrame:picker.view.frame];
        UIImageView* square = [[UIImageView alloc]initWithFrame:overlay.frame];
        if([UIDevice hasFourInchDisplay]){
            
            square.image = [UIImage imageNamed:@"camera_screen_area-h536"];
        }
        else{
            square.image = [UIImage imageNamed:@"camera_screen_area"];
        }
        [overlay addSubview:square];
        picker.cameraOverlayView = overlay;
        
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
    UIImage* grd = [UIImage imageNamed:@"grd_give_img"];
    self.imageOverlay.image = grd;
    CGRect fr = self.imageOverlay.frame;
    fr.origin.y =  self.retailerName.frame.origin.y - 20;//self.captionContainerView.frame.origin.y - grd.size.height;
    fr.size.height = self.imageOverlay.frame.size.height - fr.origin.y;
    self.imageOverlay.frame = fr;

    [self.takePhoto removeFromSuperview];
    self.takePictureBtn.frame = CGRectMake(265, 0, 55, 55);
    [self.takePictureBtn setImage:[UIImage imageNamed:@"ic_post_give_camera"] forState:UIControlStateNormal];
    
    CGRect cropRect = CGRectMake(8, 94, 624, 624);
    UIImage* image = [info valueForKey:UIImagePickerControllerOriginalImage];
    image = [image imageByScalingAndCroppingForSize:CGSizeMake(640, 960)];
    
    self.imageToPost = [image imageCropToRect:cropRect];
    if( [UIDevice hasFourInchDisplay]){
        self.giveImage.image = self.imageToPost;
    }
    else{
        CGRect retina35CropRect = CGRectMake(8, 131, 624, 587);
        self.giveImage.image = [image imageCropToRect:retina35CropRect];
    }
    [self dismissViewControllerAnimated:YES completion:nil];
    photoTaken = YES;
}

- (void)imagePickerControllerDidCancel:(UIImagePickerController *)picker{
    
    [self dismissViewControllerAnimated:YES completion:nil];
}


#pragma mark - keyboard options
-(IBAction)keyboardWillShow:(NSNotification*)notification{
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
    if(self.captionContainerView.frame.origin.y != containerFrame.origin.y){
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
        containerFrame.origin.y = (containerFrame.size.height > DEFAULT_CONTAINER_VIEW_HEIGHT)?TEXT_EDIT_CONTAINER_ORIGIN_Y - (containerFrame.size.height - DEFAULT_CONTAINER_VIEW_HEIGHT): TEXT_EDIT_CONTAINER_ORIGIN_Y;
    }
    else{
        containerFrame.origin.y = (containerFrame.size.height > DEFAULT_CONTAINER_VIEW_HEIGHT)? TEXT_EDIT_CONTAINER_ORIGIN_Y_35_SCREEN - (containerFrame.size.height - DEFAULT_CONTAINER_VIEW_HEIGHT): TEXT_EDIT_CONTAINER_ORIGIN_Y_35_SCREEN;
    }
	
	// animations settings
	[UIView beginAnimations:nil context:NULL];
	[UIView setAnimationBeginsFromCurrentState:YES];
    [UIView setAnimationDuration:[duration doubleValue]];
    [UIView setAnimationCurve:(UIViewAnimationCurve)[curve intValue]];
    
	// set views with new info
    // set views with new info
    if(self.captionContainerView.frame.origin.y != containerFrame.origin.y){
        CGFloat delta = self.captionContainerView.frame.origin.y - containerFrame.origin.y;
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
        growingTextView.font = [UIFont fontWithName:@"HelveticaNeue-Light" size:14.0];
        growingTextView.textColor = [UIColor blackColor];
    }
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
            [picker setMessageBody:result.body isHTML:YES];
            
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
    else{
        [FBSettings setLoggingBehavior:[NSSet setWithObject:FBLoggingBehaviorFBRequests]];
        FBCouponObject* obj = [[FBCouponObject alloc]init];
        obj.caption = self.captionTextView.text;
        obj.merchant = self.retailerName.text;
        obj.locationId = self.chosenlocation;
        obj.employeeName = self.employeeName;
        obj.landingUrl = result.landingUrl;
        obj.gift = self.giftDesctription.text;
        obj.detailedDescription = self.giftDescriptionOptional.text;
        [obj postCoupon:self.imageUrl];
    }
    
}

-(void) onShareError:(NSNotification*)notification{
    UIAlertView* alert = [[UIAlertView alloc]initWithTitle:@"Hmmm..." message:NSLocalizedString(@"Unreachable", nil) delegate:nil cancelButtonTitle:@"Ok" otherButtonTitles:nil];
    alert.tag = CALL_URL_TAG;
    [alert show];
    [self.spinnerView removeFromSuperview];
}


-(void) onImageUploadSuccess:(NSNotification*)notification{
    [self.spinnerView removeFromSuperview];
    
    self.imageUrl = [notification object];
    CGRect frame = ((AppDelegate*)[UIApplication sharedApplication].delegate).window.frame;
    ShareChannelSelectorView* view = [[ShareChannelSelectorView alloc]initWithFrame:frame];
    view.locations = self.offer.location;
    [view createsubviews];
    view.delegate = self;
    [self.view addSubview:view];
}

-(void) onImageUploadError:(NSNotification*)notification{
    UIAlertView* alert = [[UIAlertView alloc]initWithTitle:@"Hmmm..." message:NSLocalizedString(@"Unreachable", nil) delegate:nil cancelButtonTitle:@"Ok" otherButtonTitles:nil];
    alert.tag = CALL_URL_TAG;
    [alert show];
    [self.spinnerView removeFromSuperview];
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
    
    CGRect frame = ((AppDelegate*)[UIApplication sharedApplication].delegate).window.frame;
    ShareSuccessView* shareView = [[ShareSuccessView alloc]initWithFrame:frame];
    shareView.delegate = self;
    [shareView manuallyLayoutSubviews];
    [((AppDelegate*)[UIApplication sharedApplication].delegate).window addSubview:shareView];
    
    
    [self.spinnerView removeFromSuperview];

}

-(void) onFBStoryPostError:(NSNotification *)notification{
    UIAlertView* alert = [[UIAlertView alloc]initWithTitle:@"Hmmm..." message:NSLocalizedString(@"Unreachable", nil) delegate:nil cancelButtonTitle:@"Ok" otherButtonTitles:nil];
    alert.tag = CALL_URL_TAG;
    [alert show];
    
    [self.spinnerView removeFromSuperview];
}

-(void)onImageDownloaded:(NSNotification*)notification{
    NSString* imagePath = [ImagePersistor imageFileExists:self.offer.merchantId imageType:DEFAULT_GIVE_IMAGE_TYPE];
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
    //todo: upload email
    shareViaEmail = YES;
    shareViaSMS = NO;
    ShareExperienceRequest* request = [[ShareExperienceRequest alloc]init];
    NSMutableDictionary* dict = [[NSMutableDictionary alloc]initWithCapacity:5];
    
    [dict setObject:self.offer.merchantId forKey:@"merchantId"];
    [dict setObject:locationId forKey:@"locationId"];
    [dict setObject:self.offer.offerId forKey:@"offerId"];
    [dict setObject:self.imageUrl forKey:@"imageUrl"];
    [dict setObject:@"email" forKey:@"type"];
    [dict setObject:@"ios" forKey:@"platform"];
    [dict setObject:name forKey:@"employeeId"];
    if( [self.captionTextView.text compare:NSLocalizedString(@"add comment", nil)] == NSOrderedSame ){
        [dict setObject:@"" forKey:@"caption"];
    }
    else{
        [dict setObject:self.captionTextView.text forKey:@"caption"];
    }
    [request restRequest:dict];

}

-(void)onSmsSelected:(NSNumber*)locationId withEmployeeName:(NSString*)name{
    shareViaEmail = NO;
    shareViaSMS = YES;
    ShareExperienceRequest* request = [[ShareExperienceRequest alloc]init];
    NSMutableDictionary* dict = [[NSMutableDictionary alloc]initWithCapacity:5];
    
    [dict setObject:self.offer.merchantId forKey:@"merchantId"];
    [dict setObject:locationId forKey:@"locationId"];
    [dict setObject:self.offer.offerId forKey:@"offerId"];
    [dict setObject:self.imageUrl forKey:@"imageUrl"];
    [dict setObject:@"sms" forKey:@"type"];
    [dict setObject:@"ios" forKey:@"platform"];
    [dict setObject:name forKey:@"employeeId"];
    if( [self.captionTextView.text compare:NSLocalizedString(@"add comment", nil)] == NSOrderedSame ){
        [dict setObject:@"" forKey:@"caption"];
    }
    else{
        [dict setObject:self.captionTextView.text forKey:@"caption"];
    }
    [request restRequest:dict];
}

-(void)onTimelineSelected:(NSNumber*)locationId withEmployeeName:(NSString*)name{
    CGRect frame = ((AppDelegate*)[UIApplication sharedApplication].delegate).window.frame;
    self.spinnerView = [[SpinnerView alloc]initWithFrame:frame];
    [self.spinnerView startActivity];
    [((AppDelegate*)[UIApplication sharedApplication].delegate).window addSubview:self.spinnerView];

    shareViaEmail = NO;
    shareViaSMS = NO;
    
    self.employeeName = name;
    self.chosenlocation = locationId;
    
    ShareExperienceRequest* request = [[ShareExperienceRequest alloc]init];
    NSMutableDictionary* dict = [[NSMutableDictionary alloc]initWithCapacity:5];
    [dict setObject:self.offer.merchantId forKey:@"merchantId"];
    [dict setObject:locationId forKey:@"locationId"];
    [dict setObject:self.offer.offerId forKey:@"offerId"];
    [dict setObject:name forKey:@"employeeId"];
    [dict setObject:self.imageUrl forKey:@"imageUrl"];
    [dict setObject:@"fb" forKey:@"type"];
    [dict setObject:@"ios" forKey:@"platform"];
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
