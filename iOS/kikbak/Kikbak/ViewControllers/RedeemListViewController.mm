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

@interface RedeemListViewController (){
    bool locationResolved;
}

@property (nonatomic, strong) UITableView* table;
@property (nonatomic, strong) NSMutableArray* rewards;

-(void) manuallyLayoutSubviews;
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
	// Do any additional setup after loading the view.
    self.navigationController.navigationBar.topItem.title = NSLocalizedString(@"Kikbak", nil);
    self.table = [[UITableView alloc]initWithFrame:self.view.frame style:UITableViewStylePlain];
    self.table.dataSource = self;
    self.table.delegate = self;
    [self.view addSubview:self.table];
    
    UINavigationBar* bar = self.navigationController.navigationBar;
    [bar setBackgroundImage:[UIImage imageNamed:@"titlebar_gradient"] forBarMetrics:UIBarMetricsDefault];
    
    if(((AppDelegate*)[UIApplication sharedApplication].delegate).locationMgr.currentLocation != nil)
        locationResolved = YES;
    else{
        locationResolved = NO;
    }
}

- (void)viewWillAppear:(BOOL)animated{
    [super viewWillAppear:animated];
    
    [[NSNotificationCenter defaultCenter]addObserver:self selector:@selector(onLocationUpdate:) name:kKikbakLocationUpdate object:nil];
    [[NSNotificationCenter defaultCenter]addObserver:self selector:@selector(onRewaredCollectionUpdate:) name:kKikbakRewardUpdate object:nil];
    
    [self createRewardCollection];
    [self.table reloadData];
}

-(void)viewDidAppear:(BOOL)animated{
    [super viewDidAppear:animated];
    [self manuallyLayoutSubviews];
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
    return 155;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath{
    
    RedeemTableViewCell* cell = [tableView dequeueReusableCellWithIdentifier:@"redeemCell"];
    if( cell == nil){
        cell = [[RedeemTableViewCell alloc]initWithStyle:UITableViewCellStyleDefault reuseIdentifier:@"redeemCell"];
    }
    
    cell.selectionStyle = UITableViewCellSelectionStyleGray;
    cell.rewards = [self.rewards objectAtIndex:indexPath.row];
    [cell setup];
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
        CGRect frame = ((AppDelegate*)[UIApplication sharedApplication].delegate).window.frame;
        RedeemChooserView* chooser = [[RedeemChooserView alloc]initWithFrame:frame];
        [chooser manuallyLayoutSubviews];
        chooser.delegate = self;
        [((AppDelegate*)[UIApplication sharedApplication].delegate).window addSubview:chooser];
    }
    else if(collection.gift != nil){
        RewardCollection* collection = [self.rewards objectAtIndex:indexPath.row ];
        [self performSegueWithIdentifier:@"RedeemGiftSegue" sender:collection.gift];
    }
    else if(collection.credit != nil){
        RewardCollection* collection = [self.rewards objectAtIndex:indexPath.row ];
        [self performSegueWithIdentifier:@"RedeemCreditSegue" sender:collection.credit];
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

-(void)onRedeemCredit:(Kikbak*)credit{
    [self performSegueWithIdentifier:@"RedeemCreditSegue" sender:credit];
}

-(void)onRedeemGift:(Gift*)gift{
    [self performSegueWithIdentifier:@"RedeemGiftSegue" sender:gift];
}

@end
