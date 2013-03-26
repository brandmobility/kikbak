//
//  ShareViewController.h
//  kikit
//
//  Created by Ian Barile on 3/15/13.
//  Copyright (c) 2013 Ian Barile. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface ShareViewController : UIViewController<UIActionSheetDelegate, UIImagePickerControllerDelegate, UINavigationControllerDelegate>{
    bool pictureTaken;
}

@property(nonatomic, strong) IBOutlet UIImageView* retailerImage;
@property(nonatomic, strong) IBOutlet UILabel* retailer;
@property(nonatomic, strong) IBOutlet UIImageView* mapMarker;
@property(nonatomic, strong) IBOutlet UILabel* distance;
@property(nonatomic, strong) IBOutlet UIImageView* topGradient;
@property(nonatomic, strong) IBOutlet UILabel* giftLabel;
@property(nonatomic, strong) IBOutlet UIImageView* giveArrow;
@property(nonatomic, strong) IBOutlet UILabel* giveText;
@property(nonatomic, strong) IBOutlet UILabel* getLabel;
@property(nonatomic, strong) IBOutlet UIImageView* getArrow;
@property(nonatomic, strong) IBOutlet UILabel* getText;
@property(nonatomic, strong) IBOutlet UIImageView* photoFrame;
@property(nonatomic, strong) IBOutlet UIImageView* giftImage;
@property(nonatomic, strong) IBOutlet UIButton* termsBtn;
@property(nonatomic, strong) IBOutlet UIButton* redeemBtn;
@property(nonatomic, strong) IBOutlet UIView* locationView;

-(IBAction)onGiveGift:(id)sender;
-(IBAction)onTermsAndConditions:(id)sender;

@end
