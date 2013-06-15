//
//  UIButton+Util.m
//  Kikbak
//
//  Created by Ian Barile on 6/12/13.
//  Copyright (c) 2013 Ian Barile. All rights reserved.
//

#import "UIButton+Util.h"

@implementation UIButton (Util)

+(UIBarButtonItem*)blackBackBtn:(id)target{
 
    UIImage *backImage = [UIImage imageNamed:@"btn_back.png"];
    UIButton *backButton = [UIButton buttonWithType:UIButtonTypeCustom];
    backButton.frame = CGRectMake(0, 0, backImage.size.width, backImage.size.height);
    
    [backButton setBackgroundImage:backImage forState:UIControlStateNormal];
    [backButton setBackgroundImage:backImage forState:UIControlStateHighlighted];
    [backButton setTitle:NSLocalizedString(@"Back",nil) forState:UIControlStateNormal];
    backButton.titleLabel.font = [UIFont boldSystemFontOfSize:12];
    backButton.titleEdgeInsets = UIEdgeInsetsMake(0, 6, 0, 0);
    backButton.contentHorizontalAlignment = UIControlContentHorizontalAlignmentCenter;
    [backButton addTarget:target action:@selector(onBackBtn:)    forControlEvents:UIControlEventTouchUpInside];
    UIBarButtonItem *backBarButtonItem = [[UIBarButtonItem alloc] initWithCustomView:backButton];
    backBarButtonItem.style = UIBarButtonItemStylePlain;
    return  backBarButtonItem;
}

@end
