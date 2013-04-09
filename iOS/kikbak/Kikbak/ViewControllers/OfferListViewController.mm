//
//  OfferListViewController.m
//  Kikbak
//
//  Created by Ian Barile on 3/27/13.
//  Copyright (c) 2013 Ian Barile. All rights reserved.
//

#import "OfferListViewController.h"
#import "KikbakTableViewCell.h"
#import "OfferLoader.h"
#import "KikbakTableViewCell.h"
#import "AppDelegate.h"
#import "LocationManager.h"

@interface OfferListViewController (){
    bool locationResolved;
}

@property (nonatomic, strong) UITableView* table;
@property (nonatomic, strong) NSArray* offers;

-(void) manuallyLayoutSubviews;
-(void) onLocationUpdate:(NSNotification*)notification;
-(void) onOfferUpdate:(NSNotification*)notification;
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
    self.table.delegate = self;
    self.table.backgroundColor = [UIColor colorWithRed:0.835 green:0.835 blue:0.835 alpha:1];
    [self.view addSubview:self.table];
    
    self.offers = [OfferLoader getOffers];
    if(((AppDelegate*)[UIApplication sharedApplication].delegate).locationMgr.currentLocation != nil)
        locationResolved = YES;
    else{
        locationResolved = NO;
    }
}

- (void)viewWillAppear:(BOOL)animated{
    [super viewWillAppear:animated];
    [[NSNotificationCenter defaultCenter]addObserver:self selector:@selector(onLocationUpdate:) name:kKikbakLocationUpdate object:nil];
    [[NSNotificationCenter defaultCenter]addObserver:self selector:@selector(onOfferUpdate:) name:kKikbakOfferUpdate object:nil];
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
    
    KikbakTableViewCell* cell = [tableView dequeueReusableCellWithIdentifier:@"redeemCell"];
    if( cell == nil){
        cell = [[KikbakTableViewCell alloc]initWithStyle:UITableViewCellStyleDefault reuseIdentifier:@"redeemCell"];
    }
    
    [cell setup:[self.offers objectAtIndex:indexPath.row]];
    
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
    [self performSegueWithIdentifier:@"OfferSegue" sender:self];
    
}

- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender{
    [segue.destinationViewController setHidesBottomBarWhenPushed:YES];
}


#pragma mark - NSNotification Handlers
-(void) onLocationUpdate:(NSNotification*)notification{
    locationResolved = YES;
    [self.table reloadData];
}

-(void) onOfferUpdate:(NSNotification*)notification{

    self.offers = [OfferLoader getOffers];
    [self.table reloadData];
}
@end
