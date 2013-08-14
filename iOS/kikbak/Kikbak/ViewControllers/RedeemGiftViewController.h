//
//  RedeemViewController.h
//  kikit
//
//  Created by Ian Barile on 3/9/13.
//  Copyright (c) 2013 Ian Barile. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <ZXingWidgetController.h>

@class Gift;
@class ShareInfo;

@interface RedeemGiftViewController : UIViewController<ZXingDelegate>

@property(nonatomic,strong) Gift* gift;
@property(nonatomic,strong) ShareInfo* shareInfo;


@end
