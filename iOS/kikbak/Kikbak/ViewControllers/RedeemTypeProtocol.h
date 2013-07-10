//
//  RedeemTypeProtocol.h
//  Kikbak
//
//  Created by Ian Barile on 5/23/13.
//  Copyright (c) 2013 Ian Barile. All rights reserved.
//

#import <Foundation/Foundation.h>

@class Credit;
@class Gift;

@protocol RedeemTypeProtocol <NSObject>

@required
-(void)onRedeemGift:(Gift*)gift;
-(void)onRedeemCredit:(Credit*)credi;

@end
