//
//  AppDelegate.h
//  kikback
//
//  Created by Ian Barile on 9/20/12.
//  Copyright (c) 2012 Ian Barile. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <FacebookSDK/FacebookSDK.h>
#import <CoreData/CoreData.h>




@class FBUserInfo;
@class LoginViewController;
@class LocationManager;

@interface AppDelegate : UIResponder <UIApplicationDelegate>{
  UIImageView* splash;
  FBUserInfo* userInfo;
}

@property (strong, nonatomic) UIWindow *window;
@property (strong, nonatomic) FBSession *session;
@property (strong, nonatomic) FBUserInfo* userInfo;
@property (strong, nonatomic) LocationManager* locationMgr;
@property (strong, nonatomic) NSData* deviceToken;
@property (strong, nonatomic, readonly) NSManagedObjectModel *managedObjectModel;
@property (strong, nonatomic, readonly) NSManagedObjectContext *managedObjectContext;
@property (strong, nonatomic, readonly) NSPersistentStoreCoordinator *persistentStoreCoordinator;

@end
