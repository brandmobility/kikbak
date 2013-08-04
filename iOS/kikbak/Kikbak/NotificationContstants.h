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
KIKBAK_EXTERN NSString *const kKikbakImageDownloaded;
KIKBAK_EXTERN NSString *const kKikbakOffersDownloaded;
KIKBAK_EXTERN NSString *const kKikbakRewardUpdate;
KIKBAK_EXTERN NSString *const kKikbakRedeemGiftError;
KIKBAK_EXTERN NSString *const kKikbakRedeemGiftSuccess;
KIKBAK_EXTERN NSString *const kKikbakRedeemKikbakError;
KIKBAK_EXTERN NSString *const kKikbakRedeemKikbakSuccess;
KIKBAK_EXTERN NSString *const kKikbakShareSuccess;
KIKBAK_EXTERN NSString *const kKikbakShareError;
KIKBAK_EXTERN NSString *const kKikbakClaimSuccess;
KIKBAK_EXTERN NSString *const kKikbakClaimError;
KIKBAK_EXTERN NSString *const kKikbakBarcodeSuccess;
KIKBAK_EXTERN NSString *const kKikbakBarcodeError;
KIKBAK_EXTERN NSString *const kKikbakSMSBodySuccess;
KIKBAK_EXTERN NSString *const kKikbakSMSBodyError;
KIKBAK_EXTERN NSString *const kKikbakEmailBodySuccess;
KIKBAK_EXTERN NSString *const kKikbakEmailBodyError;
KIKBAK_EXTERN NSString *const kKikbakImagePostSuccess;
KIKBAK_EXTERN NSString *const kKikbakImagePostError;

