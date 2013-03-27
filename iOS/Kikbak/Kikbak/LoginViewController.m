//
//  ViewController.m
//  kikback
//
//  Created by Ian Barile on 9/20/12.
//  Copyright (c) 2012 Ian Barile. All rights reserved.
//

#import "LoginViewController.h"
#import "AppDelegate.h"
#import "Flurry.h"
#import "FBQuery.h"

@interface LoginViewController ()

@property (strong, nonatomic) IBOutlet UIButton *buttonLoginLogout;

- (void)updateView;
@end



@implementation LoginViewController

- (void)viewDidLoad
{
    [super viewDidLoad];
    [self updateView];
  
    AppDelegate *appDelegate = [[UIApplication sharedApplication]delegate];
    if (!appDelegate.session.isOpen) {
      // create a fresh session object
      appDelegate.session = [[FBSession alloc] initWithPermissions:[FBQuery FBPermissions]];
      
      // if we don't have a cached token, a call to open here would cause UX for login to
      // occur; we don't want that to happen unless the user clicks the login button, and so
      // we check here to make sure we have a token before calling open
      if (appDelegate.session.state == FBSessionStateCreatedTokenLoaded) {
        // even though we had a cached token, we need to login to make the session usable
        [appDelegate.session openWithCompletionHandler:^(FBSession *session,
                                                         FBSessionState status,
                                                         NSError *error) {
          // we recurse here, in order to update buttons and labels
          [self updateView];
        }];
      }
    }
}



-(void)viewWillAppear:(BOOL)animated{
  [super viewWillAppear:animated];
  
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

-(IBAction)onFaceBookLogin:(id)sender{
  
    AppDelegate *appDelegate = [[UIApplication sharedApplication]delegate];
    
    [Flurry logEvent:@"FaceBookLoginEvent" timed:YES];
    if (appDelegate.session.state != FBSessionStateCreated) {
      // Create a new, logged out session.
        appDelegate.session = [[FBSession alloc] initWithPermissions:[FBQuery FBPermissions]];
    }
    
    // if the session isn't open, let's open it now and present the login UX to the user
    [appDelegate.session openWithCompletionHandler:^(FBSession *session,
                                                     FBSessionState status,
                                                     NSError *error) {
        // and here we make sure to update our UX according to the new session state
        [self updateView];
    }];
}

// FBSample logic
// main helper method to update the UI to reflect the current state of the session.
- (void)updateView {
  // get the app delegate, so that we can reference the session property
  AppDelegate *appDelegate = [[UIApplication sharedApplication]delegate];
  if (appDelegate.session.isOpen) {
    // valid account UI is shown whenever the session is open
    UIStoryboard* mainBoard = [UIStoryboard storyboardWithName:@"MainStoryboard" bundle:nil];
    UIViewController* postView = [mainBoard instantiateViewControllerWithIdentifier:@"PostReviewViewController"];
    appDelegate.window.rootViewController = postView;
  } 
}

@end
