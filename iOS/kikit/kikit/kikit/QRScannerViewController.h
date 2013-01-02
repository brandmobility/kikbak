//
//  QRScannerViewController.h
//  kikit
//
//  Created by Ian Barile on 12/31/12.
//  Copyright (c) 2012 Ian Barile. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <ZXingWidgetController.h>

@interface QRScannerViewController : UIViewController<ZXingDelegate>
{
  
}

-(IBAction)ScanPressed:(id)sender;

@end
