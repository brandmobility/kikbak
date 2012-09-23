//
//  AppDelegate.m
//  kikback
//
//  Created by Ian Barile on 9/20/12.
//  Copyright (c) 2012 Ian Barile. All rights reserved.
//

#import "AppDelegate.h"

@interface AppDelegate()
-(void)fadeOutSplash;
@end

@implementation AppDelegate

@synthesize window = _window;
@synthesize session = _session;

- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions
{
    // Override point for customization after application launch.
    [self createFBSession];
  
  NSLog(@"access token: %@", self.session.accessToken);
  NSLog(@"session: %@", self.session);
    if( self.session.isOpen ){

        UIStoryboard* mainBoard = [UIStoryboard storyboardWithName:@"MainStoryboard" bundle:nil];
        UIViewController* postView = [mainBoard instantiateViewControllerWithIdentifier:@"PostReviewViewController"];
        self.window.rootViewController = postView;
    }
  
    splash = [[UIImageView alloc]initWithImage:[UIImage imageNamed:@"morning_320x480"]];
    splash.frame = self.window.frame;
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
  return [self.session handleOpenURL:url];
}


-(void)createFBSession{
  if (!self.session.isOpen) {
//    NSArray* permissions = [[NSArray alloc]initWithObjects:@"email",@"publish_actions",@"publish_stream",@"publish_checkins",nil];
    NSArray* permissions = [[NSArray alloc]initWithObjects:@"publish_actions",nil];
    
    // if we don't have a cached token, a call to open here would cause UX for login to
    // occur; we don't want that to happen unless the user clicks the login button, and so
    // we check here to make sure we have a token before calling open
    //if (self.session.state == FBSessionStateCreatedTokenLoaded) {
      // even though we had a cached token, we need to login to make the session usable
//    [self.session openWithCompletionHandler:^(FBSession *session,
//                                              FBSessionState status,
//                                              NSError *error) {

      
    bool result = [FBSession openActiveSessionWithPermissions:permissions
                                   allowLoginUI:NO
                              completionHandler:^(FBSession *session,
                                                  FBSessionState state,
                                                  NSError *error) {
        NSLog(@"State: %d", state);
        NSLog(@"Error: %@", error);
                                self.session = [FBSession activeSession];
      }];
    //}
  }

}

@end
