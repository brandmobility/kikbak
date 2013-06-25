//
//  CreditChooserViewController.h
//  Kikbak
//
//  Created by Ian Barile on 5/25/13.
//  Copyright (c) 2013 Ian Barile. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "RedeemCreditViewController.h"

@interface CreditChooserViewController : UIViewController<UITextFieldDelegate>

@property (nonatomic, strong) NSNumber* credit;
@property (nonatomic, strong) id<UserCreditUpdate> updateDelegate;
@end
