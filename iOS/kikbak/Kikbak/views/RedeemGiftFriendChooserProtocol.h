//
//  RedeemGiftFriendChooserProtocol.h
//  Kikbak
//
//  Created by Ian Barile on 8/18/13.
//  Copyright (c) 2013 Ian Barile. All rights reserved.
//

#import <Foundation/Foundation.h>

@class Gift;
@class ShareInfo;

@protocol RedeemGiftFriendChooserProtocol <NSObject>

-(void)onRedeemGift:(Gift*)gift withShareInfo:(ShareInfo*)shareInfo;

@end
