//
//  RedeemListViewController.h
//  Kikbak
//
//  Created by Ian Barile on 3/27/13.
//  Copyright (c) 2013 Ian Barile. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "RedeemTypeProtocol.h"
#import "RedeemGiftFriendChooserProtocol.h"


@interface RedeemListViewController : UIViewController<UITableViewDataSource,
                                                        UITableViewDelegate,
                                                        RedeemTypeProtocol,
                                                        RedeemGiftFriendChooserProtocol>


@end
