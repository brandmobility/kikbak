//
//  AppDelegate.h
//  kikback
//
//  Created by Ian Barile on 9/20/12.
//  Copyright (c) 2012 Ian Barile. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <FacebookSDK/FacebookSDK.h>
#import <FacebookSDK/FBTestSession.h>

@class LoginViewController;

@interface AppDelegate : UIResponder <UIApplicationDelegate>{
  UIImageView* splash;
}

-(void)createFBSession;

@property (strong, nonatomic) UIWindow *window;
@property (strong, nonatomic) FBSession *session;

@end
