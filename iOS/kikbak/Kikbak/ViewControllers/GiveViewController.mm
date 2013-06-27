//
//  ShareViewController.m
//  kikit
//
//  Created by Ian Barile on 3/15/13.
//  Copyright (c) 2013 Ian Barile. All rights reserved.
//

#import "GiveViewController.h"
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
#import "UIButton+Util.h"

#define DEFAULT_CONTAINER_VIEW_HEIGHT 50
#define PHOTO_TAG  1000
#define CAPTION_TAG  1001
#define CALL_URL_TAG 1002

const double TEXT_EDIT_CONTAINER_ORIGIN_Y = 241.0;
const double TEXT_EDIT_CONTAINER_ORIGIN_Y_35_SCREEN = 170.0;

@interface GiveViewController ()

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



-(void)createSubviews;
-(void)manuallyLayoutSubviews;
-(void)adjustRetailerInfo:(CGFloat)delta;

-(IBAction)onTakePhotoBtn:(id)sender;
-(IBAction)onGiveGift:(id)sender;
-(IBAction)onLearnMore:(id)sender;
-(IBAction)onTerms:(id)sender;

-(IBAction)onBackBtn:(id)sender;
-(IBAction)onMapBtn:(id)sender;
-(IBAction)onCallBtn:(id)sender;
-(IBAction)onWebBtn:(id)sender;
-(void)postToFacebook;

-(void) onLocationUpdate:(NSNotification*)notification;
-(void) updateDisance;

-(IBAction)keyboardWillShow:(NSNotification*)notification;
-(IBAction)keyboardWillHide:(NSNotification*)notification;

-(id<KikbakOGCoupon>)CouponObject:(NSString*)caption;
-(void)postPhotoThenOpenGraphAction;
-(void)postOpenGraphActionWithPhotoURL:(NSString*)photoURL withImageId:(NSNumber*)fbImageId;

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
    photoTaken = NO;
    captionAdded = NO;
    
    if (self.offer.location.count > 0) {
        self.location = [self.offer.location anyObject];
    }
    
    
    [self createSubviews];
    [self manuallyLayoutSubviews];
    [self updateDisance];
    
    
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
    
}

-(void)viewDidAppear:(BOOL)animated{
    [super viewDidAppear:animated];
    
}

-(void)viewDidDisappear:(BOOL)animated{
    [super viewDidDisappear:animated];
    [[NSNotificationCenter defaultCenter]removeObserver:self name:UIKeyboardWillHideNotification object:nil];
    [[NSNotificationCenter defaultCenter]removeObserver:self name:UIKeyboardWillShowNotification object:nil];
    [[NSNotificationCenter defaultCenter]removeObserver:self name:kKikbakLocationUpdate object:nil];
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
        self.retailerName.frame = CGRectMake(14, 120, 316, 26);
        self.mapIcon.frame = CGRectMake(14, 152, 10, 14);
        self.distance.frame = CGRectMake(30, 151, 70, 18);
        self.mapBtn.frame = CGRectMake(14, 149, 70, 30);
        self.webIcon.frame = CGRectMake(109, 150, 19, 19);
        self.webBtn.frame = CGRectMake(99, 148, 30, 30);
        self.callIcon.frame = CGRectMake(145, 150, 15, 18);
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
    self.giveImage.image = [UIImage imageNamed:@"img_vz_photo"];
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
    self.captionContainerView.backgroundColor = [UIColor colorWithRed:0.0 green:0.0 blue:0.0 alpha:1];
    [self.view addSubview:self.captionContainerView];
    
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
    self.captionTextView.internalTextView.layer.cornerRadius = 5;
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
    
    fr = self.callIcon.frame;
    fr.origin.y -= delta;
    self.callIcon.frame = fr;
    fr = self.callBtn.frame;
    fr.origin.y -= delta;
    self.callBtn.frame = fr;
}

#pragma mark - btn actions
-(IBAction)onBackBtn:(id)sender{
    [self.navigationController popViewControllerAnimated:YES];

}

-(IBAction)onGiveGift:(id)sender{
    
//    if(!photoTaken){
//        UIAlertView* alert = [[UIAlertView alloc]initWithTitle:nil message:@"Gifts should come\n with there own photo" delegate:self cancelButtonTitle:@"Next Time" otherButtonTitles: @"Take Photo", nil];
//        alert.tag = PHOTO_TAG;
//        [alert show];
//        return;
//    }
//    
//    if(!captionAdded){
//        UIAlertView* alert = [[UIAlertView alloc]initWithTitle:nil
//                                                       message:@"Do you want to tell your\n friend abour your photo?"
//                                                      delegate:self
//                                             cancelButtonTitle:@"No"
//                                             otherButtonTitles: @"Yes", nil];
//        alert.tag = CAPTION_TAG;
//        [alert show];
//        return;
//    }
    

    [self postToFacebook];
   // [self postPhotoThenOpenGraphAction];
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

-(IBAction)onLearnMore:(id)sender{
    
}

-(IBAction)onTerms:(id)sender{
    
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
 
    if((alertView.tag == PHOTO_TAG || alertView.tag == CAPTION_TAG) && buttonIndex == 0) {
        [self postToFacebook];
        //[self postPhotoThenOpenGraphAction];
    }
}


#pragma mark - image picker delegates
- (void)imagePickerController:(UIImagePickerController *)picker didFinishPickingMediaWithInfo:(NSDictionary *)info{
    [self.imageOverlay removeFromSuperview];
    [self.takePhoto removeFromSuperview];
    self.takePictureBtn.frame = CGRectMake(265, 11, 55, 55);
    [self.takePictureBtn setImage:[UIImage imageNamed:@"ic_post_give_camera"] forState:UIControlStateNormal];
    
    CGRect cropRect = CGRectMake(10, 50, 500, 500);
    UIImage* image = [info valueForKey:UIImagePickerControllerOriginalImage];
    image = [image imageByScalingAndCroppingForSize:CGSizeMake(640, 960)];
    self.giveImage.image =  [image imageCropToRect:cropRect];
    [self dismissViewControllerAnimated:YES completion:nil];
    photoTaken = YES;
}

- (void)imagePickerControllerDidCancel:(UIImagePickerController *)picker{
    
    [self dismissViewControllerAnimated:YES completion:nil];
}



#pragma mark - facebook
-(void)postToFacebook{
    
    CGRect frame = ((AppDelegate*)[UIApplication sharedApplication].delegate).window.frame;
    self.spinnerView = [[SpinnerView alloc]initWithFrame:frame];
    [self.spinnerView startActivity];
    [((AppDelegate*)[UIApplication sharedApplication].delegate).window addSubview:self.spinnerView];

    
    self.giveBtn.enabled = NO;
    FBRequestConnection* connection = [[FBRequestConnection alloc]initWithTimeout:30];
    
    
    FBRequest* request = [FBRequest requestForUploadPhoto:[self.giveImage.image imageByScalingAndCroppingForSize:CGSizeMake(300, 300)]];
    if( [self.captionTextView.text compare:NSLocalizedString(@"add comment", nil)] == NSOrderedSame ){
        [request.parameters setObject:[NSString stringWithFormat:@"%@.\n\nVisit getkikbak.com for an exclusive offer shared by your friend", self.captionTextView.text] forKey:@"name"];
    }
    else{
        [request.parameters setObject:@"Visit getkikbak.com for an exclusive offer shared by your friend" forKey:@"name"];
    }
    
    [connection addRequest:request completionHandler:^(FBRequestConnection *connection, id result, NSError *error) {
        if(error == nil){
            [Flurry logEvent:@"ShareEvent" timed:YES];

            [self.spinnerView removeFromSuperview];
            ShareSuccessView* shareView = [[ShareSuccessView alloc]initWithFrame:frame];
            [shareView manuallyLayoutSubviews];
            [((AppDelegate*)[UIApplication sharedApplication].delegate).window addSubview:shareView];
            ShareExperienceRequest* request = [[ShareExperienceRequest alloc]init];
            NSMutableDictionary* dict = [[NSMutableDictionary alloc]initWithCapacity:5];

            [dict setObject:self.offer.merchantId forKey:@"merchantId"];
            [dict setObject:self.location.locationId forKey:@"locationId"];
            [dict setObject:self.offer.offerId forKey:@"offerId"];
            [dict setObject:[result objectForKey:@"id"] forKey:@"fbImageId"];
            if( [self.captionTextView.text compare:NSLocalizedString(@"add comment", nil)] == NSOrderedSame ){
                [dict setObject:@"" forKey:@"caption"];
            }
            else{
                [dict setObject:self.captionTextView.text forKey:@"caption"];
            }
            [request restRequest:dict];
        }
        else{
            [self.spinnerView removeFromSuperview];
            [Flurry logEvent:@"FailedShareEvent" timed:YES];
            NSLog(@"Submit Error: %@", error);
        }
        //for testing
        //self.giveBtn.enabled = YES;
    }];
    
    
    [connection start];
    
}

-(id<KikbakOGCoupon>)CouponObject:(NSString*)caption{
    // This URL is specific to this sample, and can be used to
    // create arbitrary OG objects for this app; your OG objects
    // will have URLs hosted by your server.
    NSString *format =
    @"https://young-springs-3453.herokuapp.com/repeater.php?"
    @"fb:app_id=493383324061333&og:type=%@&"
    @"og:title=%@&og:description=%%22%@%%22&"
    @"og:image=https://s-static.ak.fbcdn.net/images/devsite/attachment_blank.png&"
    @"og:message=testing 1234&"
    @"body=%@";
    
    // We create an FBGraphObject object, but we can treat it as
    // an SCOGMeal with typed properties, etc. See <FacebookSDK/FBGraphObject.h>
    // for more details.
    id<KikbakOGCoupon> result = (id<KikbakOGCoupon>)[FBGraphObject graphObject];
    
    // Give it a URL that will echo back the name of the meal as its title,
    // description, and body.
    result.url = [NSString stringWithFormat:format,
                  @"referredlabs:coupon", @"Testing", caption, caption];
    
    return result;
}

- (void)postPhotoThenOpenGraphAction{
    self.giveBtn.enabled = NO;
    
    FBRequestConnection *connection = [[FBRequestConnection alloc] init];
    
    // First request uploads the photo.
    FBRequest *request1 = [FBRequest
                           requestForUploadPhoto:[self.giveImage.image imageByScalingAndCroppingForSize:CGSizeMake(480, 480)]];
    [request1.parameters setObject:@"To get gift install kikbak, @[http://en.wikipedia.org/wiki/Cyan]" forKey:@"name"];
    [connection addRequest:request1
         completionHandler:
     ^(FBRequestConnection *connection, id result, NSError *error) {
         if (!error) {
         }
     }
            batchEntryName:@"photopost"
     ];
    
    // Second request retrieves photo information for just-created
    // photo so we can grab its source.
    FBRequest *request2 = [FBRequest
                           requestForGraphPath:@"{result=photopost:$.id}"];
    [connection addRequest:request2
         completionHandler:
     ^(FBRequestConnection *connection, id result, NSError *error) {
         if (!error &&
             result) {
             NSString *source = [result objectForKey:@"source"];
             NSNumber* fbImageId = [result objectForKey:@"id"];
             [self postOpenGraphActionWithPhotoURL:source withImageId:fbImageId];
         }
     }
     ];
    
    [connection start];
}


-(void)postOpenGraphActionWithPhotoURL:(NSString*)photoURL withImageId:(NSNumber*)fbImageId{
    // First create the Open Graph meal object for the meal we ate.
    id<KikbakOGCoupon> couponObject = [self CouponObject:self.captionTextView.text];
    
    // Now create an Open Graph eat action with the meal, our location,
    // and the people we were with.
    id<KikbakOGShareCouponAction> action = (id<KikbakOGShareCouponAction>)[FBGraphObject graphObject];
    action.coupon = couponObject;
    
    if (photoURL) {
        NSMutableDictionary *image = [[NSMutableDictionary alloc] init];
        [image setObject:photoURL forKey:@"url"];
        [image setObject:@"true" forKey:@"user_generated"];
        [image setObject:@"true" forKey:@"fb:explicitly_shared"];
        [image setObject:@"this is a test message" forKey:@"description"];
        
        NSMutableArray *images = [[NSMutableArray alloc] init];
        [images addObject:image];
        
        action.image = images;
    }
    
    // Create the request and post the action to the
    // "me/<YOUR_APP_NAMESPACE>:eat" path.
    [FBRequestConnection startForPostWithGraphPath:@"me/referredlabs:share"
                                       graphObject:action
                                 completionHandler:
     ^(FBRequestConnection *connection, id result, NSError *error) {
         if (!error) {
             CGRect frame = ((AppDelegate*)[UIApplication sharedApplication].delegate).window.frame;
             ShareSuccessView* shareView = [[ShareSuccessView alloc]initWithFrame:frame];
             [shareView manuallyLayoutSubviews];
             [((AppDelegate*)[UIApplication sharedApplication].delegate).window addSubview:shareView];
             ShareExperienceRequest* request = [[ShareExperienceRequest alloc]init];
             NSMutableDictionary* dict = [[NSMutableDictionary alloc]initWithCapacity:5];
             
             [dict setObject:self.offer.merchantId forKey:@"merchantId"];
             [dict setObject:self.location.locationId forKey:@"locationId"];
             [dict setObject:self.offer.offerId forKey:@"offerId"];
             [dict setObject:fbImageId forKey:@"fbImageId"];
             if( [self.captionTextView.text compare:NSLocalizedString(@"add comment", nil)] == NSOrderedSame ){
                 [dict setObject:@"" forKey:@"caption"];
             }
             else{
                 [dict setObject:self.captionTextView.text forKey:@"caption"];
             }
             [request restRequest:dict];
         } else {
             NSLog(@"%@, %@",[NSString stringWithFormat:
                          @"error: domain = %@, code = %d",
                          error.domain, error.code], error.userInfo);
         }

     }
     ];
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
    captionAdded = true;
	[self.captionTextView resignFirstResponder];
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
}

- (BOOL)growingTextViewShouldBeginEditing:(HPGrowingTextView *)growingTextView{
    if( [growingTextView.text compare:NSLocalizedString(@"add comment", nil)] == NSOrderedSame ){
        growingTextView.text = @"";
        growingTextView.font = [UIFont fontWithName:@"HelveticaNeue-Light" size:14.0];
        growingTextView.textColor = [UIColor blackColor];
    }
    return YES;
}


#pragma mark - NSNotification Center 
-(void) onLocationUpdate:(NSNotification*)notification{
    [self updateDisance];
}

-(void) updateDisance{
    CLLocation* current = [[CLLocation alloc]initWithLatitude:self.location.latitude.doubleValue longitude:self.location.longitude.doubleValue];
    self.distance.text = [NSString stringWithFormat:NSLocalizedString(@"miles away", nil),
                          [Distance distanceToInMiles:current]];
    
     
}

@end
