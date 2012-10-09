//
//  AppDelegate.m
//  kikback
//
//  Created by Ian Barile on 9/20/12.
//  Copyright (c) 2012 Ian Barile. All rights reserved.
//

#import "AppDelegate.h"
#import "Flurry.h"
#import "Parse/Parse.h"
#import "SBJson.h"
#import "FBQuery.h"
#import "FBUserInfo.h"

@interface AppDelegate()
-(void)fadeOutSplash;
@end

@implementation AppDelegate

@synthesize window = _window;
@synthesize session = _session;
@synthesize userInfo = _userInfo;

- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions
{
    //FLuury
    [Flurry startSession:@"3MFX5FS2Z6YBXBQS2F8P"];
  
    //parse server setup
    [Parse setApplicationId:@"HKX3h3Xgt6FXTX9ndG0SySwqF5O2ShWbhxBXuiaE" clientKey:@"Suat5isMgkTCzhKMWurHtXbJza9uTYYZCecDxoqD"];
    [PFUser enableAutomaticUser];
    PFACL *defaultACL = [PFACL ACL];
    // If you would like all objects to be private by default, remove this line.
    //[defaultACL setPublicReadAccess:YES];
    [PFACL setDefaultACL:defaultACL withAccessForCurrentUser:YES];
  
    self.userInfo = [[FBUserInfo alloc]init];
    [FBQuery createFBSession];
    if( self.session ){
        [FBQuery fbMe];
    }
  
    if( self.session.isOpen ){

        UIStoryboard* mainBoard = [UIStoryboard storyboardWithName:@"MainStoryboard" bundle:nil];
        UIViewController* postView = [mainBoard instantiateViewControllerWithIdentifier:@"PostReviewViewController"];
        self.window.rootViewController = postView;
    }
  
    splash = [[UIImageView alloc]initWithImage:[UIImage imageNamed:@"Default.png"]];
    splash.frame = CGRectMake(0, 0, 320, 460);
    [self.window.rootViewController.view addSubview:splash];
    [self.window bringSubviewToFront:splash];
  
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
}

- (void)fadeOutSplash
{
  [UIView beginAnimations:nil context:NULL];
  
  [UIView setAnimationCurve:UIViewAnimationCurveEaseIn];
  
  [UIView setAnimationDuration:0.3];
  
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




@end
