//
//  SuggestViewController.m
//  Kikbak
//
//  Created by Ian Barile on 6/12/13.
//  Copyright (c) 2013 Ian Barile. All rights reserved.
//

#import "SuggestViewController.h"
#import "UIButton+Util.h"
#import "util.h"
#import <QuartzCore/QuartzCore.h>
#import "Flurry.h"
#import "UIDevice+Screen.h"
#import "UIDevice+OSVersion.h"
#import "UIImage+Manipulate.h"
#import "NotificationContstants.h"
#import "ImageUploadRequest.h"
#import "SuggestBusinessRequest.h"
#import "SpinnerView.h"
#import "AppDelegate.h"

static int offsetForIOS6 = 44;

@interface SuggestViewController (){
    bool photoTaken;
    float keyboardHeight;
    float keyboardOrigin;
}

@property(nonatomic, strong)SpinnerView* spinnerView;

@property (nonatomic, strong) UIScrollView* scrollView;
@property (nonatomic, strong) UIImageView* photoImage;
@property (nonatomic, strong) UILabel* takePhoto;
@property (nonatomic, strong) UIButton* photoBtn;

@property (nonatomic, strong) UIImageView* gradient;
@property (nonatomic, strong) UILabel* recommend;

@property (nonatomic, strong) UIImageView* businessShadow;
@property (nonatomic, strong) UITextView* business;
@property (nonatomic, strong) UIImageView* aboutShadow;
@property (nonatomic, strong) UITextView* about;

@property (nonatomic, strong) UIButton* submit;


-(IBAction)keyboardWillShow:(NSNotification*)notification;
-(IBAction)keyboardWillHide:(NSNotification*)notification;


-(void)createSubviews;
-(void)manuallyLayoutSubviews;
-(void)resignTextView;


-(IBAction)onBackBtn:(id)sender;
-(IBAction)onTakePhotoBtn:(id)sender;
-(IBAction)onSubmit:(id)sender;

-(void) onImageUploadSuccess:(NSNotification*)notification;
-(void) onImageUploadError:(NSNotification*)notification;
-(void) onSuggestSuccess:(NSNotification*)notification;
-(void) onSuggestError:(NSNotification*)notification;

@end

@implementation SuggestViewController

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
    self.navigationItem.hidesBackButton = YES;
    self.navigationItem.leftBarButtonItem = [UIButton blackBackBtn:self];
    
    self.title = NSLocalizedString(@"Suggest", nil);
    
    if( [UIDevice osVersion7orGreater] ){
        self.edgesForExtendedLayout = UIRectEdgeNone;
        offsetForIOS6 = 0;
    }
    
    photoTaken = NO;
    [self createSubviews];
    [self manuallyLayoutSubviews];
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
    [[NSNotificationCenter defaultCenter]addObserver:self selector:@selector(onImageUploadSuccess:) name:kKikbakImagePostSuccess object:nil];
    [[NSNotificationCenter defaultCenter]addObserver:self selector:@selector(onImageUploadError:) name:kKikbakImagePostError object:nil];
    [[NSNotificationCenter defaultCenter]addObserver:self selector:@selector(onSuggestSuccess:) name:kKikbakSuggestSuccess object:nil];
    [[NSNotificationCenter defaultCenter]addObserver:self selector:@selector(onSuggestError:) name:kKikbakSuggestError object:nil];
    
}

-(void)viewDidAppear:(BOOL)animated{
    [super viewDidAppear:animated];
    [Flurry logEvent:@"SuggestBusinessEvent" timed:YES];
}

-(void)viewWillDisappear:(BOOL)animated{
    [super viewWillDisappear:animated];
    [[NSNotificationCenter defaultCenter]removeObserver:self name:UIKeyboardWillHideNotification object:nil];
    [[NSNotificationCenter defaultCenter]removeObserver:self name:UIKeyboardWillShowNotification object:nil];
    [[NSNotificationCenter defaultCenter]removeObserver:self name:kKikbakImagePostSuccess object:nil];
    [[NSNotificationCenter defaultCenter]removeObserver:self name:kKikbakImagePostError object:nil];
    [[NSNotificationCenter defaultCenter]removeObserver:self name:kKikbakSuggestSuccess object:nil];
    [[NSNotificationCenter defaultCenter]removeObserver:self name:kKikbakSuggestError object:nil];


}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

-(void)createSubviews{
    self.scrollView = [[UIScrollView alloc]initWithFrame:CGRectMake(0, 0 + offsetForIOS6, 320, self.view.frame.size.height)];
    self.scrollView.backgroundColor = [UIColor colorWithPatternImage:[UIImage imageNamed:@"bg_offwhite_eggshell"]];
    [self.view addSubview:self.scrollView];
    
    self.photoImage = [[UIImageView alloc]initWithFrame:CGRectMake(0, 0, 320, 249)];
    self.photoImage.backgroundColor = [UIColor colorWithPatternImage:[UIImage imageNamed:@"bg_black"]];
    [self.scrollView addSubview:self.photoImage];
    
    self.gradient = [[UIImageView alloc]initWithFrame:CGRectMake(0, 249, 320, 4)];
    self.gradient.image = [UIImage imageNamed:@"grd_suggest"];
    [self.scrollView addSubview:self.gradient];
    
    self.takePhoto = [[UILabel alloc]initWithFrame:CGRectMake(0, 62, 320, 20)];
    self.takePhoto.backgroundColor = [UIColor clearColor];
    self.takePhoto.textAlignment = NSTextAlignmentCenter;
    self.takePhoto.textColor = [UIColor whiteColor];
    self.takePhoto.text = NSLocalizedString(@"Take a photo", nil);
    self.takePhoto.font = [UIFont fontWithName:@"HelveticaNeue-Bold" size:16];
    [self.scrollView addSubview:self.takePhoto];
    
    UIImage* camera = [UIImage imageNamed:@"ic_camera"];
    self.photoBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    self.photoBtn.frame = CGRectMake(112, 88, 95, 95);
    [self.photoBtn setImage:camera forState:UIControlStateNormal];
    [self.photoBtn addTarget:self action:@selector(onTakePhotoBtn:) forControlEvents:UIControlEventTouchUpInside];
    [self.scrollView addSubview:self.photoBtn];
    
    self.recommend = [[UILabel alloc]initWithFrame:CGRectMake(11, 269, 298, 55)];
    self.recommend.text = NSLocalizedString(@"recommend business", nil);
    self.recommend.font = [UIFont fontWithName:@"HelveticaNeue-Bold" size:15];
    self.recommend.numberOfLines = 3;
    self.recommend.textColor = UIColorFromRGB(0x3a3a3a);
    self.recommend.backgroundColor = [UIColor clearColor];
    [self.scrollView addSubview:self.recommend];
    
    self.business = [[UITextView alloc]initWithFrame:CGRectMake(11, 334, 298, 35)];
    self.business.delegate = self;
    self.business.text = NSLocalizedString(@"Business name", nil);
    self.business.textColor = UIColorFromRGB(0x7F7F7F);
    self.business.scrollEnabled = NO;
    self.business.returnKeyType = UIReturnKeyDone; 
    self.business.contentInset = UIEdgeInsetsMake(2, 5, 2, 5);
    self.business.font = [UIFont fontWithName:@"HelveticaNeue" size:13];
    self.business.backgroundColor = [UIColor clearColor];
    self.business.layer.cornerRadius = 5;
    self.business.layer.masksToBounds = YES;
    self.business.layer.borderWidth = 1;
    self.business.layer.borderColor = [UIColorFromRGB(0xb9b9b9) CGColor];
    self.businessShadow = [[UIImageView alloc]initWithImage:[UIImage imageNamed:@"bg_textview_suggest_business"]];
    self.businessShadow.frame = CGRectMake(11, 334, 298, 35);
    [self.scrollView addSubview:self.businessShadow];
    [self.scrollView addSubview:self.business];
    
    self.about = [[UITextView alloc]initWithFrame:CGRectMake(11, 379, 298, 55)];
    self.about.delegate = self;
    self.about.text = NSLocalizedString(@"What makes them great", nil);
    self.about.textColor = UIColorFromRGB(0x7F7F7F);
    self.about.returnKeyType = UIReturnKeyDone; 
    self.about.scrollEnabled = NO;
    self.about.contentInset = UIEdgeInsetsMake(0, 5, 0, 5);
    self.about.font = [UIFont fontWithName:@"HelveticaNeue" size:13];
    self.about.backgroundColor = [UIColor clearColor];
    self.about.layer.cornerRadius = 5;
    self.about.layer.masksToBounds = YES;
    self.about.layer.borderWidth = 1;
    self.about.layer.borderColor = [UIColorFromRGB(0xb9b9b9) CGColor];
    self.aboutShadow = [[UIImageView alloc]initWithImage:[UIImage imageNamed:@"bg_textview_suggest_great"]];
    self.aboutShadow.frame = CGRectMake(11, 379, 298, 55);
    [self.scrollView addSubview:self.aboutShadow];
    [self.scrollView addSubview:self.about];

    self.submit = [UIButton buttonWithType:UIButtonTypeCustom];
    self.submit.frame = CGRectMake(11, 445, 298, 40);
    [self.submit setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
    [self.submit setBackgroundImage:[UIImage imageNamed:@"btn_blue"] forState:UIControlStateNormal];
    [self.submit setTitle:NSLocalizedString(@"Let us know", nil) forState:UIControlStateNormal];
    [self.submit addTarget:self action:@selector(onSubmit:) forControlEvents:UIControlEventTouchUpInside];
    [self.scrollView addSubview:self.submit];
}

-(void)manuallyLayoutSubviews{
    
    if(![UIDevice hasFourInchDisplay]){
        self.photoImage.frame = CGRectMake(0, 0, 320, 178);
        self.gradient.frame = CGRectMake(0, 178, 320, 4);
        self.takePhoto.frame = CGRectMake(0, 32, 320, 20);
        self.photoBtn.frame = CGRectMake(112, 56, 95, 95);
        self.recommend.frame = CGRectMake(11, 190, 298, 55);
        self.business.frame = CGRectMake(11, 255, 298, 35);
        self.businessShadow.frame = CGRectMake(11, 255, 298, 35);
        self.about.frame = CGRectMake(11, 300, 298, 55);
        self.aboutShadow.frame = CGRectMake(11, 300, 298, 55);
        self.submit.frame = CGRectMake(11, 365, 298, 40);
    }
}

#pragma mark - text view
-(void)resignTextView
{
	[self.about resignFirstResponder];
    [self.business resignFirstResponder];
}

- (BOOL)textView:(UITextView *)textView shouldChangeTextInRange:(NSRange)range
                                                replacementText:(NSString *)atext {
	
	//weird 1 pixel bug when clicking backspace when textView is empty
	if(![textView hasText] && [atext isEqualToString:@""]) return NO;
	
	
	if ([atext isEqualToString:@"\n"]) {
        [textView resignFirstResponder];
        return NO;
	}
	
	return YES;
}

- (void)textViewDidBeginEditing:(UITextView *)textView{
    
    CGFloat bottom = textView.frame.origin.y + textView.frame.size.height;
    if(self.view.frame.size.height - keyboardHeight < bottom){
        CGFloat offset = self.view.frame.size.height - keyboardHeight - bottom - 10;
        self.scrollView.contentOffset = CGPointMake(0, -offset);
    }
    
    if([textView.text compare:NSLocalizedString(@"Business name", nil)] == NSOrderedSame
       || [textView.text compare:NSLocalizedString(@"What makes them great", nil)] == NSOrderedSame ){
        textView.text = @"";
        textView.textColor = UIColorFromRGB(0x3a3a3a);
    }

}

- (void)textViewDidEndEditing:(UITextView *)textView{
    self.scrollView.contentOffset = CGPointMake(0, 0);

    if(textView == self.business && [textView.text compare:@""] == NSOrderedSame){
        self.business.text = NSLocalizedString(@"Business name", nil);
        self.business.textColor = UIColorFromRGB(0x7F7F7F);
    }
    
    if(textView == self.about && [textView.text compare:@""] == NSOrderedSame){
        self.about.text = NSLocalizedString(@"What makes them great", nil);
        self.about.textColor = UIColorFromRGB(0x7F7F7F);
    }
}


#pragma mark - buttons
-(IBAction)onBackBtn:(id)sender{
    [self.navigationController popViewControllerAnimated:YES];
}

-(IBAction)onTakePhotoBtn:(id)sender{
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
        
        [Flurry logEvent:@"BusinessPhotoEvent" timed:YES];
        
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

-(IBAction)onSubmit:(id)sender{
    if(photoTaken == NO){
        UIAlertView* alert = [[UIAlertView alloc]initWithTitle:nil message:NSLocalizedString(@"Suggest Photo", nil)
                                             delegate:self
                                             cancelButtonTitle:nil
                                             otherButtonTitles:NSLocalizedString(@"Take a photo", nil), nil];
        [alert show];
        return;
    }
    
    if([self.business.text compare:NSLocalizedString(@"Business name", nil)] == NSOrderedSame
       || [self.about.text compare:NSLocalizedString(@"What makes them great", nil)] == NSOrderedSame ){
        UIAlertView* alert = [[UIAlertView alloc]initWithTitle:nil message:NSLocalizedString(@"suggest required", nil)
                                                      delegate:self
                                             cancelButtonTitle:nil
                                             otherButtonTitles:NSLocalizedString(@"Ok", nil), nil];
        [alert show];
        return;
    }
    
    CGRect frame = ((AppDelegate*)[UIApplication sharedApplication].delegate).window.frame;
    self.spinnerView = [[SpinnerView alloc]initWithFrame:frame];
    [self.spinnerView startActivity];
    [((AppDelegate*)[UIApplication sharedApplication].delegate).window addSubview:self.spinnerView];

    
    self.submit.enabled = NO;
    ImageUploadRequest* request = [[ImageUploadRequest alloc]init];
    request.image = self.photoImage.image;
    [request postImage];
    
}

#pragma mark - image picker delegates
- (void)imagePickerController:(UIImagePickerController *)picker didFinishPickingMediaWithInfo:(NSDictionary *)info{
    CGRect cropRect = CGRectMake(10, 50, 500, 500);
    
    [self.takePhoto removeFromSuperview];
    self.photoBtn.frame = CGRectMake(265, 11, 55, 55);
    [self.photoBtn setImage:[UIImage imageNamed:@"ic_post_give_camera"] forState:UIControlStateNormal];

    UIImage* image = [info valueForKey:UIImagePickerControllerOriginalImage];
    image = [image imageByScalingAndCroppingForSize:CGSizeMake(640, 960)];
    self.photoImage.image =  [image imageCropToRect:cropRect];
    [self dismissViewControllerAnimated:YES completion:nil];
    photoTaken = YES;
    
}

- (void)imagePickerControllerDidCancel:(UIImagePickerController *)picker{
    
    [self dismissViewControllerAnimated:YES completion:nil];
}

#pragma mark - AlertView Delegate Methods
- (void)alertView:(UIAlertView *)alertView didDismissWithButtonIndex:(NSInteger)buttonIndex{
    
    if( buttonIndex == 0) {
        [self onTakePhotoBtn:nil];
    }
}

#pragma mark - keyboard methods
-(IBAction)keyboardWillShow:(NSNotification*)notification{

    CGRect keyboardBounds;
    [[notification.userInfo valueForKey:UIKeyboardFrameEndUserInfoKey] getValue: &keyboardBounds];
    
    // Need to translate the bounds to account for rotation.
    keyboardBounds = [self.view convertRect:keyboardBounds toView:nil];
    keyboardHeight = keyboardBounds.size.height;
    keyboardOrigin = keyboardBounds.origin.y;
}

-(IBAction)keyboardWillHide:(NSNotification*)notification{
    
}


#pragma mark - notifications
-(void) onImageUploadSuccess:(NSNotification*)notification{
    SuggestBusinessRequest* request = [[SuggestBusinessRequest alloc]init];
    NSMutableDictionary* dict = [[NSMutableDictionary alloc]initWithCapacity:3];
    [dict setObject:[notification object] forKey:@"image_url"];
    [dict setObject:self.about.text forKey:@"why"];
    [dict setObject:self.business.text forKey:@"business_name"];
    [request restRequest:dict];
}

-(void) onImageUploadError:(NSNotification*)notification{
    [self.spinnerView removeFromSuperview];
    self.submit.enabled = YES;
    UIAlertView* alert = [[UIAlertView alloc]initWithTitle:nil message:NSLocalizedString(@"Unable to suggest", nil)
                                                  delegate:self
                                         cancelButtonTitle:nil
                                         otherButtonTitles:NSLocalizedString(@"Ok", nil), nil];
    [alert show];
}

-(void) onSuggestSuccess:(NSNotification*)notification{
    [self.spinnerView removeFromSuperview];
    UIAlertView* alert = [[UIAlertView alloc]initWithTitle:nil message:NSLocalizedString(@"Thank you", nil)
                                                  delegate:self
                                         cancelButtonTitle:nil
                                         otherButtonTitles:NSLocalizedString(@"Ok", nil), nil];

    [alert show];
}

-(void) onSuggestError:(NSNotification*)notification{
    [self.spinnerView removeFromSuperview];
    self.submit.enabled = YES;
    UIAlertView* alert = [[UIAlertView alloc]initWithTitle:nil message:NSLocalizedString(@"Unable to suggest", nil)
                                                  delegate:self
                                         cancelButtonTitle:nil
                                         otherButtonTitles:NSLocalizedString(@"Ok", nil), nil];
    [alert show];
}


@end
