//
//  ShareViewController.h
//  kikit
//
//  Created by Ian Barile on 3/15/13.
//  Copyright (c) 2013 Ian Barile. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "HPGrowingTextView.h"
#import "ShareSuccessView.h"
#import "ShareChannelSelectorView.h"
#import <MessageUI/MessageUI.h>
#import <MessageUI/MFMailComposeViewController.h>

@class Offer;

@interface GiveViewController : UIViewController<UIImagePickerControllerDelegate,
                                        UINavigationControllerDelegate,
                                        UIAlertViewDelegate,
                                        HPGrowingTextViewDelegate,
                                        ShareCompleteDelegate,
                                        ChannelSelectorDelegate,
                                        MFMailComposeViewControllerDelegate>{
}

@property(nonatomic, strong) Offer* offer;




@end
