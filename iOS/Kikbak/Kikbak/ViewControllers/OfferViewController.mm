//
//  ShareViewController.m
//  kikit
//
//  Created by Ian Barile on 3/15/13.
//  Copyright (c) 2013 Ian Barile. All rights reserved.
//

#import "OfferViewController.h"
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

@interface OfferViewController ()

@property (nonatomic, strong) Location* location;
@property (nonatomic, strong) UIButton* takePhotoBtn;
@property (nonatomic, strong) UIButton* addCaptionBtn;
@property (nonatomic, strong) UIButton* retakeBtn;

-(void)manuallyLayoutSubviews;

-(IBAction)takePhoto:(id)sender;
-(IBAction)retakePhoto:(id)sender;
-(IBAction)addCaption:(id)sender;

-(void)postToFacebook;
@end

@implementation OfferViewController

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
    
    self.takePhotoBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    [self.takePhotoBtn setImage:[UIImage imageNamed:@"add_photo"] forState:UIControlStateNormal];
    [self.takePhotoBtn addTarget:self action:@selector(takePhoto:) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:self.takePhotoBtn];
    
    self.addCaptionBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    self.addCaptionBtn.backgroundColor = [UIColor colorWithRed:0.81 green:0.81 blue:0.81 alpha:1.0];
    self.addCaptionBtn.titleLabel.font = [UIFont systemFontOfSize:12];
    [self.addCaptionBtn setTitle:NSLocalizedString(@"Caption", nil) forState:UIControlStateNormal];
    [self.addCaptionBtn addTarget:self action:@selector(addCaption:) forControlEvents:UIControlEventTouchUpInside];
    self.addCaptionBtn.backgroundColor = [UIColor grayColor];
    [self.view addSubview:self.addCaptionBtn];
    
    
    self.retakeBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    self.retakeBtn.backgroundColor = [UIColor colorWithRed:0.81 green:0.81 blue:0.81 alpha:1.0];
    self.retakeBtn.titleLabel.font = [UIFont systemFontOfSize:12];
    self.retakeBtn.backgroundColor = [UIColor grayColor];
    [self.retakeBtn setTitle:NSLocalizedString(@"Retake", nil) forState:UIControlStateNormal];
    [self.retakeBtn addTarget:self action:@selector(retakePhoto:) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:self.retakeBtn];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

-(void)viewWillAppear:(BOOL)animated{
    [super viewWillAppear:animated];
    self.retailer.text = self.offer.merchantName;
    if (self.offer.location.count > 0) {
        self.location = [self.offer.location anyObject];
    }
    
    self.distance.text = [Distance distanceToInMiles:[[CLLocation alloc]initWithLatitude:self.location.latitude.doubleValue longitude:self.location.longitude.doubleValue]];
    
    self.giveText.text = self.offer.giftDescription;
    self.getText.text = self.offer.kikbakDescription;
    
    NSString* imagePath = [ImagePersistor merchantImageFileExists:self.offer.merchantId];
    if(imagePath != nil){
        self.retailerImage.image = [[UIImage alloc]initWithContentsOfFile:imagePath];
    }
}

-(void)viewDidAppear:(BOOL)animated{
    [super viewDidAppear:animated];
    
    
    [self manuallyLayoutSubviews];
}

-(void)viewDidLayoutSubviews{
    [self manuallyLayoutSubviews];
}

-(void)manuallyLayoutSubviews{
    
    if ([UIDevice hasFourInchDisplay]) {
        //location
        CGRect fr = _locationView.frame;
        fr.size.height = 76;
        _locationView.frame = fr;
        
        fr = _retailerImage.frame;
        fr.origin.y = 12;
        _retailerImage.frame = fr;
        
        CGRect retailerFr = _retailer.frame;
        retailerFr.origin.y = 14;
        _retailer.frame = retailerFr;
        
        fr = _mapMarker.frame;
        fr.origin.y = retailerFr.origin.y + retailerFr.size.height + 6;
        _mapMarker.frame = fr;
        
        fr = _distance.frame;
        fr.origin.y = retailerFr.origin.y + retailerFr.size.height + 6;
        _distance.frame = fr;
        
        fr = _topGradient.frame;
        fr.origin.y = 76;
        _topGradient.frame = fr;
        
        CGRect glFr =  _giftLabel.frame;
        glFr.origin.y += 10;
        _giftLabel.frame = glFr;
        
        
        CGRect gaFr = _giveArrow.frame;
        gaFr.origin.y = glFr.origin.y + glFr.size.height;
        _giveArrow.frame = gaFr;
        
        
        CGRect gtFr = _giveText.frame;
        gtFr.origin.y = gaFr.origin.y + 7;
        _giveText.frame = gtFr;
        
        CGRect getLabelFr = _getLabel.frame;
        getLabelFr.origin.y += 12;
        _getLabel.frame = getLabelFr;
        
        
        CGRect getArrowFr = _getArrow.frame;
        getArrowFr.origin.y = getLabelFr.origin.y + getLabelFr.size.height;
        _getArrow.frame = getArrowFr;
        
        CGRect getTextFr = _getText.frame;
        getTextFr.origin.y = getArrowFr.origin.y + 7;
        _getText.frame = getTextFr;
  
        //image frame
        CGRect photoFr = _photoFrame.frame;
        photoFr.origin.y = getArrowFr.origin.y + getArrowFr.size.height + 24;
        photoFr.origin.x = 70;
        photoFr.size.width = 171;
        photoFr.size.height = 171;
        _photoFrame.frame = photoFr;
        
        fr = _giftImage.frame;
        fr.origin.y = photoFr.origin.y + 10;
        fr.origin.x = 77;
        fr.size.width = 150;
        fr.size.height = 149;
        _giftImage.frame = fr;
    }
    
    if( !photoTaken ){
        CGRect fr = _giftImage.frame;
        CGRect takePhotoFr = CGRectMake(fr.origin.x + ((fr.size.width-110)/2) + 8, fr.origin.y + ((fr.size.height - 64)/2), 110, 64);
        self.takePhotoBtn.frame = takePhotoFr;
        self.takePhotoBtn.hidden = NO;
        self.retakeBtn.hidden = YES;
        self.addCaptionBtn.hidden = YES;
    }
    else{
        CGRect fr = _giftImage.frame;
        [self.addCaptionBtn sizeToFit];
        CGRect captionFr = self.addCaptionBtn.frame;
        captionFr.size.height = 16;
        captionFr.origin.x = fr.origin.x + fr.size.width - captionFr.size.width + 4;
        captionFr.origin.y = fr.origin.y + fr.size.height - 17;
        captionFr.size.width += 3;
        self.addCaptionBtn.frame = captionFr;
        self.addCaptionBtn.layer.cornerRadius = 4;
        
     
        [self.retakeBtn sizeToFit];
        CGRect retakeFr = self.retakeBtn.frame;
        retakeFr.size.height = 16;
        retakeFr.size.width += 12;
        retakeFr.origin.x = fr.origin.x + fr.size.width - retakeFr.size.width + 7;
        retakeFr.origin.y = fr.origin.y + 1;
        self.retakeBtn.frame = retakeFr;
        self.retakeBtn.layer.cornerRadius = 4;

        self.takePhotoBtn.hidden = YES;
        self.retakeBtn.hidden = NO;
        self.addCaptionBtn.hidden = NO;
    }
    
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

-(IBAction)onTermsAndConditions:(id)sender{
    
}

-(IBAction)takePhoto:(id)sender{
    UIActionSheet* sheet = [[UIActionSheet alloc] initWithTitle:@"Share It" delegate:self cancelButtonTitle:@"Cancel" destructiveButtonTitle:nil otherButtonTitles:@"Take Photo", nil];
    [sheet showInView:self.view];
}

-(IBAction)retakePhoto:(id)sender{
    
    [self takePhoto:nil];
    
}

-(IBAction)addCaption:(id)sender{
    captionAdded = YES;
    
}

#pragma mark - AlertView Delegate Methods
- (void)alertView:(UIAlertView *)alertView didDismissWithButtonIndex:(NSInteger)buttonIndex{
 
    if (buttonIndex == 0) {
        [self postToFacebook];
    }
}


#pragma mark - image picker delegates
- (void)imagePickerController:(UIImagePickerController *)picker didFinishPickingMediaWithInfo:(NSDictionary *)info{
    
    UIImage* image = [info valueForKey:UIImagePickerControllerOriginalImage];
    
    CGSize size = CGSizeMake(self.giftImage.frame.size.width, self.giftImage.frame.size.height);
    self.giftImage.image = [UIImage resizeImage:image newSize:size];
    
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
    
    self.shareBtn.enabled = NO;
    FBRequestConnection* connection = [[FBRequestConnection alloc]initWithTimeout:30];
    
    
    FBRequest* request = [FBRequest requestForUploadPhoto:self.giftImage.image];
    
    
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
            [request makeRequest:dict];
        }
        else{
            [Flurry logEvent:@"FailedShareEvent" timed:YES];
            NSLog(@"Submit Error: %@", error);
        }
        self.shareBtn.enabled = YES;
    }];
    
    
    [connection start];
    
}

@end
