//
//  LocationTableViewCell.h
//  Kikbak
//
//  Created by Ian Barile on 8/19/13.
//  Copyright (c) 2013 Ian Barile. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface LocationTableViewCell : UITableViewCell

@property (nonatomic,strong) NSString* address;

-(void)manuallyLayoutSubviews;

@end
