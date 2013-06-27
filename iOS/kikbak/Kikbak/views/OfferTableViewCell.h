//
//  RedeemTableViewCell.h
//  Kikbak
//
//  Created by Ian Barile on 3/27/13.
//  Copyright (c) 2013 Ian Barile. All rights reserved.
//

#import <UIKit/UIKit.h>

@class Offer;

@interface OfferTableViewCell : UITableViewCell


@property(strong, nonatomic)Offer* offer;

-(void)setup:(int)index;

@end
