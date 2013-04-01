//
//  QRScannerViewController.m
//  kikit
//
//  Created by Ian Barile on 12/31/12.
//  Copyright (c) 2012 Ian Barile. All rights reserved.
//

#import "QRScannerViewController.h"
#import "QRCodeReader.h"

@interface QRScannerViewController ()

@end

@implementation QRScannerViewController

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
	// Do any additional setup after loading the view.
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

-(void)ScanPressed:(id)sender{
  ZXingWidgetController *widController = [[ZXingWidgetController alloc] initWithDelegate:self showCancel:YES OneDMode:NO];
  
  NSMutableSet *readers = [[NSMutableSet alloc ] init];
  
  QRCodeReader* qrcodeReader = [[QRCodeReader alloc] init];
  [readers addObject:qrcodeReader];

  
  widController.readers = readers;
  
  //NSBundle *mainBundle = [NSBundle mainBundle];
  //widController.soundToPlay =
 // [NSURL fileURLWithPath:[mainBundle pathForResource:@"beep-beep" ofType:@"aiff"] isDirectory:NO];
  [self presentModalViewController:widController animated:YES];
}


- (void)zxingController:(ZXingWidgetController*)controller didScanResult:(NSString *)result {

  [self dismissModalViewControllerAnimated:NO];
}

- (void)zxingControllerDidCancel:(ZXingWidgetController*)controller {
  [self dismissModalViewControllerAnimated:YES];
}


@end
