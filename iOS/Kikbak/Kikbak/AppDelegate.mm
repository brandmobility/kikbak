//
//  AppDelegate.m
//  kikback
//
//  Created by Ian Barile on 9/20/12.
//  Copyright (c) 2012 Ian Barile. All rights reserved.
//

#import "AppDelegate.h"
#import "Flurry.h"
#import "SBJson.h"
#import "FBQuery.h"
#import "FBUserInfo.h"
#import "LocationManager.h"
#import "KikbakConstants.h"
#import "DeviceTokenRequest.h"
#import "ServerTest.h"
#import "NSData+Base64.h"
#import "RootViewController.h"
#import "RewardRequest.h"



@interface AppDelegate()
-(void)fadeOutSplash;
@end

@implementation AppDelegate

@synthesize userInfo = _userInfo;
@synthesize managedObjectModel = _managedObjectModel;
@synthesize managedObjectContext = _managedObjectContext;
@synthesize persistentStoreCoordinator = _persistentStoreCoordinator;

- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions
{
    //init location manager first
    self.locationMgr = [[LocationManager alloc]init];
    
    //FLuury
    [Flurry startSession:@"3MFX5FS2Z6YBXBQS2F8P"];
  
    [self managedObjectContext];
  
    self.userInfo = [[FBUserInfo alloc]init];
    [FBQuery createFBSession];

    if( self.session.isOpen ){
        [FBQuery fbMe];
        UIStoryboard* mainBoard = [UIStoryboard storyboardWithName:@"MainStoryboard" bundle:nil];
        UIViewController* postView = [mainBoard instantiateViewControllerWithIdentifier:@"RootViewController"];
        self.window.rootViewController = postView;
    }
  
    [application registerForRemoteNotificationTypes:(UIRemoteNotificationTypeBadge|UIRemoteNotificationTypeSound|UIRemoteNotificationTypeAlert)];
  
    splash = [[UIImageView alloc]initWithImage:[UIImage imageNamed:@"Default.png"]];
    splash.frame = CGRectMake(0, 0, 320, 460);
    [self.window.rootViewController.view addSubview:splash];
    [self.window bringSubviewToFront:splash];
  
    NSUserDefaults* prefs = [NSUserDefaults standardUserDefaults];
    if( [prefs objectForKey:KIKBAK_USER_ID] != nil ){
        RewardRequest* request = [[RewardRequest alloc]init];
        [request restRequest:[[NSDictionary alloc]init]];
    }
    
    [self fadeOutSplash];
    return YES;
}
							
- (void)applicationWillResignActive:(UIApplication *)application
{
  // Sent when the application is about to move from active to inactive state. This can occur for certain types of temporary interruptions (such as an incoming phone call or SMS message) or when the user quits the application and it begins the transition to the background state.
  // Use this method to pause ongoing tasks, disable timers, and throttle down OpenGL ES frame rates. Games should use this method to pause the game.
}

- (void)applicationDidEnterBackground:(UIApplication *)application
{
    splash.alpha = 1;
}

- (void)applicationWillEnterForeground:(UIApplication *)application
{
    [self fadeOutSplash];
    NSUserDefaults* prefs = [NSUserDefaults standardUserDefaults];
    if( [prefs objectForKey:KIKBAK_USER_ID] == nil ){
        if( [self.session isOpen] ){
            [FBQuery fbMe];
        }
    }
    
    if( [prefs objectForKey:KIKBAK_USER_ID] != nil ){
        RewardRequest* request = [[RewardRequest alloc]init];
        [request restRequest:[[NSDictionary alloc]init]];
    }
}

- (void)fadeOutSplash
{
  [UIView beginAnimations:nil context:NULL];
  
  [UIView setAnimationCurve:UIViewAnimationCurveEaseIn];
  
  [UIView setAnimationDuration:0.2];
  
  [UIView setAnimationDelay:0.5]; //was 1.5
  
  splash.alpha = 0;
  
  [UIView commitAnimations];
}

- (void)applicationDidBecomeActive:(UIApplication *)application
{
  // this means the user switched back to this app without completing a login in Safari/Facebook App
  if (self.session.state == FBSessionStateCreatedOpening) {
    // BUG: for the iOS 6 preview we comment this line out to compensate for a race-condition in our
    // state transition handling for integrated Facebook Login; production code should close a
    // session in the opening state on transition back to the application; this line will again be
    // active in the next production rev
    //[self.session close]; // so we close our session and start over
  }
}

- (void)applicationWillTerminate:(UIApplication *)application
{
  
  [self.session close];
}


- (BOOL)application:(UIApplication *)application  openURL:(NSURL *)url sourceApplication:(NSString *)sourceApplication annotation:(id)annotation {
  // attempt to extract a token from the url
  
  [self.session handleOpenURL:url];  
  [FBSession setActiveSession:self.session];
  [FBQuery fbMe];
  return YES;
}



- (void)application:(UIApplication *)application didRegisterForRemoteNotificationsWithDeviceToken:(NSData *)deviceToken{
 
    NSUserDefaults* prefs = [NSUserDefaults standardUserDefaults];
    [prefs setValue:[deviceToken base64EncodedString] forKey:DEVICE_TOKEN_KEY];
    [prefs synchronize];
    if( [prefs objectForKey:KIKBAK_USER_ID] != nil ){
      DeviceTokenRequest* request = [[DeviceTokenRequest alloc]init];
      NSMutableDictionary* data = [[NSMutableDictionary alloc]initWithCapacity:2];
      [data setObject:[deviceToken base64EncodedString] forKey:@"token"];
      [data setObject:[NSNumber numberWithInt:0] forKey:@"platform_id"];
      [request restRequest:data];
    }
  
}

#pragma mark -
#pragma mark Core Data stack

/**
 Returns the managed object context for the application.
 If the context doesn't already exist, it is created and bound to the persistent store coordinator for the application.
 */
- (NSManagedObjectContext *)managedObjectContext {
	
    if (_managedObjectContext != nil) {
        return _managedObjectContext;
    }
	
    NSPersistentStoreCoordinator *coordinator = [self persistentStoreCoordinator];
    if (coordinator != nil) {
        _managedObjectContext = [NSManagedObjectContext new];
        [_managedObjectContext setPersistentStoreCoordinator: coordinator];
    }
    return _managedObjectContext;
}


/**
 Returns the managed object model for the application.
 If the model doesn't already exist, it is created by merging all of the models found in the application bundle.
 */
- (NSManagedObjectModel *)managedObjectModel {
	
    if (_managedObjectModel != nil) {
        return _managedObjectModel;
    }
    
    NSString *path = [[NSBundle mainBundle] pathForResource:@"kikbak" ofType:@"momd"];
    NSURL *momURL = [NSURL fileURLWithPath:path];
    _managedObjectModel = [[NSManagedObjectModel alloc] initWithContentsOfURL:momURL];

    return _managedObjectModel;
}


/**
 Returns the persistent store coordinator for the application.
 If the coordinator doesn't already exist, it is created and the application's store added to it.
 */
- (NSPersistentStoreCoordinator *)persistentStoreCoordinator {
	
    if (_persistentStoreCoordinator != nil) {
        return _persistentStoreCoordinator;
    }
    
	NSString *storePath = [[self applicationDocumentsDirectory] stringByAppendingPathComponent:@"kikbak.sqlite"];
	/*
	 Set up the store.
	 For the sake of illustration, provide a pre-populated default store.
	 */
	NSFileManager *fileManager = [NSFileManager defaultManager];
	// If the expected store doesn't exist, copy the default store.
	if (![fileManager fileExistsAtPath:storePath]) {
		NSString *defaultStorePath = [[NSBundle mainBundle] pathForResource:@"kikbak" ofType:@"sqlite"];
		if (defaultStorePath) {
			[fileManager copyItemAtPath:defaultStorePath toPath:storePath error:NULL];
		}
	}
	
	NSURL *storeUrl = [NSURL fileURLWithPath:storePath];
	
	NSError *error;
    _persistentStoreCoordinator = [[NSPersistentStoreCoordinator alloc] initWithManagedObjectModel: [self managedObjectModel]];
    if (![_persistentStoreCoordinator addPersistentStoreWithType:NSSQLiteStoreType configuration:nil URL:storeUrl options:nil error:&error]) {
		/*
		 Replace this implementation with code to handle the error appropriately.
		 
		 abort() causes the application to generate a crash log and terminate. You should not use this function in a shipping application, although it may be useful during development. If it is not possible to recover from the error, display an alert panel that instructs the user to quit the application by pressing the Home button.
		 
		 Typical reasons for an error here include:
		 * The persistent store is not accessible
		 * The schema for the persistent store is incompatible with current managed object model
		 Check the error message to determine what the actual problem was.
		 */
		NSLog(@"Unresolved error %@, %@", error, [error userInfo]);
		abort();
    }
    
    return _persistentStoreCoordinator;
}


#pragma mark -
#pragma mark Application's documents directory

/**
 Returns the path to the application's documents directory.
 */
- (NSString *)applicationDocumentsDirectory {
	return [NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES) lastObject];
}

@end
