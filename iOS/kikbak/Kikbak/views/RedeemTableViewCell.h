//
//  ReedemTableViewCell.h
//  Kikbak
//
//  Created by Ian Barile on 4/12/13.
//  Copyright (c) 2013 Ian Barile. All rights reserved.
//

#import <Foundation/Foundation.h>

@class Gift;

@interface RedeemTableViewCell : UITableViewCell

@property(strong, nonatomic)Gift* gift;

-(void)setup;

@end
