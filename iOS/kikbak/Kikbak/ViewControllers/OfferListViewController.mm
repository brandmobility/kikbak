//
//  OfferListViewController.m
//  Kikbak
//
//  Created by Ian Barile on 3/27/13.
//  Copyright (c) 2013 Ian Barile. All rights reserved.
//

#import "OfferListViewController.h"
#import "GiveViewController.h"
#import "OfferTableViewCell.h"
#import "OfferService.h"
#import "OfferTableViewCell.h"
#import "Offer.h"
#import "AppDelegate.h"
#import "LocationManager.h"
#import "NotificationContstants.h"
#import "SuggestViewController.h"

const int CELL_HEIGHT = 156;

@interface OfferListViewController (){
    bool locationResolved;
}

@property (nonatomic, strong) UITableView* table;
@property (nonatomic, strong) NSArray* offers;
@property (nonatomic, strong) UIButton* giveBtn;
@property (nonatomic, strong) UIButton* redeemBtn;
@property (nonatomic, strong) UIImageView* seperator;

-(void) manuallyLayoutSubviews;
-(void) onLocationUpdate:(NSNotification*)notification;
-(void) onOfferUpdate:(NSNotification*)notification;

-(IBAction)onSuggest:(id)sender;
-(IBAction)onGiveBtn:(id)sender;
-(IBAction)onRedeemBtn:(id)sender;

@end

@implementation OfferListViewController

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
    self.navigationController.navigationBar.topItem.title = NSLocalizedString(@"Kikbak", nil);
    
    self.table = [[UITableView alloc]initWithFrame:self.view.frame style:UITableViewStylePlain];
    self.table.dataSource = self;
    self.table.separatorStyle = UITableViewCellSeparatorStyleNone;
    self.table.delegate = self;
    self.table.backgroundColor = [UIColor colorWithPatternImage:[UIImage imageNamed:@"bg_offer"]];
    [self.view addSubview:self.table];
    
    
    UINavigationBar* bar = self.navigationController.navigationBar;
    [bar setBackgroundImage:[UIImage imageNamed:@"grd_navigationbar"] forBarMetrics:UIBarMetricsDefault];
    
    if(((AppDelegate*)[UIApplication sharedApplication].delegate).locationMgr.currentLocation != nil)
        locationResolved = YES;
    else{
        locationResolved = NO;
    }
    
    UIImage *suggestImage = [UIImage imageNamed:@"btn_suggest"];
    UIButton *suggestButton = [UIButton buttonWithType:UIButtonTypeCustom];
    suggestButton.frame = CGRectMake(0, 0, suggestImage.size.width, suggestImage.size.height);
    
    [suggestButton setBackgroundImage:suggestImage forState:UIControlStateNormal];
    [suggestButton setBackgroundImage:suggestImage forState:UIControlStateHighlighted];
    [suggestButton setTitle:NSLocalizedString(@"Suggest",nil) forState:UIControlStateNormal];
    suggestButton.titleLabel.font = [UIFont boldSystemFontOfSize:12];
    suggestButton.contentHorizontalAlignment = UIControlContentHorizontalAlignmentCenter;
    [suggestButton addTarget:self action:@selector(onSuggest:)    forControlEvents:UIControlEventTouchUpInside];
    UIBarButtonItem *backBarButtonItem = [[UIBarButtonItem alloc] initWithCustomView:suggestButton];
    backBarButtonItem.style = UIBarButtonItemStylePlain;
    
    self.navigationItem.hidesBackButton = YES;
    self.navigationItem.rightBarButtonItem = backBarButtonItem;
    
    UITabBar* tabBar = self.tabBarController.tabBar;
    tabBar.backgroundColor = [UIColor clearColor];
    CGRect tabBarFR = tabBar.frame;
    
    self.giveBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    self.giveBtn.frame = CGRectMake(0, tabBarFR.origin.y, 159, tabBarFR.size.height);
    self.giveBtn.titleLabel.font = [UIFont fontWithName:@"HelveticaNeue-Medium" size:15];
    [self.giveBtn setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
    [self.giveBtn setTitleColor:[UIColor whiteColor] forState:UIControlStateDisabled];
    [self.giveBtn setTitle:NSLocalizedString(@"Give", nil) forState:UIControlStateDisabled];
    [self.giveBtn setTitle:NSLocalizedString(@"Give", nil) forState:UIControlStateNormal];
    [self.giveBtn setBackgroundImage:[UIImage imageNamed:@"btn_highlighted"] forState:UIControlStateDisabled];
    [self.giveBtn setBackgroundImage:[UIImage imageNamed:@"btn_normal"] forState:UIControlStateNormal];
    [self.giveBtn addTarget:self action:@selector(onGiveBtn:) forControlEvents:UIControlEventTouchUpInside];
    self.giveBtn.enabled = NO;
    [self.tabBarController.view addSubview:self.giveBtn];
    
    self.seperator = [[UIImageView alloc]initWithFrame:CGRectMake(159, tabBarFR.origin.y, 1, tabBarFR.size.height)];
    self.seperator.image = [UIImage imageNamed:@"separator"];
    [self.tabBarController.view addSubview:self.seperator];
    
    self.redeemBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    self.redeemBtn.frame = CGRectMake(160, tabBarFR.origin.y, 160, tabBarFR.size.height);
    self.redeemBtn.titleLabel.font = [UIFont fontWithName:@"HelveticaNeue-Medium" size:15];
    [self.redeemBtn setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
    [self.redeemBtn setTitleColor:[UIColor whiteColor] forState:UIControlStateDisabled];
    [self.redeemBtn setTitle:NSLocalizedString(@"Redeem", nil) forState:UIControlStateDisabled];
    [self.redeemBtn setTitle:NSLocalizedString(@"Redeem", nil) forState:UIControlStateNormal];
    [self.redeemBtn setBackgroundImage:[UIImage imageNamed:@"btn_highlighted"] forState:UIControlStateDisabled];
    [self.redeemBtn setBackgroundImage:[UIImage imageNamed:@"btn_normal"] forState:UIControlStateNormal];
    [self.redeemBtn addTarget:self action:@selector(onRedeemBtn:) forControlEvents:UIControlEventTouchUpInside];
    [self.tabBarController.view addSubview:self.redeemBtn];
}

- (void)viewWillAppear:(BOOL)animated{
    [super viewWillAppear:animated];
    [[NSNotificationCenter defaultCenter]addObserver:self selector:@selector(onLocationUpdate:) name:kKikbakLocationUpdate object:nil];
    [[NSNotificationCenter defaultCenter]addObserver:self selector:@selector(onOfferUpdate:) name:kKikbakImageDownloaded object:nil];
    
    [self.tabBarController.view addSubview:self.redeemBtn];
    [self.tabBarController.view addSubview:self.seperator];
    [self.tabBarController.view addSubview:self.giveBtn];
    
    self.offers = [OfferService getOffers];
    [self.table reloadData];
}

-(void)viewDidAppear:(BOOL)animated{
    [super viewDidAppear:animated];
    [self manuallyLayoutSubviews];
}

-(void)viewWillDisappear:(BOOL)animated{
    [self.redeemBtn removeFromSuperview];
    [self.seperator removeFromSuperview];
    [self.giveBtn removeFromSuperview];
}

-(void)viewDidDisappear:(BOOL)animated{
    
    [[NSNotificationCenter defaultCenter]removeObserver:self name:kKikbakImageDownloaded object:nil];
    [[NSNotificationCenter defaultCenter]removeObserver:self name:kKikbakLocationUpdate object:nil];
    [super viewDidDisappear:animated];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

-(void)viewDidLayoutSubviews{
    [self manuallyLayoutSubviews];
}

-(void) manuallyLayoutSubviews{
    self.table.frame = self.view.frame;
}

#pragma mark - table datasource methods
- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath{
    return CELL_HEIGHT;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath{
    
    OfferTableViewCell* cell = [tableView dequeueReusableCellWithIdentifier:@"redeemCell"];
    if( cell == nil){
        cell = [[OfferTableViewCell alloc]initWithStyle:UITableViewCellStyleDefault reuseIdentifier:@"redeemCell"];
    }
    
    cell.offer = [self.offers objectAtIndex:indexPath.row];
    [cell setup];
    
    return cell;
}


- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section{
    
    if( locationResolved ){
        return [self.offers count];
    }

    return 0;
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView{
    return 1;
}

#pragma mark - segue
- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath{
    [self performSegueWithIdentifier:@"OfferSegue" sender:[self.offers objectAtIndex:indexPath.row]];
}

- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender{
    GiveViewController* vc = (GiveViewController*)segue.destinationViewController;
    vc.offer = sender;
    [segue.destinationViewController setHidesBottomBarWhenPushed:YES];
}


#pragma mark - NSNotification Handlers
-(void) onLocationUpdate:(NSNotification*)notification{
    locationResolved = YES;
    [self.table reloadData];
}

-(void) onOfferUpdate:(NSNotification*)notification{
    self.offers = [OfferService getOffers];
    [self.table reloadData];
}

#pragma mark - btns
-(IBAction)onSuggest:(id)sender{
    SuggestViewController* svc = [[SuggestViewController alloc]init];
    [svc setHidesBottomBarWhenPushed:YES];
    [self.navigationController pushViewController:svc animated:YES];
}

-(IBAction)onGiveBtn:(id)sender{
    self.tabBarController.selectedIndex = 0;
    self.redeemBtn.enabled = YES;
    self.giveBtn.enabled = NO;
}

-(IBAction)onRedeemBtn:(id)sender{
    self.tabBarController.selectedIndex = 1;
    self.redeemBtn.enabled = NO;
    self.giveBtn.enabled = YES;
}

@end
