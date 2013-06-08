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


const int CELL_HEIGHT = 156;

@interface OfferListViewController (){
    bool locationResolved;
}

@property (nonatomic, strong) UITableView* table;
@property (nonatomic, strong) NSArray* offers;

-(void) manuallyLayoutSubviews;
-(void) onLocationUpdate:(NSNotification*)notification;
-(void) onOfferUpdate:(NSNotification*)notification;

-(IBAction)onSuggest:(id)sender;

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
    self.table.backgroundColor = [UIColor colorWithPatternImage:[UIImage imageNamed:@"offer_bg"]];
    [self.view addSubview:self.table];
    
    
    UINavigationBar* bar = self.navigationController.navigationBar;
    [bar setBackgroundImage:[UIImage imageNamed:@"grd_navigationbar"] forBarMetrics:UIBarMetricsDefault];
    
    if(((AppDelegate*)[UIApplication sharedApplication].delegate).locationMgr.currentLocation != nil)
        locationResolved = YES;
    else{
        locationResolved = NO;
    }
    
    UIImage *backImage = [UIImage imageNamed:@"btn_suggest"];
    UIButton *backButton = [UIButton buttonWithType:UIButtonTypeCustom];
    backButton.frame = CGRectMake(0, 0, backImage.size.width, backImage.size.height);
    
    [backButton setBackgroundImage:backImage forState:UIControlStateNormal];
    [backButton setBackgroundImage:backImage forState:UIControlStateHighlighted];
    [backButton setTitle:NSLocalizedString(@"Suggest",nil) forState:UIControlStateNormal];
    backButton.titleLabel.font = [UIFont boldSystemFontOfSize:12];
    backButton.contentHorizontalAlignment = UIControlContentHorizontalAlignmentCenter;
    [backButton addTarget:self action:@selector(onSuggest:)    forControlEvents:UIControlEventTouchUpInside];
    UIBarButtonItem *backBarButtonItem = [[UIBarButtonItem alloc] initWithCustomView:backButton];
    backBarButtonItem.style = UIBarButtonItemStylePlain;
    
    self.navigationItem.hidesBackButton = YES;
    self.navigationItem.rightBarButtonItem = backBarButtonItem;
    
    UITabBar* tabBar = self.tabBarController.tabBar;
    tabBar.se
}

- (void)viewWillAppear:(BOOL)animated{
    [super viewWillAppear:animated];
    [[NSNotificationCenter defaultCenter]addObserver:self selector:@selector(onLocationUpdate:) name:kKikbakLocationUpdate object:nil];
    [[NSNotificationCenter defaultCenter]addObserver:self selector:@selector(onOfferUpdate:) name:kKikbakImageDownloaded object:nil];
    
    
    self.offers = [OfferService getOffers];
    [self.table reloadData];
}

-(void)viewDidAppear:(BOOL)animated{
    [super viewDidAppear:animated];
    [self manuallyLayoutSubviews];
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
    
}

@end
