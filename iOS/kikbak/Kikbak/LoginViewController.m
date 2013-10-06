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
#import "UIDevice+OSVersion.h"
#import "UIDevice+Screen.h"

@interface LoginViewController ()

@property (strong, nonatomic) IBOutlet UIButton *buttonLoginLogout;

@property (nonatomic,strong) UIImageView* splash;
@property (nonatomic,strong) UIButton* fbLoginBtn;

-(void)createSubviews;
-(void)manuallyLayoutSubviews;

-(IBAction)onFaceBookLogin:(id)sender;

@end



@implementation LoginViewController

- (void)viewDidLoad
{
    [super viewDidLoad];
    [self createSubviews];
    [self manuallyLayoutSubviews];
  
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

-(void)createSubviews{
    self.splash = [[UIImageView alloc]initWithFrame:self.view.bounds];
    self.splash.image = [UIImage imageNamed:@"splash-586h"];
    [self.view addSubview:self.splash];
    
    self.fbLoginBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    self.fbLoginBtn.frame = CGRectMake(11, 489, 298, 40);
    [self.fbLoginBtn addTarget:self action:@selector(onFaceBookLogin:) forControlEvents:UIControlEventTouchUpInside];
    [self.fbLoginBtn setBackgroundImage:[UIImage imageNamed:@"btn_facebook"] forState:UIControlStateNormal];
    [self.fbLoginBtn setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
    [self.fbLoginBtn setTitle:NSLocalizedString(@"FB Login", nil) forState:UIControlStateNormal];
    self.fbLoginBtn.titleLabel.font = [UIFont fontWithName:@"HelveticaNeue-Bold" size:15];
    [self.view addSubview:self.fbLoginBtn];
}

-(void)manuallyLayoutSubviews{
    if(![UIDevice hasFourInchDisplay]){
        self.splash.image = [UIImage imageNamed:@"splash"];
        self.fbLoginBtn.frame = CGRectMake(11, 403, 298, 40);
    }
    
    if( [UIDevice osVersion7orGreater]){
        CGRect fr = self.fbLoginBtn.frame;
        fr.origin.y += [UIApplication sharedApplication].statusBarFrame.size.height;
        self.fbLoginBtn.frame = fr;
    }
}

#pragma mark - BTN actions
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
        if( !error && status == FBSessionStateOpen ){
            [FBSession setActiveSession:appDelegate.session];
            [FBQuery fbMe];
        }
    }];
}


@end
