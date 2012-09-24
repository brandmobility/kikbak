//
//  TestViewController.h
//  kikback
//
//  Created by Ian Barile on 9/20/12.
//  Copyright (c) 2012 Ian Barile. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface PostReviewViewController : UIViewController<UIActionSheetDelegate, UIImagePickerControllerDelegate, UINavigationControllerDelegate,UITextViewDelegate>


@property (strong, nonatomic) IBOutlet UIImageView* photo;
@property (strong, nonatomic) IBOutlet UITextView* descriptionText;
@property (strong, nonatomic) IBOutlet UIScrollView* scrollView;
@property (strong, nonatomic) IBOutlet UIButton* submit;

-(IBAction)takePhoto:(id)sender;
-(IBAction)submit:(id)sender;

@end
