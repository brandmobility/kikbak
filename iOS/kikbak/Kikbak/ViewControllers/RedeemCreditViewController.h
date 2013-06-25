//
//  RedeemCreditViewController.h
//  Kikbak
//
//  Created by Ian Barile on 5/25/13.
//  Copyright (c) 2013 Ian Barile. All rights reserved.
//

#import <UIKit/UIKit.h>

@protocol UserCreditUpdate <NSObject>

-(void)onUpdateAmount:(NSNumber*)amount;

@end

@class Kikbak;

@interface RedeemCreditViewController : UIViewController<UserCreditUpdate>

@property(nonatomic,strong)Kikbak* credit;

@end
