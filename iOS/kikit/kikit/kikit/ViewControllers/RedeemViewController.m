//
//  RedeemViewController.m
//  kikit
//
//  Created by Ian Barile on 3/9/13.
//  Copyright (c) 2013 Ian Barile. All rights reserved.
//

#import "RedeemViewController.h"
#import "UIDevice+Screen.h"

@interface RedeemViewController ()
-(void)manuallyLayoutSubviews;
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
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

-(void)viewWillAppear:(BOOL)animated{
    [super viewWillAppear:animated];
    
}

-(void)viewDidAppear:(BOOL)animated{
    [super viewDidAppear:animated];

 
    [self manuallyLayoutSubviews];
}

-(void)viewDidLayoutSubviews{
    //[self manuallyLayoutSubviews];
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
        retailerFr.origin.y = 18;
        _retailer.frame = retailerFr;
        
        fr = _mapMarker.frame;
        fr.origin.y = retailerFr.origin.y + retailerFr.size.height + 9;
        _mapMarker.frame = fr;
        
        fr = _distance.frame;
        fr.origin.y = retailerFr.origin.y + retailerFr.size.height + 16;
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
        fr.origin.y = photoFr.origin.y + 8;
        fr.size.width = 151;
        fr.size.height = 151;
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


@end
