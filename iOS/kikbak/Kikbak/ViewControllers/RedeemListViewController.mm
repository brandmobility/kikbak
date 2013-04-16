//
//  RedeemListViewController.m
//  Kikbak
//
//  Created by Ian Barile on 3/27/13.
//  Copyright (c) 2013 Ian Barile. All rights reserved.
//

#import "RedeemListViewController.h"
#import "RedeemTableViewCell.h"
#import "GiftLoader.h"
#import "AppDelegate.h"
#import "LocationManager.h"
#import "RedeemViewController.h"


@interface RedeemListViewController (){
    bool locationResolved;
}

@property (nonatomic, strong) UITableView* table;
@property (nonatomic, strong) NSArray* gifts;

-(void) manuallyLayoutSubviews;

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
    
    if(((AppDelegate*)[UIApplication sharedApplication].delegate).locationMgr.currentLocation != nil)
        locationResolved = YES;
    else{
        locationResolved = NO;
    }
}

- (void)viewWillAppear:(BOOL)animated{
    [super viewWillAppear:animated];
    
    [[NSNotificationCenter defaultCenter]addObserver:self selector:@selector(onLocationUpdate:) name:kKikbakLocationUpdate object:nil];
    [[NSNotificationCenter defaultCenter]addObserver:self selector:@selector(onGiftUpdate:) name:kKikbakGiftUpdate object:nil];
    
    self.gifts = [GiftLoader getGifts];
    [self.table reloadData];
}

-(void)viewDidAppear:(BOOL)animated{
    [super viewDidAppear:animated];
    [self manuallyLayoutSubviews];
}

-(void)viewDidDisappear:(BOOL)animated{
    [[NSNotificationCenter defaultCenter]removeObserver:self name:kKikbakOfferUpdate object:nil];
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
    
    cell.gift = [self.gifts objectAtIndex:indexPath.row];
    [cell setup];
    return cell;
}


- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section{
    if (locationResolved) {
        return [self.gifts count];
    }
    return 0;
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView{
    return 1;
}

#pragma mark - segue
- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath{
    [self performSegueWithIdentifier:@"RedeemSegue" sender:[self.gifts objectAtIndex:indexPath.row ]];
    
}

- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender{
    RedeemViewController* vc = (RedeemViewController*)segue.destinationViewController;
    vc.gift = sender;
    [segue.destinationViewController setHidesBottomBarWhenPushed:YES];
}


#pragma mark - NSNotification Handlers
-(void) onLocationUpdate:(NSNotification*)notification{
    locationResolved = YES;
    [self.table reloadData];
}

-(void) onGiftUpdate:(NSNotification*)notification{
    
    self.gifts = [GiftLoader getGifts];
    [self.table reloadData];
}

@end
