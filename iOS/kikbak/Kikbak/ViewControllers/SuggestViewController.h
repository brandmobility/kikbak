//
//  SuggestViewController.h
//  Kikbak
//
//  Created by Ian Barile on 6/12/13.
//  Copyright (c) 2013 Ian Barile. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <MessageUI/MessageUI.h>
#import <MessageUI/MFMailComposeViewController.h>

@interface SuggestViewController : UIViewController<UINavigationControllerDelegate,
                                                    UIImagePickerControllerDelegate,
                                                    UITextViewDelegate,
                                                    MFMailComposeViewControllerDelegate,
                                                    UIAlertViewDelegate>

@end
