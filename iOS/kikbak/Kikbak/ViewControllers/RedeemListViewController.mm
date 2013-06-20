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
#import "Kikbak.h"
#import "GiftService.h"
#import "KikbakService.h"
#import "AppDelegate.h"
#import "LocationManager.h"
#import "RedeemGiftViewController.h"
#import "RewardCollection.h"
#import "NotificationContstants.h"
#import "RedeemChooserView.h"
#import "RedeemCreditViewController.h"
#import "SuggestViewController.h"


const int CELL_HEIGHT = 156;

@interface RedeemListViewController (){
    bool locationResolved;
}

@property (nonatomic, strong) UITableView* table;
@property (nonatomic, strong) NSMutableArray* rewards;
@property (nonatomic, strong) UIButton* giveBtn;
@property (nonatomic, strong) UIButton* redeemBtn;
@property (nonatomic, strong) UIImageView* seperator;


-(void) manuallyLayoutSubviews;
-(void) createRewardCollection;

-(IBAction)onSuggest:(id)sender;

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
	// Do any additional setup after loading the view.
    self.navigationController.navigationBar.topItem.title = NSLocalizedString(@"Kikbak", nil);
    self.table = [[UITableView alloc]initWithFrame:self.view.frame style:UITableViewStylePlain];
    self.table.dataSource = self;
    self.table.delegate = self;
    self.table.backgroundColor = [UIColor colorWithPatternImage:[UIImage imageNamed:@"bg_offwhite_eggshell"]];
    [self.view addSubview:self.table];
    
    UINavigationBar* bar = self.navigationController.navigationBar;
    [bar setBackgroundImage:[UIImage imageNamed:@"grd_navigationbar"] forBarMetrics:UIBarMetricsDefault];
    
    if(((AppDelegate*)[UIApplication sharedApplication].delegate).locationMgr.currentLocation != nil)
        locationResolved = YES;
    else{
        locationResolved = NO;
    }
        
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
}

- (void)viewWillAppear:(BOOL)animated{
    [super viewWillAppear:animated];
    
    [[NSNotificationCenter defaultCenter]addObserver:self selector:@selector(onLocationUpdate:) name:kKikbakLocationUpdate object:nil];
    [[NSNotificationCenter defaultCenter]addObserver:self selector:@selector(onRewaredCollectionUpdate:) name:kKikbakRewardUpdate object:nil];
    
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
    
    RedeemTableViewCell* cell = [tableView dequeueReusableCellWithIdentifier:@"redeemCell"];
    if( cell == nil){
        cell = [[RedeemTableViewCell alloc]initWithStyle:UITableViewCellStyleDefault reuseIdentifier:@"redeemCell"];
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

#pragma mark - segue
- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath{

    RewardCollection* collection = [self.rewards objectAtIndex:indexPath.row];
    if (collection.gift != nil && collection.credit != nil) {
        [tableView deselectRowAtIndexPath:indexPath animated:YES];
        RedeemTableViewCell* redeemCell = (RedeemTableViewCell*)[tableView cellForRowAtIndexPath:indexPath];
        CGRect frame = ((AppDelegate*)[UIApplication sharedApplication].delegate).window.frame;
        RedeemChooserView* chooser = [[RedeemChooserView alloc]initWithFrame:frame];
        chooser.credit = redeemCell.creditValue.text;
        chooser.gift = redeemCell.giftValue.text;
        [chooser manuallyLayoutSubviews];
        chooser.delegate = self;
        [((AppDelegate*)[UIApplication sharedApplication].delegate).window addSubview:chooser];
    }
    else if(collection.gift != nil){
        RedeemGiftViewController* vc = [[RedeemGiftViewController alloc]init];
        vc.hidesBottomBarWhenPushed = true;
        vc.gift = collection.gift;
        [self.navigationController pushViewController:vc animated:YES];
    }
    else if(collection.credit != nil){
        RedeemCreditViewController* vc = [[RedeemCreditViewController alloc]init];
        vc.hidesBottomBarWhenPushed = true;
        vc.credit = collection.credit;
        [self.navigationController pushViewController:vc animated:YES];
    }

}

- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender{
    if( [segue.destinationViewController isKindOfClass:[RedeemGiftViewController class]]){
        RedeemGiftViewController* vc = (RedeemGiftViewController*)segue.destinationViewController;
        vc.gift = sender;
    }
    else{
        RedeemCreditViewController* vc = (RedeemCreditViewController*)segue.destinationViewController;
        vc.credit = sender;
    }
    [segue.destinationViewController setHidesBottomBarWhenPushed:YES];
}


#pragma mark - NSNotification Handlers
-(void) onLocationUpdate:(NSNotification*)notification{
    locationResolved = YES;
    [self.table reloadData];
}

-(void) onRewaredCollectionUpdate:(NSNotification*)notification{
    [self createRewardCollection];
    [self.table reloadData];
}

-(void) createRewardCollection{
    
    NSArray* gifts = [GiftService getGifts];
    NSArray* kikbaks = [KikbakService getKikbaks];
    
    NSMutableDictionary* dict = [[NSMutableDictionary alloc]initWithCapacity:[gifts count]];
    
    for(Gift* gift in gifts){
        RewardCollection* collection = [[RewardCollection alloc]init];
        collection.gift = gift;
        [dict setObject:collection forKey:gift.merchantId];
    }
    
    for(Kikbak* kikbak in kikbaks){
        if([dict objectForKey:kikbak.merchantId]){
            ((RewardCollection*)[dict objectForKey:kikbak.merchantId]).credit = kikbak;
        }
        else{
            RewardCollection* collection = [[RewardCollection alloc]init];
            collection.credit = kikbak;
            [dict setObject:collection forKey:kikbak.merchantId];
        }
    }
    
    self.rewards = [[NSMutableArray alloc]init];
    for(RewardCollection* collection in [dict allValues]){
        [self.rewards addObject:collection];
    }
}

#pragma mark - btn actions

-(void)onRedeemCredit:(Kikbak*)credit{
    RedeemCreditViewController* vc = [[RedeemCreditViewController alloc]init];
    vc.credit = credit;
    vc.hidesBottomBarWhenPushed = true;
    [self.navigationController pushViewController:vc animated:YES];
}

-(void)onRedeemGift:(Gift*)gift{
    RedeemGiftViewController* vc = [[RedeemGiftViewController alloc]init];
    vc.gift = gift;
    vc.hidesBottomBarWhenPushed = true;
    [self.navigationController pushViewController:vc animated:YES];
}

-(IBAction)onGiveBtn:(id)sender{
    self.tabBarController.selectedIndex = 0;
}

-(IBAction)onRedeemBtn:(id)sender{
    self.tabBarController.selectedIndex = 1;

}

-(IBAction)onSuggest:(id)sender{
    SuggestViewController* svc = [[SuggestViewController alloc]init];
    [svc setHidesBottomBarWhenPushed:YES];
    [self.navigationController pushViewController:svc animated:YES];
}

@end
