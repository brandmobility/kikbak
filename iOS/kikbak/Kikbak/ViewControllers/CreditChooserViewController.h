//
//  CreditChooserViewController.h
//  Kikbak
//
//  Created by Ian Barile on 5/25/13.
//  Copyright (c) 2013 Ian Barile. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface CreditChooserViewController : UIViewController<UIPickerViewDelegate>

@property (nonatomic, strong) NSNumber* creditAvailable;
@end
