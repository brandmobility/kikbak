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

#define DEFAULT_CONTAINER_VIEW_HEIGHT 50

@interface GiveViewController ()

@property (nonatomic, strong) Location* location;


@property(nonatomic, strong) UIView* merchantBackground;
@property(nonatomic, strong) UIImageView* retailerLogo;
@property(nonatomic, strong) UILabel* retailerName;
@property(nonatomic, strong) UIImageView* mapMarker;
@property(nonatomic, strong) UILabel* distance;
@property(nonatomic, strong) UIImageView* callIcon;
@property(nonatomic, strong) UILabel* call;
@property(nonatomic, strong) UIImageView* webIcon;
@property(nonatomic, strong) UILabel* web;


@property(nonatomic, strong) UIImageView* giveImage;
@property(nonatomic, strong) UIView* imageOverlay;
@property(nonatomic, strong) UIButton* takePictureBtn;

@property(nonatomic, strong) UIView* captionContainerView;
@property(nonatomic, strong) HPGrowingTextView* captionTextView;

@property(nonatomic, strong) UIView* dealBackground;
@property(nonatomic, strong) UILabel* giveLabel;
@property(nonatomic, strong) UIImageView* seperator;
@property(nonatomic, strong) UILabel* getLabel;
@property(nonatomic, strong) UIButton* termsBtn;
@property(nonatomic, strong) UIButton* learnBtn;

@property(nonatomic, strong) UIButton* giveBtn;



-(void)createSubviews;
-(void)manuallyLayoutSubviews;

-(IBAction)takePhoto:(id)sender;
-(IBAction)onGiveGift:(id)sender;
-(IBAction)onLearnMore:(id)sender;
-(void)postToFacebook;

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
    photoTaken = NO;
    captionAdded = NO;
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
    if (self.offer.location.count > 0) {
        self.location = [self.offer.location anyObject];
    }

    
    [self createSubviews];
    [self manuallyLayoutSubviews];
}

-(void)viewDidAppear:(BOOL)animated{
    [super viewDidAppear:animated];
}

-(void)viewDidDisappear:(BOOL)animated{
    [super viewDidDisappear:animated];
    [[NSNotificationCenter defaultCenter]removeObserver:self name:UIKeyboardWillHideNotification object:nil];
    [[NSNotificationCenter defaultCenter]removeObserver:self name:UIKeyboardWillShowNotification object:nil];
}

-(void)viewDidLayoutSubviews{
  //  [self manuallyLayoutSubviews];
}


-(void)manuallyLayoutSubviews{
    if(![UIDevice hasFourInchDisplay]){
        self.giveImage.frame = CGRectMake(0, 0, 320, 275);
        self.imageOverlay.frame = CGRectMake(0, 60, 320, 215);
        self.takePictureBtn.frame = CGRectMake(100, 72, 121, 121);
        self.merchantBackground.frame = CGRectMake(0, 0, 320, 60);
        self.retailerLogo.frame = CGRectMake(11, 9, 42, 42);
        self.retailerName.frame = CGRectMake(66, 8, 254, 24);
        self.mapMarker.frame = CGRectMake(66, 41, 13, 12);
        self.distance.frame = CGRectMake(80, 40, 90, 13);
        self.callIcon.frame = CGRectMake(175, 40, 13, 12);
        self.call.frame = CGRectMake(190, 40, 20, 13);
        self.webIcon.frame = CGRectMake(246, 40, 12, 12);
        self.web.frame = CGRectMake(262, 40, 30, 13);
        self.captionContainerView.frame = CGRectMake(0, 203, 320, 48);
        self.captionTextView.frame = CGRectMake(10, 8, 300, 32);
        self.dealBackground.frame = CGRectMake(0, 251, 320, 100);
        self.giveLabel.frame = CGRectMake(0, 8, 320, 20);
        self.seperator.frame = CGRectMake(11, 34, 298, 1);
        self.getLabel.frame = CGRectMake(0, 43, 320, 26);
        self.termsBtn.frame = CGRectMake(10, 78, 150, 14);
        self.learnBtn.frame = CGRectMake(160, 78, 150, 14);
        self.giveBtn.frame = CGRectMake(10, 362, 300, 44);
    }
}

-(void)createSubviews{
    
    self.view.backgroundColor = UIColorFromRGB(0xf5f5f5);
    
    self.giveImage = [[UIImageView alloc]initWithFrame:CGRectMake(0, 0, 320, 320)];
    self.giveImage.image = [UIImage imageNamed:@"sample.jpg"];
    [self.view addSubview:self.giveImage];
    
    self.imageOverlay = [[UIView alloc]initWithFrame:CGRectMake(0, 65, 320, 219)];
    self.imageOverlay.backgroundColor = UIColorFromRGBWithOpacity(0xFFFFFF, .5);
    [self.view addSubview:self.imageOverlay];
    
    self.takePictureBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    [self.takePictureBtn setImage:[UIImage imageNamed:@"add_picture"] forState:UIControlStateNormal];
    self.takePictureBtn.frame = CGRectMake(100, 113, 121, 121);
    [self.takePictureBtn addTarget:self action:@selector(takePhoto:) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:self.takePictureBtn];
    
    
    self.merchantBackground = [[UIView alloc]initWithFrame:CGRectMake(0, 0, 320, 65)];
    self.merchantBackground.backgroundColor = [UIColor colorWithRed:0.0 green:0.0 blue:0.0 alpha:0.7];
    [self.view addSubview:self.merchantBackground];
    
    self.retailerLogo = [[UIImageView alloc]initWithFrame:CGRectMake(11, 11, 42, 42)];
    self.retailerLogo.image = [UIImage imageNamed:@"logo"];
    self.retailerLogo.backgroundColor = [UIColor clearColor];
    [self.merchantBackground addSubview:self.retailerLogo];
    
    self.retailerName = [[UILabel alloc]initWithFrame:CGRectMake(66, 10, 254, 24)];
    self.retailerName.font = [UIFont fontWithName:@"HelveticaNeue-Light" size:24];
    self.retailerName.textColor = UIColorFromRGB(0xf5f5f5);
    self.retailerName.text = self.offer.merchantName;
    self.retailerName.backgroundColor = [UIColor clearColor];
    [self.merchantBackground addSubview:self.retailerName];
    
    self.mapMarker = [[UIImageView alloc]initWithFrame:CGRectMake(66, 41, 13, 12)];
    self.mapMarker.image = [UIImage imageNamed:@"map_marker"];
    [self.merchantBackground addSubview:self.mapMarker];
    
    self.distance = [[UILabel alloc] initWithFrame:CGRectMake(80, 40, 90, 13)];
    self.distance.font = [UIFont fontWithName:@"HelveticaNeue-Light" size:12];
    self.distance.textColor = UIColorFromRGB(0xf5f5f5);
    self.distance.text = [NSString stringWithFormat:NSLocalizedString(@"miles away", nil),
                          [Distance distanceToInMiles:
                           [[CLLocation alloc]initWithLatitude:
                            self.location.latitude.doubleValue
                                                     longitude:self.location.longitude.doubleValue]]];
    self.distance.backgroundColor = [UIColor clearColor];
    [self.merchantBackground addSubview:self.distance];
    
    self.callIcon = [[UIImageView alloc]initWithFrame:CGRectMake(175, 40, 13, 12)];
    self.callIcon.image = [UIImage imageNamed:@"phone_icon"];
    [self.merchantBackground addSubview:self.callIcon];
    
    self.call = [[UILabel alloc]initWithFrame:CGRectMake(190, 40, 20, 13)];
    self.call.font = [UIFont fontWithName:@"HelveticaNeue-Light" size:12];
    self.call.textColor = UIColorFromRGB(0xf5f5f5);
    self.call.text = NSLocalizedString(@"call", nil);
    self.call.backgroundColor = [UIColor clearColor];
    [self.merchantBackground addSubview:self.call];
    
    self.webIcon = [[UIImageView alloc]initWithFrame:CGRectMake(246, 40, 12, 12)];
    self.webIcon.image = [UIImage imageNamed:@"web_icon"];
    [self.merchantBackground addSubview:self.webIcon];
    
    self.web = [[UILabel alloc]initWithFrame:CGRectMake(262, 40, 30, 13)];
    self.web.font = [UIFont fontWithName:@"HelveticaNeue-Light" size:12];
    self.web.textColor = UIColorFromRGB(0xf5f5f5);
    self.web.text = NSLocalizedString(@"web", nil);
    self.web.backgroundColor = [UIColor clearColor];
    [self.merchantBackground addSubview:self.web];
    
    self.captionContainerView = [[UIView alloc]initWithFrame:CGRectMake(0, 284, 320, 48)];
    self.captionContainerView.backgroundColor = [UIColor colorWithRed:0.0 green:0.0 blue:0.0 alpha:0.7];
    [self.view addSubview:self.captionContainerView];
    
    self.captionTextView = [[HPGrowingTextView alloc]initWithFrame:CGRectMake(10, 8, 300, 32)];
    self.captionTextView.text = NSLocalizedString(@"add comment", nil);
    self.captionTextView.contentInset = UIEdgeInsetsMake(9, 0, 8, 0);
    self.captionTextView.layer.cornerRadius = 5.0;
    
    self.captionTextView.minNumberOfLines = 1;
	self.captionTextView.maxNumberOfLines = 2;
	self.captionTextView.returnKeyType = UIReturnKeyDone; //just as an example
	self.captionTextView.font = [UIFont fontWithName:@"HelveticaNeue-Light" size:14.0f];
	self.captionTextView.delegate = self;
    self.captionTextView.backgroundColor = [UIColor whiteColor];
    [self.captionContainerView addSubview:self.captionTextView];
    
    self.dealBackground = [[UIView alloc]initWithFrame:CGRectMake(0, 332, 320, 110)];
    self.dealBackground.backgroundColor = UIColorFromRGB(0xF0F0F0);
    [self.view addSubview:self.dealBackground];
    
    self.giveLabel = [[UILabel alloc]initWithFrame:CGRectMake(0, 14, 320, 20)];
    self.giveLabel.font = [UIFont fontWithName:@"HelveticaNeue-Light" size:18];
    self.giveLabel.text = @"Give $10 off $50";
    self.giveLabel.textColor = UIColorFromRGB(0x3a3a3a);
    self.giveLabel.textAlignment = NSTextAlignmentCenter;
    self.giveLabel.backgroundColor = [UIColor clearColor];
    [self.dealBackground addSubview:self.giveLabel];
    
    self.seperator = [[UIImageView alloc]initWithFrame:CGRectMake(11, 48, 298, 1)];
    self.seperator.image = [UIImage imageNamed:@"text_divider"];
    [self.dealBackground addSubview:self.seperator];
    
    self.getLabel = [[UILabel alloc]initWithFrame:CGRectMake(0, 61, 320, 26)];
    self.getLabel.font = [UIFont fontWithName:@"HelveticaNeue-Bold" size:24];
    self.getLabel.text = @"Get $5 Credit";
    self.getLabel.textColor = UIColorFromRGB(0x3a3a3a);
    self.getLabel.textAlignment = NSTextAlignmentCenter;
    self.getLabel.backgroundColor = [UIColor clearColor];
    [self.dealBackground addSubview:self.getLabel];
    
    self.termsBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    self.termsBtn.frame = CGRectMake(10, 92, 150, 14);
    [self.termsBtn setTitle:NSLocalizedString(@"Terms and Conditions", nil) forState:UIControlStateNormal];
    self.termsBtn.titleLabel.font = [UIFont fontWithName:@"HelveticaNeue-Light" size:12];
    [self.termsBtn setTitleColor:UIColorFromRGB(0x686868) forState:UIControlStateNormal];
    self.termsBtn.titleLabel.textAlignment = NSTextAlignmentLeft;
    self.termsBtn.contentHorizontalAlignment = UIControlContentHorizontalAlignmentLeft;
    [self.dealBackground addSubview:self.termsBtn];
    
    self.learnBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    self.learnBtn.frame = CGRectMake(160, 92, 150, 14);
    [self.learnBtn setTitle:NSLocalizedString(@"Learn More", nil) forState:UIControlStateNormal];
    self.learnBtn.titleLabel.font = [UIFont fontWithName:@"HelveticaNeue-Light" size:12];
    [self.learnBtn setTitleColor:UIColorFromRGB(0x686868) forState:UIControlStateNormal];
    self.learnBtn.titleLabel.textAlignment = NSTextAlignmentRight;
    self.learnBtn.contentHorizontalAlignment = UIControlContentHorizontalAlignmentRight;
    [self.dealBackground addSubview:self.learnBtn];

    
    self.giveBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    [self.giveBtn setImage:[UIImage imageNamed:@"give_bkg_button"] forState:UIControlStateNormal];
    self.giveBtn.frame = CGRectMake(10, 453, 300, 44);
    [self.giveBtn addTarget:self action:@selector(onGiveGift:) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:self.giveBtn];
}


#pragma mark - btn actions 
-(IBAction)onGiveGift:(id)sender{
    
    if(!photoTaken){
        UIAlertView* alert = [[UIAlertView alloc]initWithTitle:@"Error" message:@"Please Add Photo" delegate:nil cancelButtonTitle:@"OK" otherButtonTitles: nil];
        [alert show];
        return;
    }
    
    if(!captionAdded){
        UIAlertView* alert = [[UIAlertView alloc]initWithTitle:@"Warning"
                                                       message:@"Would you like to add a caption"
                                                      delegate:self
                                             cancelButtonTitle:@"No"
                                             otherButtonTitles: @"Yes", nil];
        [alert show];
        return;
    }
    
//    CGRect frame = ((AppDelegate*)[UIApplication sharedApplication].delegate).window.frame;
//    ShareSuccessView* shareView = [[ShareSuccessView alloc]initWithFrame:frame];
//    [shareView manuallyLayoutSubviews];
//    [((AppDelegate*)[UIApplication sharedApplication].delegate).window addSubview:shareView];

    [self postToFacebook];
}

-(IBAction)takePhoto:(id)sender{
    UIActionSheet* sheet = [[UIActionSheet alloc] initWithTitle:@"Share It" delegate:self cancelButtonTitle:@"Cancel" destructiveButtonTitle:nil otherButtonTitles:@"Take Photo", nil];
    [sheet showInView:self.view];
}

-(IBAction)onLearnMore:(id)sender{
    
}


#pragma mark - AlertView Delegate Methods
- (void)alertView:(UIAlertView *)alertView didDismissWithButtonIndex:(NSInteger)buttonIndex{
 
    if (buttonIndex == 0) {
        [self postToFacebook];
    }
}


#pragma mark - image picker delegates
- (void)imagePickerController:(UIImagePickerController *)picker didFinishPickingMediaWithInfo:(NSDictionary *)info{
    [self.imageOverlay removeFromSuperview];
    CGRect cropRect = CGRectMake(10, 50, 500, 500);
    if ([UIDevice hasFourInchDisplay]) {
        cropRect.origin.y = 94;
    }
    UIImage* image = [info valueForKey:UIImagePickerControllerOriginalImage];
    image = [image imageByScalingAndCroppingForSize:CGSizeMake(320, 480)];
    self.giveImage.image =  [image imageCropToRect:cropRect];
    [self dismissViewControllerAnimated:YES completion:nil];
    photoTaken = YES;
}

- (void)imagePickerControllerDidCancel:(UIImagePickerController *)picker{
    
    [self dismissViewControllerAnimated:YES completion:nil];
}


- (void)actionSheet:(UIActionSheet *)actionSheet clickedButtonAtIndex:(NSInteger)buttonIndex{
    if(buttonIndex == 0){
        if([UIImagePickerController isSourceTypeAvailable:UIImagePickerControllerSourceTypeCamera]){
            UIImagePickerController* picker = [[UIImagePickerController alloc]init];
            picker.view.frame = self.view.frame;
            picker.sourceType = UIImagePickerControllerSourceTypeCamera;
            picker.delegate = self;
            
            UIView* overlay = [[UIView alloc]initWithFrame:picker.view.frame];
            UIImageView* square = [[UIImageView alloc]initWithFrame:overlay.frame];
            if([UIDevice hasFourInchDisplay]){
             
                square.image = [UIImage imageNamed:@"camera_area-h568"];
            }
            else{
                square.image = [UIImage imageNamed:@"camera_area"];
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
}


#pragma mark - facebook
-(void)postToFacebook{
    
    self.giveBtn.enabled = NO;
    FBRequestConnection* connection = [[FBRequestConnection alloc]initWithTimeout:30];
    
    
    FBRequest* request = [FBRequest requestForUploadPhoto:self.giveImage.image];
    if( [self.captionTextView.text compare:NSLocalizedString(@"add comment", nil)] == NSOrderedSame ){
        [request.parameters setObject:[NSString stringWithFormat:@"%@.\n\n To get gift install kikbak, http://en.wikipedia.org/wiki/Cyan", self.captionTextView.text] forKey:@"name"];
    }
    else{
        [request.parameters setObject:@"To get gift install kikbak, http://en.wikipedia.org/wiki/Cyan" forKey:@"name"];
    }
    
    [connection addRequest:request completionHandler:^(FBRequestConnection *connection, id result, NSError *error) {
        if(error == nil){
            [Flurry logEvent:@"ShareEvent" timed:YES];

            CGRect frame = ((AppDelegate*)[UIApplication sharedApplication].delegate).window.frame;
            ShareSuccessView* shareView = [[ShareSuccessView alloc]initWithFrame:frame];
            [shareView manuallyLayoutSubviews];
            [((AppDelegate*)[UIApplication sharedApplication].delegate).window addSubview:shareView];
            ShareExperienceRequest* request = [[ShareExperienceRequest alloc]init];
            NSMutableDictionary* dict = [[NSMutableDictionary alloc]initWithCapacity:3];

            [dict setObject:self.offer.merchantId forKey:@"merchantId"];
            [dict setObject:self.location.locationId forKey:@"locationId"];
            [dict setObject:self.offer.offerId forKey:@"offerId"];
            [dict setObject:[result objectForKey:@"id"] forKey:@"fbImageId"];
            [request restRequest:dict];
        }
        else{
            [Flurry logEvent:@"FailedShareEvent" timed:YES];
            NSLog(@"Submit Error: %@", error);
        }
        self.giveBtn.enabled = YES;
    }];
    
    
    [connection start];
    
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
        containerFrame.origin.y = (containerFrame.size.height > DEFAULT_CONTAINER_VIEW_HEIGHT)?284 - (containerFrame.size.height - DEFAULT_CONTAINER_VIEW_HEIGHT): 284;
    }
    else{
        containerFrame.origin.y = (containerFrame.size.height > DEFAULT_CONTAINER_VIEW_HEIGHT)? 203 - (containerFrame.size.height - DEFAULT_CONTAINER_VIEW_HEIGHT): 203;
    }
	
	// animations settings
	[UIView beginAnimations:nil context:NULL];
	[UIView setAnimationBeginsFromCurrentState:YES];
    [UIView setAnimationDuration:[duration doubleValue]];
    [UIView setAnimationCurve:(UIViewAnimationCurve)[curve intValue]];
    
	// set views with new info
	self.captionContainerView.frame = containerFrame;
    [self.view bringSubviewToFront:self.captionContainerView];
	
	// commit animations
	[UIView commitAnimations];
    
}

#pragma mark - HPGrowingTextView delegate methods
-(void)resignTextView
{
	[self.captionTextView resignFirstResponder];
}


- (BOOL)growingTextViewShouldReturn:(HPGrowingTextView *)growingTextView{
    [self.captionTextView resignFirstResponder];
    return YES;
}


- (void)growingTextView:(HPGrowingTextView *)growingTextView willChangeHeight:(float)height
{
    float diff = (growingTextView.frame.size.height - height);
    
	CGRect r = self.captionContainerView.frame;
    r.size.height -= diff;
    r.origin.y += diff;
	self.captionContainerView.frame = r;
}

- (BOOL)growingTextViewShouldBeginEditing:(HPGrowingTextView *)growingTextView{
    if( [growingTextView.text compare:NSLocalizedString(@"add comment", nil)] == NSOrderedSame ){
        growingTextView.text = @"";
    }
    return YES;
}

@end
