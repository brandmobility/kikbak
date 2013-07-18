//
//  ClaimCreditViewController.m
//  Kikbak
//
//  Created by Ian Barile on 7/15/13.
//  Copyright (c) 2013 Ian Barile. All rights reserved.
//

#import "ClaimCreditViewController.h"
#import "ClaimCreditRequest.h"

@interface ClaimCreditViewController ()

@property (nonatomic, strong) UIScrollView* scrollView;
@property (nonatomic, strong) UILabel* merchantName;
@property (nonatomic, strong) UIView* amountBackground;
@property (nonatomic, strong) UILabel* amount;
@property (nonatomic, strong) UILabel* giftCard;
@property (nonatomic, strong) UILabel* userInfo;

@property (nonatomic, strong) UITextView* phoneNumber;
@property (nonatomic, strong) UITextView* name;
@property (nonatomic, strong) UITextView* street;
@property (nonatomic, strong) UITextView* apt;
@property (nonatomic, strong) UITextView* city;
@property (nonatomic, strong) UITextView* state;
@property (nonatomic, strong) UITextView* zip;

@property (nonatomic, strong) UILabel* arrival;
@property (nonatomic, strong) UIButton* submitBtn;

-(void)createSubviews;
-(void)manuallyLayoutSubviews;

-(void)onClaimSuccess:(NSNotification*)notification;
-(void)onClaimError:(NSNotification*)notification;

-(IBAction)onSubmitBtn:(id)sender;

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
    
    [self createSubviews];
}

-(void)viewWillAppear:(BOOL)animated{
    
}

-(void)viewWillDisappear:(BOOL)animated{
    
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}


-(void)createSubviews{
    
    self.scrollView = [[UIScrollView alloc]initWithFrame:CGRectMake(0,0, self.view.frame.size.width, self.view.frame.size.height)];
    [self.view addSubview:self.scrollView];
    
    self.submitBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    self.submitBtn.frame = CGRectMake(11, 30, 298, 40);
    [self.submitBtn setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
    [self.submitBtn setBackgroundImage:[UIImage imageNamed:@"btn_blue"] forState:UIControlStateNormal];
    [self.submitBtn setTitle:NSLocalizedString(@"Let us know", nil) forState:UIControlStateNormal];
    [self.submitBtn addTarget:self action:@selector(onSubmitBtn:) forControlEvents:UIControlEventTouchUpInside];
    [self.scrollView addSubview:self.submitBtn];
    
}

-(void)manuallyLayoutSubviews{
    
}


#pragma mark - Notifications
-(void)onClaimSuccess:(NSNotification*)notification{
    NSLog(@"OnClaimSuccess");
}

-(void)onClaimError:(NSNotification*)notification{
    NSLog(@"onClaimError");
}

#pragma mark - btn actions
-(IBAction)onSubmitBtn:(id)sender{
    
    NSMutableDictionary* dict = [[NSMutableDictionary alloc] initWithCapacity:8];
    [dict setObject:[NSNumber numberWithLong:1] forKey:@"creditId"];
    [dict setObject:@"Test" forKey:@"phoneNumber"];
    [dict setObject:@"Test" forKey:@"name"];
    [dict setObject:@"Test" forKey:@"street"];
    [dict setObject:@"Test" forKey:@"apt"];
    [dict setObject:@"Test" forKey:@"city"];
    [dict setObject:@"Test" forKey:@"state"];
    [dict setObject:@"Test" forKey:@"zipcode"];
    
    ClaimCreditRequest* request = [[ClaimCreditRequest alloc]init];
    [request restRequest:dict];
}

@end
