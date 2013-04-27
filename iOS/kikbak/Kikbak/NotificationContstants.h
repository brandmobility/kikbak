//
//  NotificationContstants.h
//  Kikbak
//
//  Created by Ian Barile on 4/25/13.
//  Copyright (c) 2013 Ian Barile. All rights reserved.
//

#import <Foundation/Foundation.h>

#ifdef __cplusplus
#define KIKBAK_EXTERN		extern "C" __attribute__((visibility ("default")))
#else
#define KIKBAK_EXTERN	        extern __attribute__((visibility ("default")))
#endif

KIKBAK_EXTERN NSString *const kKikbakLocationUpdate;
KIKBAK_EXTERN NSString *const kKikbakOfferUpdate;
KIKBAK_EXTERN NSString *const kKikbakRewardUpdate;
KIKBAK_EXTERN NSString *const kKikbakRedeemGiftError;
KIKBAK_EXTERN NSString *const kKikbakRedeemGiftSuccess;
KIKBAK_EXTERN NSString *const kKikbakRedeemKikbakError;
KIKBAK_EXTERN NSString *const kKikbakRedeemKikbakSuccess;
