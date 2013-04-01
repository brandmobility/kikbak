//
//  RedeemViewController.h
//  kikit
//
//  Created by Ian Barile on 3/9/13.
//  Copyright (c) 2013 Ian Barile. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <ZXingWidgetController.h>

@interface RedeemViewController : UIViewController<ZXingDelegate>


@property(nonatomic, strong) IBOutlet UIImageView* retailerImage;
@property(nonatomic, strong) IBOutlet UILabel* retailer;
@property(nonatomic, strong) IBOutlet UIImageView* mapMarker;
@property(nonatomic, strong) IBOutlet UILabel* distance;
@property(nonatomic, strong) IBOutlet UIImageView* topGradient;
@property(nonatomic, strong) IBOutlet UILabel* friendName;
@property(nonatomic, strong) IBOutlet UILabel* friendCaption;
@property(nonatomic, strong) IBOutlet UIImageView* friendImage;
@property(nonatomic, strong) IBOutlet UIImageView* photoFrame;
@property(nonatomic, strong) IBOutlet UIImageView* sharedImage;
@property(nonatomic, strong) IBOutlet UIImageView* offerBackgroundImage;
@property(nonatomic, strong) IBOutlet UIImageView* giftIcon;
@property(nonatomic, strong) IBOutlet UILabel* yourGift;
@property(nonatomic, strong) IBOutlet UILabel* scan;
@property(nonatomic, strong) IBOutlet UILabel* offer;
@property(nonatomic, strong) IBOutlet UILabel* conditions;
@property(nonatomic, strong) IBOutlet UIButton* termsBtn;
@property(nonatomic, strong) IBOutlet UIButton* redeemBtn;
@property(nonatomic, strong) IBOutlet UIView* locationView;


-(IBAction)onRedeem:(id)sender;
-(IBAction)onTermsAndConditions:(id)sender;


@end
