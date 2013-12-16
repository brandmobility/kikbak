//
//  RedeemListViewController.m
//  Kikbak
//
//  Created by Ian Barile on 3/27/13.
//  Copyright (c) 2013 Ian Barile. All rights reserved.
//

#import "RedeemListViewController.h"
#import "RedeemTableViewCell.h"
#import "Gift.h"
#import "Credit.h"
#import "GiftService.h"
#import "CreditService.h"
#import "AppDelegate.h"
#import "LocationManager.h"
#import "RedeemGiftViewController.h"
#import "RedeemClaimViewController.h"
#import "RewardCollection.h"
#import "NotificationContstants.h"
#import "RedeemChooserView.h"
#import "RedeemCreditViewController.h"
#import "SuggestViewController.h"
#import "TermsAndConditionsView.h"
#import "util.h"
#import "UIDevice+Screen.h"
#import "UIDevice+OSVersion.h"
#import <map>
#import "Distance.h"
#import "Location.h"
#import "FriendSelectorView.h"
#import <Flurry.h>


const int CELL_HEIGHT = 206;

static int offsetForIOS6 = 44;

@interface RedeemListViewController (){
    bool locationResolved;
}

@property (nonatomic, strong) UITableView* table;
@property (nonatomic, strong) NSMutableArray* rewards;
@property (nonatomic, strong) UIButton* giveBtn;
@property (nonatomic, strong) UIButton* redeemBtn;
@property (nonatomic, strong) UIImageView* seperator;

@property (nonatomic, strong) UIView* emptyListView;
@property (nonatomic, strong) UILabel* doh;
@property (nonatomic, strong) UILabel* receivedCredit;
@property (nonatomic, strong) UILabel* earnCredit;
@property (nonatomic, strong) UIButton* seeOffersBtn;


-(void) createSubviews;
-(void) manuallyLayoutSubviews;
-(void) toggleViews;

-(void) createRewardCollection;



@end

@implementation RedeemListViewController

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

    self.navigationController.navigationBar.topItem.title = NSLocalizedString(@"Kikbak", nil);
    UINavigationBar* bar = self.navigationController.navigationBar;
    [bar setBackgroundImage:[UIImage imageNamed:@"grd_navigationbar"] forBarMetrics:UIBarMetricsDefault];
    
    if(((AppDelegate*)[UIApplication sharedApplication].delegate).locationMgr.currentLocation != nil)
        locationResolved = YES;
    else{
        locationResolved = NO;
    }
    self.hidesBottomBarWhenPushed = YES;

    if( [UIDevice osVersion7orGreater] ){
        offsetForIOS6 = 0;
    }
    
    [self createSubviews];
    [self manuallyLayoutSubviews];
}

- (void)viewWillAppear:(BOOL)animated{
    [super viewWillAppear:animated];
    
    [Flurry logEvent:@"redeem list"];
    
    [[NSNotificationCenter defaultCenter]addObserver:self selector:@selector(onLocationUpdate:) name:kKikbakLocationUpdate object:nil];
    [[NSNotificationCenter defaultCenter]addObserver:self selector:@selector(onRewaredCollectionUpdate:) name:kKikbakRewardUpdate object:nil];
        [[NSNotificationCenter defaultCenter]addObserver:self selector:@selector(onOffersDownloaded:) name:kKikbakOffersDownloaded object:nil];
    
    [self.tabBarController.view addSubview:self.redeemBtn];
    [self.tabBarController.view addSubview:self.seperator];
    [self.tabBarController.view addSubview:self.giveBtn];
    

    [self createRewardCollection];
    [self.table reloadData];
}

-(void)viewDidAppear:(BOOL)animated{
    [super viewDidAppear:animated];
    [self manuallyLayoutSubviews];
}

-(void)viewWillDisappear:(BOOL)animated{
    [super viewWillDisappear:animated];
    [self.redeemBtn removeFromSuperview];
    [self.seperator removeFromSuperview];
    [self.giveBtn removeFromSuperview];
}

-(void)viewDidDisappear:(BOOL)animated{
    [[NSNotificationCenter defaultCenter]removeObserver:self name:kKikbakRewardUpdate object:nil];
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
}

-(void) createSubviews{
    UITabBar* tabBar = self.tabBarController.tabBar;
    tabBar.backgroundColor = [UIColor clearColor];
    CGRect tabBarFR = tabBar.frame;
    
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
    self.giveBtn.enabled = YES;
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
    self.redeemBtn.enabled = NO;
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
    self.table.delegate = self;
    self.table.backgroundColor = [UIColor colorWithPatternImage:[UIImage imageNamed:@"bg_offwhite_eggshell"]];

    self.emptyListView = [[UILabel alloc]initWithFrame:fr];
    self.emptyListView.backgroundColor = [UIColor colorWithPatternImage:[UIImage imageNamed:@"bg_offwhite_eggshell"]];
    self.emptyListView.clipsToBounds = YES;
    self.emptyListView.userInteractionEnabled = YES;
    
    self.doh = [[UILabel alloc]initWithFrame:CGRectMake(0, 88, 320, 62)];
    self.doh.text = NSLocalizedString(@"Doh", nil);
    self.doh.textAlignment = NSTextAlignmentCenter;
    self.doh.textColor = UIColorFromRGB(0x3a3a3a);
    self.doh.font = [UIFont fontWithName:@"Helvetica-Bold" size:31];
    self.doh.backgroundColor = [UIColor clearColor];
    [self.emptyListView addSubview:self.doh];
    
    
    self.receivedCredit = [[UILabel alloc]initWithFrame:CGRectMake(51, 138, 216, 40)];
    self.receivedCredit.text = NSLocalizedString(@"You haven't received any gifts or earned any credit", nil);
    self.receivedCredit.textAlignment = NSTextAlignmentCenter;
    self.receivedCredit.textColor = UIColorFromRGB(0x3a3a3a);
    self.receivedCredit.font = [UIFont fontWithName:@"Helvetica" size:16];
    self.receivedCredit.backgroundColor = [UIColor clearColor];
    self.receivedCredit.numberOfLines = 2;
    [self.emptyListView addSubview:self.receivedCredit];
    
    self.earnCredit = [[UILabel alloc]initWithFrame:CGRectMake(45, 226, 230, 60)];
    self.earnCredit.text = NSLocalizedString(@"Change empty gifts", nil);
    self.earnCredit.textAlignment = NSTextAlignmentCenter;
    self.earnCredit.textColor = UIColorFromRGB(0x3a3a3a);
    self.earnCredit.font = [UIFont fontWithName:@"Helvetica" size:13];
    self.earnCredit.backgroundColor = [UIColor clearColor];
    self.earnCredit.numberOfLines = 3;
    [self.emptyListView addSubview:self.earnCredit];
    
    
    self.seeOffersBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    self.seeOffersBtn.frame = CGRectMake(45, 285, 230, 40);
    [self.seeOffersBtn setBackgroundImage:[UIImage imageNamed:@"btn_blue"] forState:UIControlStateNormal];
    [self.seeOffersBtn setTitle:NSLocalizedString(@"See Offers", nil) forState:UIControlStateNormal];
    [self.seeOffersBtn setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
    self.seeOffersBtn.titleLabel.font = [UIFont fontWithName:@"HelveticaNeue-Bold" size:15];
    [self.seeOffersBtn addTarget:self action:@selector(onGiveBtn:) forControlEvents:UIControlEventTouchUpInside];
    [self.emptyListView addSubview:self.seeOffersBtn];
    
}

-(void) manuallyLayoutSubviews{
    if(![UIDevice hasFourInchDisplay]){
        self.doh.frame = CGRectMake(0, 53 + offsetForIOS6, 320, 62);
        self.receivedCredit.frame = CGRectMake(51, 104 + offsetForIOS6, 216, 40);
        self.earnCredit.frame = CGRectMake(45, 195 + offsetForIOS6, 230, 60);
        self.seeOffersBtn.frame = CGRectMake(45, 258 + offsetForIOS6, 230, 40);
        
        CGRect fr =  self.view.bounds;
        if( ![UIDevice osVersion7orGreater] ){
            fr.origin.y += self.navigationController.navigationBar.frame.size.height;
            fr.size.height -= self.navigationController.navigationBar.frame.size.height;
        }
        fr.size.height = 367; //hieght of view - (navbar + tab bar)
        self.table.frame = fr;
    }
    [self toggleViews];
}

-(void)toggleViews{
    if( [self.rewards count] && locationResolved ){
        [self.emptyListView removeFromSuperview];
        [self.view addSubview:self.table];
    }
    else{
        [self.table removeFromSuperview];
        [self.view addSubview:self.emptyListView];
    }
}

#pragma mark - table datasource methods
- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath{
    return CELL_HEIGHT;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath{
    
    RedeemTableViewCell* cell = [tableView dequeueReusableCellWithIdentifier:@"redeemCell"];
    if( cell == nil){
        cell = [[RedeemTableViewCell alloc]initWithStyle:UITableViewCellStyleDefault reuseIdentifier:@"redeemCell"];
        cell.delegate = self;
    }
    
    cell.selectionStyle = UITableViewCellSelectionStyleGray;
    cell.rewards = [self.rewards objectAtIndex:indexPath.row];
    [cell setup ];//:indexPath.row];
    return cell;
}


- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section{
    if (locationResolved) {
        return [self.rewards count];
    }
    return 0;
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView{
    return 1;
}


- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath{

    RewardCollection* collection = [self.rewards objectAtIndex:indexPath.row];
    if (collection.gift != nil && collection.credit != nil) {
        [tableView deselectRowAtIndexPath:indexPath animated:YES];
        RedeemTableViewCell* redeemCell = (RedeemTableViewCell*)[tableView cellForRowAtIndexPath:indexPath];
        CGRect frame = ((AppDelegate*)[UIApplication sharedApplication].delegate).window.frame;
        RedeemChooserView* chooser = [[RedeemChooserView alloc]initWithFrame:frame];
        chooser.credit = redeemCell.creditValue.text;
        chooser.gift = redeemCell.giftValue.text;
        chooser.collection = collection;
        [chooser manuallyLayoutSubviews];
        chooser.delegate = self;
        [((AppDelegate*)[UIApplication sharedApplication].delegate).window addSubview:chooser];
    }
    else if(collection.gift != nil){
        if( [collection.gift.shareInfo count] == 1){
            RedeemGiftViewController* vc = [[RedeemGiftViewController alloc]init];
            vc.hidesBottomBarWhenPushed = true;
            vc.gift = collection.gift;
            vc.shareInfo = [collection.gift.shareInfo anyObject];
            [self.navigationController pushViewController:vc animated:YES];
        }
        else{
            CGRect frame = ((AppDelegate*)[UIApplication sharedApplication].delegate).window.frame;
            frame.origin.y +=  offsetForIOS6;
            FriendSelectorView* view = [[FriendSelectorView alloc]initWithFrame:frame];
            view.gift = collection.gift;
            view.delegate = self;
            [view createSubviews];
            [self.view addSubview:view];
        }
    }
    else if(collection.credit != nil){
        if( [collection.credit.rewardType compare:@"gift_card"] != NSOrderedSame){
            RedeemCreditViewController* vc = [[RedeemCreditViewController alloc]init];
            vc.hidesBottomBarWhenPushed = true;
            vc.credit = collection.credit;
            [self.navigationController pushViewController:vc animated:YES];
        }
        else{
            RedeemClaimViewController* vc = [[RedeemClaimViewController alloc]init];
            vc.credit = collection.credit;
            [self.navigationController pushViewController:vc animated:YES];
        }
    }

}

#pragma mark - segue
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender{
    if( [segue.destinationViewController isKindOfClass:[RedeemGiftViewController class]]){
        RedeemGiftViewController* vc = (RedeemGiftViewController*)segue.destinationViewController;
        vc.gift = sender;
    }
    else{
//        RedeemCreditViewController* vc = (RedeemCreditViewController*)segue.destinationViewController;
//        vc.credit = sender;
    }
    [segue.destinationViewController setHidesBottomBarWhenPushed:YES];
}


#pragma mark - NSNotification Handlers
-(void) onLocationUpdate:(NSNotification*)notification{
    locationResolved = YES;
    [self toggleViews];
    [self.table reloadData];
}

-(void) onRewaredCollectionUpdate:(NSNotification*)notification{
    [self createRewardCollection];
    [self toggleViews];
    [self.table reloadData];
}

-(void) onOffersDownloaded:(NSNotification*)notification{
    [self.table reloadData];
}

-(void) createRewardCollection{
    
    NSArray* gifts = [GiftService getGifts];
    NSArray* credits = [CreditService getCredits];
    
    NSMutableDictionary* dict = [[NSMutableDictionary alloc]initWithCapacity:[gifts count]];
    
    for(Gift* gift in gifts){
        RewardCollection* collection = [[RewardCollection alloc]init];
        collection.gift = gift;
        [dict setObject:collection forKey:gift.merchantId];
    }
    
    for(Credit* credit in credits){
        if([dict objectForKey:credit.merchantId]){
            ((RewardCollection*)[dict objectForKey:credit.merchantId]).credit = credit;
        }
        else{
            RewardCollection* collection = [[RewardCollection alloc]init];
            collection.credit = credit;
            [dict setObject:collection forKey:credit.merchantId];
        }
    }
    
    std::map<double, id> ordered;
    for(RewardCollection* collection in [dict allValues]){
        Location* location;
        if( collection.gift ){
            location = [collection.gift.location anyObject];
        }
        else{
            location = [collection.credit.location anyObject];
        }
        double distance = [Distance distanceToInFeet:[[CLLocation alloc]initWithLatitude:location.latitude.doubleValue
                                                                               longitude:location.longitude.doubleValue]];
        ordered[distance] = collection;
    }

    self.rewards = [[NSMutableArray alloc]init];
    for (std::map<double, id>::const_iterator cit = ordered.begin(); cit != ordered.end(); ++cit) {
        [self.rewards addObject:cit->second];
    }
}

#pragma mark - btn actions

-(void)onRedeemCredit:(Credit*)credit{
    UIViewController* vc;
    if( [credit.rewardType compare:@"gift_card"] != NSOrderedSame){
        vc = [[RedeemCreditViewController alloc]init];
        ((RedeemCreditViewController* )vc).credit = credit;
    }
    else{
        vc = [[RedeemClaimViewController alloc]init];
        ((RedeemClaimViewController*)vc).credit = credit;
    }
    vc.hidesBottomBarWhenPushed = true;
    [self.navigationController pushViewController:vc animated:YES];
}

-(void)onRedeemGift:(Gift*)gift{
    if( [gift.shareInfo count] == 1){
        RedeemGiftViewController* vc = [[RedeemGiftViewController alloc]init];
        vc.gift = gift;
        vc.shareInfo = [gift.shareInfo anyObject];
        vc.hidesBottomBarWhenPushed = true;
        [self.navigationController pushViewController:vc animated:YES];
    }
    else{
        CGRect frame = ((AppDelegate*)[UIApplication sharedApplication].delegate).window.frame;
        frame.origin.y += offsetForIOS6;
        FriendSelectorView* view = [[FriendSelectorView alloc]initWithFrame:frame];
        view.gift = gift;
        view.delegate = self;
        [view createSubviews];
        [self.view addSubview:view];
    }
}

-(void)onRedeemGift:(Gift*)gift withShareInfo:(ShareInfo*)shareInfo{
    RedeemGiftViewController* vc = [[RedeemGiftViewController alloc]init];
    vc.gift = gift;
    vc.shareInfo = shareInfo;
    vc.hidesBottomBarWhenPushed = true;
    [self.navigationController pushViewController:vc animated:YES];
}

-(IBAction)onGiveBtn:(id)sender{
    self.tabBarController.selectedIndex = 0;
}

-(IBAction)onRedeemBtn:(id)sender{
    self.tabBarController.selectedIndex = 1;

}


@end
