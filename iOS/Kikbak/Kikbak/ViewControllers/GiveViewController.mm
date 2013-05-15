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

@interface GiveViewController ()

@property (nonatomic, strong) Location* location;

-(void)manuallyLayoutSubviews;

-(IBAction)takePhoto:(id)sender;
-(IBAction)retakePhoto:(id)sender;
-(IBAction)onGiveGift:(id)sender;
-(IBAction)onLearnMore:(id)sender;
-(void)postToFacebook;
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
    
    [self manuallyLayoutSubviews];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

-(void)viewWillAppear:(BOOL)animated{
    [super viewWillAppear:animated];
//    self.retailer.text = self.offer.merchantName;
//    if (self.offer.location.count > 0) {
//        self.location = [self.offer.location anyObject];
//    }
//    
//    self.distance.text = [Distance distanceToInMiles:[[CLLocation alloc]initWithLatitude:self.location.latitude.doubleValue longitude:self.location.longitude.doubleValue]];
//    
//    self.giveText.text = self.offer.giftDescription;
//    self.getText.text = self.offer.kikbakDescription;
//    
//    NSString* imagePath = [ImagePersistor imageFileExists:self.offer.merchantId imageType:MERCHANT_IMAGE_TYPE];
//    if(imagePath != nil){
//        self.retailerImage.image = [[UIImage alloc]initWithContentsOfFile:imagePath];
//    }
    
    
}

-(void)viewDidAppear:(BOOL)animated{
    [super viewDidAppear:animated];
}

-(void)viewDidLayoutSubviews{
    [self manuallyLayoutSubviews];
}

-(void)manuallyLayoutSubviews{
    
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
    
    self.distance = [[UILabel alloc] initWithFrame:CGRectMake(85, 40, 90, 13)];
    self.distance.font = [UIFont fontWithName:@"HelveticaNeue-Light" size:12];
    self.distance.textColor = UIColorFromRGB(0xf5f5f5);
    self.distance.text = @"1.7 Miles Away";
    self.distance.backgroundColor = [UIColor clearColor];
    [self.merchantBackground addSubview:self.distance];
    
    self.callIcon = [[UIImageView alloc]initWithFrame:CGRectMake(175, 40, 13, 12)];
    self.callIcon.image = [UIImage imageNamed:@"phone_icon"];
    [self.merchantBackground addSubview:self.callIcon];
    
    self.call = [[UILabel alloc]initWithFrame:CGRectMake(194, 40, 20, 13)];
    self.call.font = [UIFont fontWithName:@"HelveticaNeue-Light" size:12];
    self.call.textColor = UIColorFromRGB(0xf5f5f5);
    self.call.text = NSLocalizedString(@"call", nil);
    self.call.backgroundColor = [UIColor clearColor];
    [self.merchantBackground addSubview:self.call];
    
    self.webIcon = [[UIImageView alloc]initWithFrame:CGRectMake(246, 40, 12, 12)];
    self.webIcon.image = [UIImage imageNamed:@"web_icon"];
    [self.merchantBackground addSubview:self.webIcon];
    
    self.web = [[UILabel alloc]initWithFrame:CGRectMake(265, 40, 30, 13)];
    self.web.font = [UIFont fontWithName:@"HelveticaNeue-Light" size:12];
    self.web.textColor = UIColorFromRGB(0xf5f5f5);
    self.web.text = NSLocalizedString(@"web", nil);
    self.web.backgroundColor = [UIColor clearColor];
    [self.merchantBackground addSubview:self.web];
    
    self.captionBackground = [[UIView alloc]initWithFrame:CGRectMake(0, 284, 320, 48)];
    self.captionBackground.backgroundColor = [UIColor colorWithRed:0.0 green:0.0 blue:0.0 alpha:0.7];
    [self.view addSubview:self.captionBackground];
    
    self.captionTextView = [[UITextView alloc]initWithFrame:CGRectMake(10, 8, 300, 32)];
    self.captionTextView.text = NSLocalizedString(@"add comment", nil);
    self.captionTextView.layer.cornerRadius = 3.0;
    [self.captionBackground addSubview:self.captionTextView];
    
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

-(IBAction)retakePhoto:(id)sender{
    
    [self takePhoto:nil];
    
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
    
//    CGRect cropRect = CGRectMake(10, 50, 500, 500);
//    if ([UIDevice hasFourInchDisplay]) {
//        cropRect.origin.y = 94;
//    }
//    UIImage* image = [info valueForKey:UIImagePickerControllerOriginalImage];
//    image = [image imageByScalingAndCroppingForSize:CGSizeMake(320, 480)];
//    self.giftImage.image =  [image imageCropToRect:cropRect];
//    [self dismissViewControllerAnimated:YES completion:nil];
//    photoTaken = YES;
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
    
//    self.shareBtn.enabled = NO;
//    FBRequestConnection* connection = [[FBRequestConnection alloc]initWithTimeout:30];
//    
//    
//    FBRequest* request = [FBRequest requestForUploadPhoto:self.giftImage.image];
//    [request.parameters setObject:@"This is awesome. You gotta get it.\n\n To get gift install kikbak, http://en.wikipedia.org/wiki/Cyan" forKey:@"name"];
//    
//    
//    [connection addRequest:request completionHandler:^(FBRequestConnection *connection, id result, NSError *error) {
//        if(error == nil){
//            [Flurry logEvent:@"ShareEvent" timed:YES];
//
//            CGRect frame = ((AppDelegate*)[UIApplication sharedApplication].delegate).window.frame;
//            ShareSuccessView* shareView = [[ShareSuccessView alloc]initWithFrame:frame];
//            [shareView manuallyLayoutSubviews];
//            [((AppDelegate*)[UIApplication sharedApplication].delegate).window addSubview:shareView];
//            ShareExperienceRequest* request = [[ShareExperienceRequest alloc]init];
//            NSMutableDictionary* dict = [[NSMutableDictionary alloc]initWithCapacity:3];
//
//            [dict setObject:self.offer.merchantId forKey:@"merchantId"];
//            [dict setObject:self.location.locationId forKey:@"locationId"];
//            [dict setObject:self.offer.offerId forKey:@"offerId"];
//            [dict setObject:[result objectForKey:@"id"] forKey:@"fbImageId"];
//            [request restRequest:dict];
//        }
//        else{
//            [Flurry logEvent:@"FailedShareEvent" timed:YES];
//            NSLog(@"Submit Error: %@", error);
//        }
//        self.shareBtn.enabled = YES;
//    }];
//    
//    
//    [connection start];
    
}

@end
