//
//  RedeemTypeProtocol.h
//  Kikbak
//
//  Created by Ian Barile on 5/23/13.
//  Copyright (c) 2013 Ian Barile. All rights reserved.
//

#import <Foundation/Foundation.h>

@class Kikbak;
@class Gift;

@protocol RedeemTypeProtocol <NSObject>

@required
-(void)onRedeemGift:(Gift*)gift;
-(void)onRedeemCredit:(Kikbak*)credi;

@end
