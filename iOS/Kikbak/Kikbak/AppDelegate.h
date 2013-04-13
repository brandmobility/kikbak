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


#ifdef __cplusplus
#define KIKBAK_EXTERN		extern "C" __attribute__((visibility ("default")))
#else
#define KIKBAK_EXTERN	        extern __attribute__((visibility ("default")))
#endif

KIKBAK_EXTERN NSString *const kKikbakLocationUpdate;
KIKBAK_EXTERN NSString *const kKikbakOfferUpdate;
KIKBAK_EXTERN NSString *const kKikbakGiftUpdate;

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
