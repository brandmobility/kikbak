//
//  ShareViewController.h
//  kikit
//
//  Created by Ian Barile on 3/15/13.
//  Copyright (c) 2013 Ian Barile. All rights reserved.
//

#import <UIKit/UIKit.h>

@class Offer;

@interface GiveViewController : UIViewController<UIActionSheetDelegate, UIImagePickerControllerDelegate,
                                        UINavigationControllerDelegate, UIAlertViewDelegate>{
    bool photoTaken;
    bool captionAdded;
}

@property(nonatomic, strong) Offer* offer;

@property(nonatomic, strong) UIView* merchantBackground;
@property(nonatomic, strong) UIImageView* retailerLogo;
@property(nonatomic, strong) UILabel* retailerName;
@property(nonatomic, strong) UIImageView* mapMarker;
@property(nonatomic, strong) UILabel* distance;
@property(nonatomic, strong) UIImageView* callIcon;
@property(nonatomic, strong) UILabel* call;
@property(nonatomic, strong) UIImageView* webIcon;
@property(nonatomic, strong) UILabel* web;


@property(nonatomic, strong) UIImageView* giveImage;
@property(nonatomic, strong) UIView* imageOverlay;
@property(nonatomic, strong) UIButton* takePictureBtn;

@property(nonatomic, strong) UIView* captionBackground;
@property(nonatomic, strong) UITextView* captionTextView;

@property(nonatomic, strong) UIView* dealBackground;
@property(nonatomic, strong) UILabel* giveLabel;
@property(nonatomic, strong) UIImageView* seperator;
@property(nonatomic, strong) UILabel* getLabel;
@property(nonatomic, strong) UIButton* termsBtn;
@property(nonatomic, strong) UIButton* learnBtn;

@property(nonatomic, strong) UIButton* giveBtn;


@end
