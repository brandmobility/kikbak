//
//  RedeemCreditViewController.m
//  Kikbak
//
//  Created by Ian Barile on 5/25/13.
//  Copyright (c) 2013 Ian Barile. All rights reserved.
//

#import "RedeemCreditViewController.h"
#import "CreditChooserViewController.h"
#import "NotificationContstants.h"
#import "Kikbak.h"

@interface RedeemCreditViewController ()

@property (nonatomic,strong) UIButton* changeAmountBtn;

-(IBAction)onChangeAmount:(id)sender;

-(void) onRedeemKikbakSuccess:(NSNotification*)notification;
-(void) onRedeemKikbakError:(NSNotification*)notification;

@end

@implementation RedeemCreditViewController

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
    
    self.changeAmountBtn = [UIButton buttonWithType:UIButtonTypeRoundedRect];
    self.changeAmountBtn.frame = CGRectMake(0, 0, 80, 40);
    [self.changeAmountBtn addTarget:self action:@selector(onChangeAmount:) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:self.changeAmountBtn];
}


-(void)viewWillAppear:(BOOL)animated{
    [super viewWillAppear:animated];
    self.changeAmountBtn.center = self.view.center;
    
    [[NSNotificationCenter defaultCenter]addObserver:self selector:@selector(onRedeemKikbakSuccess:) name:kKikbakRedeemKikbakSuccess object:nil];
    [[NSNotificationCenter defaultCenter]addObserver:self selector:@selector(onRedeemKikbakError:) name:kKikbakRedeemKikbakError object:nil];
}

-(void)viewWillDisappear:(BOOL)animated{
    [super viewWillDisappear:animated];
 
    [[NSNotificationCenter defaultCenter]removeObserver:self name:kKikbakRedeemKikbakSuccess object:nil];
    [[NSNotificationCenter defaultCenter]removeObserver:self name:kKikbakRedeemKikbakError object:nil];

}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

#pragma mark - button handlers
-(IBAction)onChangeAmount:(id)sender{
    CreditChooserViewController* vc = [[CreditChooserViewController alloc]init];
    vc.creditAvailable = [NSNumber numberWithDouble:10.51]; //self.credit.value;
    [self.navigationController pushViewController:vc animated:YES];
}


#pragma mark - NSNotification Handlers
-(void) onRedeemKikbakSuccess:(NSNotification*)notification{
    
}

-(void) onRedeemKikbakError:(NSNotification*)notification{
    
}

@end
