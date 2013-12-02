//
//  ClaimCreditViewController.m
//  Kikbak
//
//  Created by Ian Barile on 7/15/13.
//  Copyright (c) 2013 Ian Barile. All rights reserved.
//

#import "ClaimCreditViewController.h"
#import "ClaimCreditRequest.h"
#import "KikbakTextField.h"
#import "AppDelegate.h"
#import "util.h"
#import "NotificationContstants.h"
#import "UIDevice+Screen.h"
#import "UIButton+Util.h"
#import "UIDevice+OSVersion.h"
#import "Flurry.h"

static int offsetForIOS6 = 44;

@interface ClaimCreditViewController ()

//@property (nonatomic, strong) UIScrollView* scrollView;
@property (nonatomic, strong) UIImageView* dropShadow;
@property (nonatomic, strong) UILabel* merchant;
@property (nonatomic, strong) UIView* amountBackground;
@property (nonatomic, strong) UILabel* credit;
@property (nonatomic, strong) UILabel* giftCard;
@property (nonatomic, strong) UILabel* userInfo;

@property (nonatomic, strong) KikbakTextField* phoneNumber;
//@property (nonatomic, strong) KikbakTextField* name;
//@property (nonatomic, strong) KikbakTextField* street;
//@property (nonatomic, strong) KikbakTextField* apt;
//@property (nonatomic, strong) KikbakTextField* city;
//@property (nonatomic, strong) KikbakTextField* state;
//@property (nonatomic, strong) KikbakTextField* zip;
@property (nonatomic, strong) UITextField* active;

@property (nonatomic, strong) UILabel* arrival;
@property (nonatomic, strong) UIButton* submitBtn;

-(void)createSubviews;
-(void)manuallyLayoutSubviews;

-(void)onClaimSuccess:(NSNotification*)notification;
-(void)onClaimError:(NSNotification*)notification;
-(IBAction)keyboardWillShow:(NSNotification*)notification;
-(IBAction)keyboardWillHide:(NSNotification*)notification;


-(IBAction)onSubmitBtn:(id)sender;
-(IBAction)onBackBtn:(id)sender;


@end

@implementation ClaimCreditViewController

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
    
    if( [UIDevice osVersion7orGreater] ){
        self.edgesForExtendedLayout = UIRectEdgeNone;
        offsetForIOS6 = 0;
    }
    
    self.navigationItem.hidesBackButton = YES;
    self.navigationItem.leftBarButtonItem = [UIButton blackBackBtn:self];
    
    [self createSubviews];
    [self manuallyLayoutSubviews];
}

-(void)viewWillAppear:(BOOL)animated{
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(keyboardWillShow:)
                                                 name:UIKeyboardWillShowNotification
                                               object:nil];
    
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(keyboardWillHide:)
                                                 name:UIKeyboardWillHideNotification
                                               object:nil];
    [[NSNotificationCenter defaultCenter]addObserver:self selector:@selector(onClaimSuccess:) name:kKikbakClaimSuccess object:nil];
    [[NSNotificationCenter defaultCenter]addObserver:self selector:@selector(onClaimError:) name:kKikbakClaimError object:nil];
}

-(void)viewWillDisappear:(BOOL)animated{
    [[NSNotificationCenter defaultCenter]removeObserver:self name:UIKeyboardWillHideNotification object:nil];
    [[NSNotificationCenter defaultCenter]removeObserver:self name:UIKeyboardWillShowNotification object:nil];
    [[NSNotificationCenter defaultCenter]removeObserver:self name:kKikbakClaimSuccess object:nil];
    [[NSNotificationCenter defaultCenter]removeObserver:self name:kKikbakClaimError object:nil];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}


-(void)createSubviews{
    
//    self.scrollView = [[UIScrollView alloc]initWithFrame:CGRectMake(0,0+offsetForIOS6, self.view.frame.size.width, 500)];
//    [self.scrollView setContentSize: CGSizeMake(320, 515)];
//    self.scrollView.translatesAutoresizingMaskIntoConstraints= NO;
//    self.scrollView.scrollEnabled=YES;
//    self.scrollView.backgroundColor = [UIColor colorWithPatternImage:[UIImage imageNamed:@"bg_credit_choice"]];
//    [self.view addSubview:self.scrollView];
    
    self.view.backgroundColor = [UIColor colorWithPatternImage:[UIImage imageNamed:@"bg_credit_choice"]];
    
    self.dropShadow = [[UIImageView alloc]initWithFrame:CGRectMake(0, 0+offsetForIOS6, 320, 4)];
    self.dropShadow.image = [UIImage imageNamed:@"grd_navbar_drop_shadow"];
    [self.view addSubview:self.dropShadow];
    
    self.merchant = [[UILabel alloc]initWithFrame:CGRectMake(0, 14, 320, 30)];
    self.merchant.font = [UIFont fontWithName:@"HelveticaNeue" size:28];
    self.merchant.text = self.merchantName;
    self.merchant.textColor = UIColorFromRGB(0x3a3a3a);
    self.merchant.textAlignment = NSTextAlignmentCenter;
    self.merchant.backgroundColor = [UIColor clearColor];
    [self.view addSubview:self.merchant];
    
    self.amountBackground = [[UIView alloc]initWithFrame:CGRectMake(0, 54, 320, 50)];
    self.amountBackground.backgroundColor = [UIColor colorWithPatternImage:[UIImage imageNamed:@"bg_blue"]];
    [self.view addSubview:self.amountBackground];
    
    self.credit = [[UILabel alloc]initWithFrame:CGRectMake(12, 8, 100, 35)];
    self.credit.font = [UIFont fontWithName:@"HelveticaNeue-Bold" size:33];
    self.credit.text = [NSString stringWithFormat:NSLocalizedString(@"currency format", nil), self.amount];
    self.credit.textColor = UIColorFromRGB(0x144887);
    self.credit.textAlignment = NSTextAlignmentLeft;
    self.credit.backgroundColor = [UIColor clearColor];
    [self.amountBackground addSubview:self.credit];
    
    self.giftCard = [[UILabel alloc]initWithFrame:CGRectMake(230, 20, 90, 20)];
    self.giftCard.font = [UIFont fontWithName:@"HelveticaNeue" size:20];
    self.giftCard.text = NSLocalizedString(@"Gift Card", nil);
    self.giftCard.textColor = UIColorFromRGB(0x144887);
    self.giftCard.textAlignment = NSTextAlignmentLeft;
    self.giftCard.backgroundColor = [UIColor clearColor];
    [self.amountBackground addSubview:self.giftCard];

    self.userInfo = [[UILabel alloc]initWithFrame:CGRectMake(19, 116, 320, 20)];
    self.userInfo.font = [UIFont fontWithName:@"HelveticaNeue-Light" size:15];
    self.userInfo.text = NSLocalizedString(@"Customer Info", nil);
    self.userInfo.textColor = UIColorFromRGB(0x888686);
    self.userInfo.textAlignment = NSTextAlignmentLeft;
    self.userInfo.backgroundColor = [UIColor clearColor];
    [self.view addSubview:self.userInfo];
    
    self.phoneNumber = [[KikbakTextField alloc]initWithFrame:CGRectMake(11,140,298,35)];
    self.phoneNumber.delegate = self;
    self.phoneNumber.tag = 
    self.phoneNumber.placeholder = NSLocalizedString(@"Phone Number", nil);
    [self.view addSubview:self.phoneNumber];
    
//    self.name = [[KikbakTextField alloc]initWithFrame:CGRectMake(11,186,298,35)];
//    self.name.delegate = self;
//    
//    self.name.placeholder = NSLocalizedString(@"First Last Name", nil);
//    [self.scrollView addSubview:self.name];
//    
//    self.street = [[KikbakTextField alloc]initWithFrame:CGRectMake(11,232,298,35)];
//    self.street.delegate = self;
//    self.street.placeholder = NSLocalizedString(@"Street", nil);
//    [self.scrollView addSubview:self.street];
//    
//    self.apt = [[KikbakTextField alloc]initWithFrame:CGRectMake(11,278,298,35)];
//    self.apt.delegate = self;
//    self.apt.placeholder = NSLocalizedString(@"Apt", nil);
//    [self.scrollView addSubview:self.apt];
//    
//    self.city = [[KikbakTextField alloc]initWithFrame:CGRectMake(11,324,298,35)];
//    self.city.delegate = self;
//    self.city.placeholder = NSLocalizedString(@"City", nil);
//    [self.scrollView addSubview:self.city];
//    
//    self.state = [[KikbakTextField alloc]initWithFrame:CGRectMake(11,370,298,35)];
//    self.state.delegate = self;
//    self.state.placeholder = NSLocalizedString(@"State", nil);
//    [self.scrollView addSubview:self.state];
    
//    self.zip = [[KikbakTextField alloc]initWithFrame:CGRectMake(11,416,298,35)];
//    self.zip.delegate = self;
//    self.zip.placeholder = NSLocalizedString(@"Zip", nil);
//    [self.scrollView addSubview:self.zip];
    
    self.submitBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    self.submitBtn.frame = CGRectMake(11, self.view.frame.size.height - 120, 298, 40);
    [self.submitBtn setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
    [self.submitBtn setBackgroundImage:[UIImage imageNamed:@"btn_blue"] forState:UIControlStateNormal];
    [self.submitBtn setTitle:NSLocalizedString(@"Submit", nil) forState:UIControlStateNormal];
    [self.submitBtn addTarget:self action:@selector(onSubmitBtn:) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:self.submitBtn];
    
}

-(void)manuallyLayoutSubviews{
//    if( ![UIDevice hasFourInchDisplay]){
//        [self.scrollView setContentSize: CGSizeMake(320, 600)];
//    }
}


#pragma mark - Notifications
-(void)onClaimSuccess:(NSNotification*)notification{
    self.submitBtn.enabled = YES;
    CGRect frame = ((AppDelegate*)[UIApplication sharedApplication].delegate).window.frame;
    ClaimSuccessView* view = [[ClaimSuccessView alloc]initWithFrame:frame];
    [view createSubviews];
    view.delegate = self;
    [self.view addSubview:view];
}

-(void)onClaimError:(NSNotification*)notification{
    NSLog(@"onClaimError");
    self.submitBtn.enabled = YES;
    [self showError];
}

-(IBAction)keyboardWillShow:(NSNotification*)notification{
//    if( ![UIDevice hasFourInchDisplay]){
//        [self.scrollView setContentSize: CGSizeMake(320, 820)];
//    }
//    else{
//        [self.scrollView setContentSize: CGSizeMake(320, 730)];
//    }
}

-(IBAction)keyboardWillHide:(NSNotification*)notification{
//    if( ![UIDevice hasFourInchDisplay]){
//        [self.scrollView setContentSize: CGSizeMake(320, 560)];
//    }
//    else{
//        [self.scrollView setContentSize: CGSizeMake(320, 515)];
//    }
}

-(void)showError{
    UIAlertView* alert = [[UIAlertView alloc]initWithTitle:NSLocalizedString(@"Claim Error Title", nil) message:NSLocalizedString(@"Claim Error Msg", nil) delegate:nil cancelButtonTitle:@"Ok" otherButtonTitles:nil];
    [alert show];
}

#pragma mark - btn actions
-(BOOL)validInput{
    if( self.phoneNumber.text == nil ){// ||
//       self.name.text == nil ||
//       self.street.text == nil ||
//       self.city.text == nil ||
//       self.state.text == nil ||
//       self.zip.text == nil){
        [self showError];
        return false;
    }
    return true;
}

-(IBAction)onSubmitBtn:(id)sender{
    
    if( [self validInput] == NO){
        return;
    }
    
    [Flurry logEvent:@"claim credit"];
    
    NSMutableDictionary* dict = [[NSMutableDictionary alloc] initWithCapacity:8];
    [dict setObject:[NSNumber numberWithLong:1] forKey:@"creditId"];
    [dict setObject:self.phoneNumber.text forKey:@"phoneNumber"];
//    [dict setObject:self.name.text forKey:@"name"];
//    [dict setObject:self.street.text forKey:@"street"];
//    if( self.apt.text != nil ){
//        [dict setObject:self.apt.text forKey:@"apt"];
//    }
//    else{
//        [dict setObject:@"" forKey:@"apt"];
//    }
//    [dict setObject:self.city.text forKey:@"city"];
//    [dict setObject:self.state.text forKey:@"state"];
//    [dict setObject:self.zip.text forKey:@"zipcode"];
    
    ClaimCreditRequest* request = [[ClaimCreditRequest alloc]init];
    [request restRequest:dict];
    [self.active resignFirstResponder];
    self.submitBtn.enabled = NO;
}

#pragma mark - text field delegate
- (BOOL)textFieldShouldReturn:(UITextField *)textField{
    [textField resignFirstResponder];
    return true;
}

- (void)textFieldDidBeginEditing:(UITextField *)textField{
    self.active = textField;
    int base = 0;
    if( ![UIDevice hasFourInchDisplay]){
        base = 80;
    }
    
//    if( textField == self.name){
//        self.scrollView.contentOffset = CGPointMake(0, base);
//    }
//    if( textField == self.street){
//        self.scrollView.contentOffset = CGPointMake(0, base + 40);
//    }
//    else if( textField == self.apt){
//        self.scrollView.contentOffset = CGPointMake(0, base + 80);
//    }
//    else if(textField == self.city){
//        self.scrollView.contentOffset = CGPointMake(0, base + 120);
//    }
//    else if(textField == self.state){
//        self.scrollView.contentOffset = CGPointMake(0, base + 170);
//    }
//    else if(textField == self.zip){
//        self.scrollView.contentOffset = CGPointMake(0, base + 230);
//    }
}

- (void)textFieldDidEndEditing:(UITextField *)textField{
//    self.scrollView.contentOffset = CGPointMake(0, 0);
    self.active = nil;
}

#pragma mark - claim success delegate
-(void)onClaimFinished{
    [self.navigationController popToRootViewControllerAnimated:YES];
}

-(IBAction)onBackBtn:(id)sender{
    [self.navigationController popViewControllerAnimated:YES];
}

@end
