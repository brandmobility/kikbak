//
//  RedeemTableViewCell.m
//  Kikbak
//
//  Created by Ian Barile on 3/27/13.
//  Copyright (c) 2013 Ian Barile. All rights reserved.
//

#import "OfferTableViewCell.h"
#import "Offer.h"
#import "Location.h"
#import "Distance.h"
#import "ImagePersistor.h"
#import "AppDelegate.h"
#import "util.h"

@interface OfferTableViewCell()

@property (nonatomic,strong) UIImageView* topGradient;
@property (nonatomic,strong) UIImageView* retailerImage;
@property (nonatomic,strong) UIImageView* retailerImageGradient;
@property (nonatomic,strong) UILabel* retailerName;
@property (nonatomic,strong) UIButton* mapBtn;
@property (nonatomic,strong) UIImageView* mapIcon;
@property (nonatomic,strong) UILabel* distance;
@property (nonatomic,strong) UIButton* webBtn;
@property (nonatomic,strong) UIButton* callBtn;
@property (nonatomic,strong) UIImageView* giveImage;
@property (nonatomic,strong) UILabel* give;
@property (nonatomic,strong) UILabel* giveDiscount;
@property (nonatomic,strong) UIImageView* getImage;
@property (nonatomic,strong) UILabel* get;
@property (nonatomic,strong) UILabel* getDiscount;
@property (nonatomic,strong) Location* location;

-(void)createSubviews;
-(IBAction)onMap:(id)sender;
-(IBAction)onWeb:(id)sender;
-(IBAction)onCall:(id)sender;

@end

@implementation OfferTableViewCell

- (id)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier
{
    self = [super initWithStyle:style reuseIdentifier:reuseIdentifier];
    if (self) {
        // Initialization code
        self.frame = CGRectMake(0, 0, 320, 156);
        self.backgroundColor = [UIColor clearColor];
        self.selectionStyle = UITableViewCellSelectionStyleNone;
        [self createSubviews];
    }
    return self;
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated
{
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}

-(void)createSubviews{

    self.topGradient = [[UIImageView alloc]initWithImage:[UIImage imageNamed:@"grd_offer_cell_top_bottom"]];
    self.topGradient.frame = CGRectMake(0, 0, 320, 16);
    [self addSubview:self.topGradient];
    
    self.retailerImage = [[UIImageView alloc]initWithImage:[UIImage imageNamed:@"img1_offer"]];
    self.retailerImage.frame = CGRectMake(0, 16, 320, 140);
    [self addSubview:self.retailerImage];

    self.retailerImageGradient = [[UIImageView alloc]initWithImage:[UIImage imageNamed:@"grd_background"]];
    self.retailerImageGradient.frame = CGRectMake(0, 39, 320, 117);
    [self addSubview:self.retailerImageGradient];
    
    self.retailerName = [[UILabel alloc]initWithFrame:CGRectMake(11, 108, 200, 25)];
    self.retailerName.font = [UIFont fontWithName:@"HelveticaNeue" size:21];
    self.retailerName.text = @"Verizon Wireless";
    self.retailerName.textColor = [UIColor whiteColor];
    self.retailerName.textAlignment = NSTextAlignmentLeft;
    self.retailerName.backgroundColor = [UIColor clearColor];
    [self addSubview:self.retailerName];

    UIImage* mapIcon = [UIImage imageNamed:@"ic_map"];
    self.mapIcon = [[UIImageView alloc]initWithImage:mapIcon];
    self.mapIcon.frame = CGRectMake(11, 135, mapIcon.size.width, mapIcon.size.height);
    [self addSubview:self.mapIcon];
    
    self.distance = [[UILabel alloc]initWithFrame:CGRectMake(23, 133, 80, 16)];
    self.distance.font = [UIFont fontWithName:@"HelveticaNeue" size:15];
    self.distance.text = @"1.7 mi";
    self.distance.textColor = [UIColor whiteColor];
    self.distance.textAlignment = NSTextAlignmentLeft;
    self.distance.backgroundColor = [UIColor clearColor];
    [self addSubview:self.distance];
    
    
    self.mapBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    self.mapBtn.frame = CGRectMake(11, 125, 60, 30);
    [self.mapBtn addTarget:self action:@selector(onMap:) forControlEvents:UIControlEventTouchUpInside];
   // self.mapBtn.backgroundColor = [UIColor greenColor];
    [self addSubview:self.mapBtn];

    self.webBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    self.webBtn.frame = CGRectMake(81, 126, 26, 30);
    self.webBtn.contentMode = UIViewContentModeCenter;
    //self.webBtn.backgroundColor = [UIColor redColor];
    [self.webBtn setImage:[UIImage imageNamed:@"ic_web"] forState:UIControlStateNormal];
    [self.webBtn addTarget:self action:@selector(onWeb:) forControlEvents:UIControlEventTouchUpInside];
    [self addSubview:self.webBtn];
    
    self.callBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    self.callBtn.frame = CGRectMake(109, 125, 26, 31);
    self.callBtn.contentMode = UIViewContentModeLeft;
    //self.callBtn.backgroundColor = [UIColor greenColor];
    [self.callBtn setImage:[UIImage imageNamed:@"ic_phone"] forState:UIControlStateNormal];
    [self.callBtn addTarget:self action:@selector(onCall:) forControlEvents:UIControlEventTouchUpInside];
    [self addSubview:self.callBtn];
    
    self.getImage = [[UIImageView alloc]initWithImage:[UIImage imageNamed:@"label2_price"]];
    self.getImage.frame = CGRectMake(239, 74, 70, 82);
    [self addSubview:self.getImage];
    
    self.giveImage = [[UIImageView alloc]initWithImage:[UIImage imageNamed:@"label1_price"]];
    self.giveImage.frame = CGRectMake(232, 10, 77, 80);
    [self addSubview:self.giveImage];
    
    self.give = [[UILabel alloc]initWithFrame:CGRectMake(239, 30, 74, 13)];
    self.give.font = [UIFont fontWithName:@"HelveticaNeue-Bold" size:13];
    self.give.text = NSLocalizedString(@"Give", nil);
    self.give.shadowColor = UIColorFromRGB(0x3a3a3a);
    self.give.textColor = [UIColor whiteColor];
    self.give.textAlignment = NSTextAlignmentCenter;
    self.give.backgroundColor = [UIColor clearColor];
    [self addSubview:self.give];

    self.giveDiscount = [[UILabel alloc]initWithFrame:CGRectMake(239, 44, 74, 24)];
    self.giveDiscount.font = [UIFont fontWithName:@"HelveticaNeue-Bold" size:24];
    self.giveDiscount.text = @"20%";
    self.giveDiscount.shadowColor = UIColorFromRGB(0x3a3a3a);
    self.giveDiscount.textColor = [UIColor whiteColor];
    self.giveDiscount.textAlignment = NSTextAlignmentCenter;
    self.giveDiscount.backgroundColor = [UIColor clearColor];
    [self addSubview:self.giveDiscount];
    
    self.get = [[UILabel alloc]initWithFrame:CGRectMake(239, 104, 74, 13)];
    self.get.font = [UIFont fontWithName:@"HelveticaNeue-Bold" size:13];
    self.get.text = NSLocalizedString(@"Get", nil);
    self.get.textColor = [UIColor whiteColor];
    self.get.textAlignment = NSTextAlignmentCenter;
    self.get.backgroundColor = [UIColor clearColor];
    [self addSubview:self.get];

    
    self.getDiscount = [[UILabel alloc]initWithFrame:CGRectMake(239, 118, 74, 24)];
    self.getDiscount.font = [UIFont fontWithName:@"HelveticaNeue-Bold" size:24];
    self.getDiscount.text = @"20%";
    self.getDiscount.textColor = [UIColor whiteColor];
    self.getDiscount.textAlignment = NSTextAlignmentCenter;
    self.getDiscount.backgroundColor = [UIColor clearColor];
    [self addSubview:self.getDiscount];
}


-(void)setup:(int)index
{
    if( index == 0){
        self.topGradient.image = [UIImage imageNamed:@"grd_offer_cell_bottom"];
    }
    
    self.retailerName.text = self.offer.merchantName;
    if ([self.offer.giftType compare:@"percentage"] == NSOrderedSame) {
        self.giveDiscount.text = [NSString stringWithFormat:NSLocalizedString(@"precentage format", nil), self.offer.giftValue];
    }
    else{
        self.giveDiscount.text = [NSString stringWithFormat:NSLocalizedString(@"currency format", nil), self.offer.giftValue];
    }

    self.getDiscount.text = [NSString stringWithFormat:NSLocalizedString(@"currency format", nil), self.offer.kikbakValue];

    //todo: find closest location
    if (self.offer.location.count > 0) {
        self.location = [self.offer.location anyObject];
    }
    
    CLLocation* current = [[CLLocation alloc]initWithLatitude:self.location.latitude.doubleValue longitude:self.location.longitude.doubleValue];
    self.distance.text = [NSString stringWithFormat:NSLocalizedString(@"miles away", nil),
                              [Distance distanceToInMiles:current]];
    
    
    NSString* imagePath = [ImagePersistor imageFileExists:self.offer.merchantId imageType:MERCHANT_IMAGE_TYPE];
    if(imagePath != nil){
        self.retailerImage.image = [[UIImage alloc]initWithContentsOfFile:imagePath];
    }
    
    if(index == 1){
        self.retailerImage.image = [UIImage imageNamed:@"img3_offer"];
    }
    else if(index == 2){
        self.retailerImage.image = [UIImage imageNamed:@"img2_offer"];
    }
}

#pragma mark - button actions
-(IBAction)onMap:(id)sender{
    NSString *stringURL = [NSString stringWithFormat:@"http://maps.apple.com/maps?q=%@,%@",
                           self.location.latitude, self.location.longitude];
    NSURL *url = [NSURL URLWithString:stringURL];
    [[UIApplication sharedApplication] openURL:url];
}

-(IBAction)onCall:(id)sender{
    NSURL* url = [[NSURL alloc]initWithString:[NSString stringWithFormat:@"tel:%@",self.location.phoneNumber]];
    if(![[UIApplication sharedApplication] canOpenURL:url]){
        UIAlertView* alert = [[UIAlertView alloc]initWithTitle:@"Hmmm..." message:@"You need to be on an iPhone to make a call" delegate:nil cancelButtonTitle:@"Ok" otherButtonTitles:nil];
        [alert show];
    }
    else{
        [[UIApplication sharedApplication]openURL:url];
    }
}


-(IBAction)onWeb:(id)sender{
    [[UIApplication sharedApplication]openURL:[[NSURL alloc]initWithString:self.offer.merchantUrl]];
}


@end
