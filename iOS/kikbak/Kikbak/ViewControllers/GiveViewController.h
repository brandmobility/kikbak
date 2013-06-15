//
//  ShareViewController.h
//  kikit
//
//  Created by Ian Barile on 3/15/13.
//  Copyright (c) 2013 Ian Barile. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "HPGrowingTextView.h"

@class Offer;

@interface GiveViewController : UIViewController<UIImagePickerControllerDelegate,
                                        UINavigationControllerDelegate, UIAlertViewDelegate,
                                        HPGrowingTextViewDelegate>{
    bool photoTaken;
    bool captionAdded;
}

@property(nonatomic, strong) Offer* offer;




@end
