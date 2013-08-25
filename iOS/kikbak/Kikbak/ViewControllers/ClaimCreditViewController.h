//
//  ClaimCreditViewController.h
//  Kikbak
//
//  Created by Ian Barile on 7/15/13.
//  Copyright (c) 2013 Ian Barile. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "ClaimSuccessView.h"

@interface ClaimCreditViewController : UIViewController<UITextFieldDelegate, ClaimCompleteDelegate>

@property (nonatomic,strong) NSNumber* amount;
@property (nonatomic,strong) NSString* merchantName;

@end
