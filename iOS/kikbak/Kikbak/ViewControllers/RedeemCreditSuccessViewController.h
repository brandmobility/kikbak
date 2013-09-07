//
//  RedeemCreditSuccessViewController.h
//  Kikbak
//
//  Created by Ian Barile on 6/25/13.
//  Copyright (c) 2013 Ian Barile. All rights reserved.
//

#import <UIKit/UIKit.h>

@class Kikbak;

@interface RedeemCreditSuccessViewController : UIViewController

@property(nonatomic,strong)NSNumber* creditUsed;
@property(nonatomic,strong)NSNumber* offerId;
@property(nonatomic,strong)NSString* merchantName;
@property(nonatomic,strong)NSString* validationCode;
@property(nonatomic,strong)NSString* imagePath;

@end
