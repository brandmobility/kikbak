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
#import "util.h"
#import "UIDevice+Screen.h"
#import "UIDevice+OSVersion.h"
#import "FBQuery.h"
#import "Flurry.h"


const int CELL_HEIGHT = 156;

@interface OfferListViewController (){
    bool locationResolved;
}

@property (nonatomic, strong) UIImageView* navbarDropShadow;

@property (nonatomic, strong) UITableView* table;
@property (nonatomic, strong) NSArray* offers;
@property (nonatomic, strong) UIButton* giveBtn;
@property (nonatomic, strong) UIButton* redeemBtn;
@property (nonatomic, strong) UIImageView* seperator;

//No offers
@property (nonatomic, strong) UIView* emptyListView;
@property (nonatomic, strong) UILabel* bummer;
@property (nonatomic, strong) UILabel* bummerDetails;

@property (nonatomic, strong) UILabel* suggestBusiness;
@property (nonatomic, strong) UIButton* suggestBusinessBtn;
@property (nonatomic, strong) UIButton* fbRequestBtn;

-(void) createSubviews;
-(void) manuallyLayoutSubviews;
-(void) createSuggestBtn;
-(void) toggleViews;

-(void) onLocationUpdate:(NSNotification*)notification;
-(void) onImageDownloaded:(NSNotification*)notification;
-(void) onOffersDownloaded:(NSNotification*)notification;

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
    UINavigationBar* bar = self.navigationController.navigationBar;
    [bar setBackgroundImage:[UIImage imageNamed:@"grd_navigationbar"] forBarMetrics:UIBarMetricsDefault];
    
    if(((AppDelegate*)[UIApplication sharedApplication].delegate).locationMgr.currentLocation != nil)
        locationResolved = YES;
    else{
        locationResolved = NO;
    }
    self.hidesBottomBarWhenPushed = YES;
    
    [self createSubviews];
    [self manuallyLayoutSubviews];
}

- (void)viewWillAppear:(BOOL)animated{
    [super viewWillAppear:animated];
    [[NSNotificationCenter defaultCenter]addObserver:self selector:@selector(onLocationUpdate:) name:kKikbakLocationUpdate object:nil];
    [[NSNotificationCenter defaultCenter]addObserver:self selector:@selector(onImageDownloaded:) name:kKikbakImageDownloaded object:nil];
    [[NSNotificationCenter defaultCenter]addObserver:self selector:@selector(onOffersDownloaded:) name:kKikbakOffersDownloaded object:nil];
    
    self.offers = [OfferService getOffers];
    [self toggleViews];
    [self.table reloadData];
    [self manuallyLayoutSubviews];
    
    [self.tabBarController.view addSubview:self.redeemBtn];
    [self.tabBarController.view addSubview:self.seperator];
    [self.tabBarController.view addSubview:self.giveBtn];
    
    [Flurry logEvent:@"Offer List"];
    
    CLAuthorizationStatus status = [CLLocationManager authorizationStatus];
    if( ![CLLocationManager locationServicesEnabled] || status != kCLAuthorizationStatusAuthorized ){
        UIAlertView* alert = [[UIAlertView alloc]initWithTitle:NSLocalizedString(@"Location Required", nil) message:NSLocalizedString(@"Enable Location", nil) delegate:nil cancelButtonTitle:@"Ok" otherButtonTitles:nil];
        [alert show];
    }
}

-(void)viewDidAppear:(BOOL)animated{
    [super viewDidAppear:animated];
    
}

-(void)viewWillDisappear:(BOOL)animated{
    [self.redeemBtn removeFromSuperview];
    [self.seperator removeFromSuperview];
    [self.giveBtn removeFromSuperview];
}

-(void)viewDidDisappear:(BOOL)animated{
    
    [[NSNotificationCenter defaultCenter]removeObserver:self name:kKikbakImageDownloaded object:nil];
    [[NSNotificationCenter defaultCenter]removeObserver:self name:kKikbakLocationUpdate object:nil];
    [[NSNotificationCenter defaultCenter]removeObserver:self name:kKikbakOffersDownloaded object:nil];

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

-(void) createSuggestBtn{
    //Suggest btn
    UIImage *suggestImage = [UIImage imageNamed:@"btn_navbar_rect"];
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
}

-(void) createSubviews{
    UITabBar* tabBar = self.tabBarController.tabBar;
    tabBar.backgroundColor = [UIColor clearColor];
    CGRect tabBarFR = tabBar.frame;
    
    self.navbarDropShadow = [[UIImageView alloc]initWithFrame:CGRectMake(0, 0, 320, 4)];
    self.navbarDropShadow.image = [UIImage imageNamed:@"grd_navbar_drop_shadow"];
    [self.view addSubview:self.navbarDropShadow];
    
    self.giveBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    self.giveBtn.frame = CGRectMake(0, tabBarFR.origin.y, 159, tabBarFR.size.height);
    self.giveBtn.titleLabel.font = [UIFont fontWithName:@"HelveticaNeue-Medium" size:15];
    [self.giveBtn setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
    [self.giveBtn setTitleColor:[UIColor whiteColor] forState:UIControlStateDisabled];
    [self.giveBtn setTitle:NSLocalizedString(@"Give_tab_bar", nil) forState:UIControlStateDisabled];
    [self.giveBtn setTitle:NSLocalizedString(@"Give_tab_bar", nil) forState:UIControlStateNormal];
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
    self.redeemBtn.enabled = YES;
    [self.tabBarController.view addSubview:self.redeemBtn];
    
    CGRect fr =  self.view.bounds;
    if( ![UIDevice osVersion7orGreater] ){
        fr.origin.y += self.navigationController.navigationBar.frame.size.height;
    }
    if( [UIDevice osVersion7orGreater] ){
        fr.size.height -= [UIApplication sharedApplication].statusBarFrame.size.height;
    }
    fr.size.height -= self.navigationController.navigationBar.frame.size.height;
    fr.size.height -= self.tabBarController.tabBar.frame.size.height;
    self.table = [[UITableView alloc]initWithFrame:fr style:UITableViewStylePlain];
    self.table.dataSource = self;
    self.table.separatorStyle = UITableViewCellSeparatorStyleNone;
    self.table.delegate = self;
    self.table.backgroundColor = [UIColor colorWithPatternImage:[UIImage imageNamed:@"bg_offwhite_eggshell"]];
    

    self.emptyListView = [[UILabel alloc]initWithFrame:fr];
    self.emptyListView.backgroundColor = [UIColor colorWithPatternImage:[UIImage imageNamed:@"bg_offwhite_eggshell"]];
    self.emptyListView.clipsToBounds = YES;
    self.emptyListView.userInteractionEnabled = YES;
    
    self.bummer = [[UILabel alloc]initWithFrame:CGRectMake(0, 66, 320, 62)];
    self.bummer.text = NSLocalizedString(@"Bummer", nil);
    self.bummer.textAlignment = NSTextAlignmentCenter;
    self.bummer.textColor = UIColorFromRGB(0x3a3a3a);
    self.bummer.font = [UIFont fontWithName:@"Helvetica-Bold" size:31];
    self.bummer.backgroundColor = [UIColor clearColor];
    [self.emptyListView addSubview:self.bummer];
    
    
    self.bummerDetails = [[UILabel alloc]initWithFrame:CGRectMake(65, 123, 190, 40)];
    self.bummerDetails.text = NSLocalizedString(@"There are no Kikbak offers", nil);
    self.bummerDetails.textAlignment = NSTextAlignmentCenter;
    self.bummerDetails.textColor = UIColorFromRGB(0x3a3a3a);
    self.bummerDetails.font = [UIFont fontWithName:@"Helvetica" size:16];
    self.bummerDetails.backgroundColor = [UIColor clearColor];
    self.bummerDetails.numberOfLines = 2;
    [self.emptyListView addSubview:self.bummerDetails];
    
    
    self.suggestBusiness = [[UILabel alloc]initWithFrame:CGRectMake(31, 336, 258, 40)];
    self.suggestBusiness.text = NSLocalizedString(@"Tell Us", nil);
    self.suggestBusiness.textAlignment = NSTextAlignmentCenter;
    self.suggestBusiness.textColor = UIColorFromRGB(0x3a3a3a);
    self.suggestBusiness.font = [UIFont fontWithName:@"Helvetica" size:13];
    self.suggestBusiness.backgroundColor = [UIColor clearColor];
    self.suggestBusiness.numberOfLines = 2;
    [self.emptyListView addSubview:self.suggestBusiness];
    
    
    self.suggestBusinessBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    self.suggestBusinessBtn.frame = CGRectMake(50, 378, 220, 40);
    [self.suggestBusinessBtn setBackgroundImage:[UIImage imageNamed:@"btn_blue"] forState:UIControlStateNormal];
    [self.suggestBusinessBtn setTitle:NSLocalizedString(@"Suggest for Kikbak", nil) forState:UIControlStateNormal];
    [self.suggestBusinessBtn setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
    self.suggestBusinessBtn.titleLabel.font = [UIFont fontWithName:@"HelveticaNeue-Bold" size:15];
    [self.suggestBusinessBtn addTarget:self action:@selector(onSuggest:) forControlEvents:UIControlEventTouchUpInside];
    [self.emptyListView addSubview:self.suggestBusinessBtn];

}

-(void) manuallyLayoutSubviews{
    

    if( ![UIDevice hasFourInchDisplay]){
        self.bummer.frame = CGRectMake(0, 35, 320, 62);
        self.bummerDetails.frame = CGRectMake(65, 86, 190, 40);
        self.suggestBusiness.frame = CGRectMake(31, 259, 258, 40);
        self.suggestBusinessBtn.frame = CGRectMake(50, 304, 220, 40);
        CGRect fr =  self.view.bounds;
        if( ![UIDevice osVersion7orGreater] ){
            fr.origin.y += self.navigationController.navigationBar.frame.size.height;
            fr.size.height -= self.tabBarController.tabBar.frame.size.height;
        }
        fr.size.height = 367; //hieght of view - (navbar + tab bar)
        self.table.frame = fr;
    }
}

-(void)toggleViews{
    if( [self.offers count] && locationResolved ){
        [self.emptyListView removeFromSuperview];
        [self.view addSubview:self.table];
        [self createSuggestBtn];
    }
    else{
        [self.table removeFromSuperview];
        self.navigationItem.rightBarButtonItem = nil;
        [self.view addSubview:self.emptyListView];
    }
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
    [cell setup:indexPath.row];
    
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

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath{
    GiveViewController* vc = [[GiveViewController alloc]initWithNibName:nil bundle:nil];
    vc.offer = [self.offers objectAtIndex:indexPath.row];
    [self.navigationController pushViewController:vc animated:YES];
}


#pragma mark - NSNotification Handlers
-(void) onLocationUpdate:(NSNotification*)notification{
    locationResolved = YES;
    [self toggleViews];
    [self.table reloadData];
}

-(void) onImageDownloaded:(NSNotification*)notification{
    [self toggleViews];
    [self.table reloadData];
}

-(void) onOffersDownloaded:(NSNotification*)notification{
    self.offers = [OfferService getOffers];
    [self toggleViews];
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
}

-(IBAction)onRedeemBtn:(id)sender{
    self.tabBarController.selectedIndex = 1;
}


@end
