//
//  RedeemViewController.m
//  kikit
//
//  Created by Ian Barile on 3/9/13.
//  Copyright (c) 2013 Ian Barile. All rights reserved.
//

#import "RedeemViewController.h"
#import "UIDevice+Screen.h"
#import "RedeemView.h"
#import "AppDelegate.h"
#import "QRCodeReader.h"
#import "Gift.h"
#import "Kikbak.h"
#import "Location.h"
#import "RedeemGiftRequest.h"
#import "RedeemKikbakRequest.h"
#import "RewardCollection.h"
#import "Distance.h"

@interface RedeemViewController (){
    double distanceToLocation;
}
-(void)manuallyLayoutSubviews;
-(NSDictionary*)setupKikbakRequest;
-(NSDictionary*)setupGiftRequest;
-(void) onLocationUpdate:(NSNotification*)notification;
-(void)updateDistance;
@end

@implementation RedeemViewController



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
    self.title = NSLocalizedString(@"Redeem", nil);
    self.hidesBottomBarWhenPushed = YES;
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

-(void)viewWillAppear:(BOOL)animated{
    [super viewWillAppear:animated];
    [[NSNotificationCenter defaultCenter]addObserver:self selector:@selector(onLocationUpdate:) name:kKikbakLocationUpdate object:nil];
    
    [self updateDistance];
}

-(void)viewDidAppear:(BOOL)animated{
    [super viewDidAppear:animated];

 
    [self manuallyLayoutSubviews];
}

-(void)viewDidDisappear:(BOOL)animated{
    [[NSNotificationCenter defaultCenter]removeObserver:self name:kKikbakLocationUpdate object:nil];
    [super viewDidDisappear:animated];
}

-(void)viewDidLayoutSubviews{
    [self manuallyLayoutSubviews];
}

-(void)manuallyLayoutSubviews{
    if ([UIDevice hasFourInchDisplay]) {
        //location
        CGRect fr = _locationView.frame;
        fr.size.height = 76;
        _locationView.frame = fr;
        
        fr = _retailerImage.frame;
        fr.origin.y = 12;
        _retailerImage.frame = fr;
        
        CGRect retailerFr = _retailer.frame;
        retailerFr.origin.y = 14;
        _retailer.frame = retailerFr;
        
        fr = _mapMarker.frame;
        fr.origin.y = retailerFr.origin.y + retailerFr.size.height + 6;
        _mapMarker.frame = fr;
        
        fr = _distance.frame;
        fr.origin.y = retailerFr.origin.y + retailerFr.size.height + 6;
        _distance.frame = fr;
        
        fr = _topGradient.frame;
        fr.origin.y = 76;
        _topGradient.frame = fr;
        
        fr = _friendName.frame;
        fr.origin.y = 76 + 13;
        _friendName.frame = fr;
        
        CGRect captionFr = _friendCaption.frame;
        captionFr.origin.y = 76+26;
        _friendCaption.frame = captionFr;
        
        fr = _friendImage.frame;
        fr.origin.y = 76+14;
        _friendImage.frame = fr;
        
        CGRect photoFr = _photoFrame.frame;
        photoFr.origin.y = captionFr.origin.y + captionFr.size.height + 16;
        photoFr.origin.x = 70;
        photoFr.size.width = 171;
        photoFr.size.height = 171;
        _photoFrame.frame = photoFr;
        
        fr = _sharedImage.frame;
        fr.origin.y = photoFr.origin.y + 10;
        fr.origin.x = 77;
        fr.size.width = 150;
        fr.size.height = 149;
        _sharedImage.frame = fr;
        
        CGRect offerFr = _offerBackgroundImage.frame;
        offerFr.origin.y = photoFr.origin.y + photoFr.size.height + 12;
        _offerBackgroundImage.frame = offerFr;
        
        CGRect giftIconFr = _giftIcon.frame;
        giftIconFr.origin.y = offerFr.origin.y + 9;
        _giftIcon.frame = giftIconFr;
        
        fr = _yourGift.frame;
        fr.origin.y = offerFr.origin.y + 10;
        _yourGift.frame = fr;
        
        CGRect offFr = _offer.frame;
        offFr.origin.y = giftIconFr.origin.y + giftIconFr.size.height + 7;
        _offer.frame = offFr;
        
        CGRect conditionsFr = _conditions.frame;
        conditionsFr.origin.y = offFr.origin.y + offFr.size.height - 1;
        _conditions.frame = conditionsFr;
        
        CGRect scanFr = _scan.frame;
        scanFr.origin.y = conditionsFr.origin.y + conditionsFr.size.height;
        _scan.frame = scanFr;
        
        offerFr.size.height = scanFr.origin.y + scanFr.size.height + 8 - offerFr.origin.y;
        _offerBackgroundImage.frame = offerFr;
    }
}

-(void)onRedeem:(id)sender{

//    ZXingWidgetController *widController = [[ZXingWidgetController alloc] initWithDelegate:self showCancel:YES OneDMode:NO];
//    
//    NSMutableSet *readers = [[NSMutableSet alloc ] init];
//    QRCodeReader* qrcodeReader = [[QRCodeReader alloc] init];
//    [readers addObject:qrcodeReader];
//    
//    widController.readers = readers;
//    [self presentViewController:widController animated:YES completion:nil];
    if(self.reward.kikbak != nil){
        RedeemKikbakRequest* rkr = [[RedeemKikbakRequest alloc]init];
        [rkr makeRequest:[self setupKikbakRequest]];
        
        self.reward.kikbak.location = nil;
        NSManagedObjectContext* context = ((AppDelegate*)[UIApplication sharedApplication].delegate).managedObjectContext;
        [context deleteObject:self.reward.kikbak];
        
        NSError* error;
        [context save:&error];
        
        if(error){
            NSLog(@"Unresolved error %@, %@", error, [error userInfo]);
        }
        
        self.reward.kikbak = nil;
    }
    if(self.reward.gift != nil){
        RedeemGiftRequest *request = [[RedeemGiftRequest alloc]init];
        [request makeRequest:[self setupGiftRequest]];
        
        self.reward.gift.location = nil;
        NSManagedObjectContext* context = ((AppDelegate*)[UIApplication sharedApplication].delegate).managedObjectContext;
        [context deleteObject:self.reward.gift];
        
        NSError* error;
        [context save:&error];
        
        if(error){
            NSLog(@"Unresolved error %@, %@", error, [error userInfo]);
        }
        
        self.reward.gift = nil;
    }
}

-(void)onTermsAndConditions:(id)sender{
    
}


- (void)zxingController:(ZXingWidgetController*)controller didScanResult:(NSString *)result {
    [self dismissViewControllerAnimated:NO completion:nil];
    
    CGRect frame = ((AppDelegate*)[UIApplication sharedApplication].delegate).window.frame;
    RedeemView* redeemView = [[RedeemView alloc]initWithFrame:frame];
    [redeemView manuallyLayoutSubviews];
    [((AppDelegate*)[UIApplication sharedApplication].delegate).window addSubview:redeemView];
    
    RedeemGiftRequest *request = [[RedeemGiftRequest alloc]init];
    NSMutableDictionary* dict = [[NSMutableDictionary alloc]initWithCapacity:3];
    [dict setObject:self.reward.gift.giftId forKey:@"id"];
    Location* location = [self.reward.gift.location anyObject];
    [dict setObject:location.locationId forKey:@"locationId"];
    [dict setObject:self.reward.gift.friendUserId forKey:@"friendId"];
    [dict setObject:@"zdfdw" forKey:@"verificationCode"];
    [request makeRequest:dict];
    
    self.reward.gift.location = nil;
    NSManagedObjectContext* context = ((AppDelegate*)[UIApplication sharedApplication].delegate).managedObjectContext;
    [context deleteObject:self.reward.gift];
    
    NSError* error;
    [context save:&error];
    
    if(error){
        NSLog(@"Unresolved error %@, %@", error, [error userInfo]);
    }
    
    self.reward.gift = nil;
}

- (void)zxingControllerDidCancel:(ZXingWidgetController*)controller {
    [self dismissViewControllerAnimated:YES completion:nil];
    
    RedeemGiftRequest *request = [[RedeemGiftRequest alloc]init];
    [request makeRequest:[self setupGiftRequest]];
    
    self.reward.gift.location = nil;
    NSManagedObjectContext* context = ((AppDelegate*)[UIApplication sharedApplication].delegate).managedObjectContext;
    [context deleteObject:self.reward.gift];
    
    NSError* error;
    [context save:&error];
    
    if(error){
        NSLog(@"Unresolved error %@, %@", error, [error userInfo]);
    }
    
    self.reward.gift = nil;
}

-(NSDictionary*)setupKikbakRequest{
    NSMutableDictionary* dict = [[NSMutableDictionary alloc]initWithCapacity:4];
    [dict setObject:self.reward.kikbak.kikbakId forKey:@"id"];
    Location* location = [self.reward.kikbak.location anyObject];
    [dict setObject:location.locationId forKey:@"locationId"];
    [dict setObject:self.reward.kikbak.value forKey:@"amount"];
    [dict setObject:@"zdfdw" forKey:@"verificationCode"];
    
    return dict;
}

-(NSDictionary*)setupGiftRequest{
    NSMutableDictionary* dict = [[NSMutableDictionary alloc]initWithCapacity:3];
    [dict setObject:self.reward.gift.giftId forKey:@"id"];
    Location* location = [self.reward.gift.location anyObject];
    [dict setObject:location.locationId forKey:@"locationId"];
    [dict setObject:self.reward.gift.friendUserId forKey:@"friendUserId"];
    [dict setObject:@"zdfdw" forKey:@"verificationCode"];
    
    return dict;
}

-(void) onLocationUpdate:(NSNotification*)notification{
    
}


-(void)updateDistance{

    //todo move to a function
    Location* location;
    if(self.reward.kikbak){
        location = [self.reward.gift.location anyObject];
    }
    else if( self.reward.gift){
        location = [self.reward.gift.location anyObject];
    }
    
    distanceToLocation = [Distance distanceToInFeet:[[CLLocation alloc]initWithLatitude:location.latitude.doubleValue longitude:location.longitude.doubleValue]];
}

@end
