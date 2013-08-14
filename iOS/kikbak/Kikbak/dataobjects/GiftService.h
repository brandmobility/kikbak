//
//  GiftLoader.h
//  Kikbak
//
//  Created by Ian Barile on 4/11/13.
//  Copyright (c) 2013 Ian Barile. All rights reserved.
//

#import <Foundation/Foundation.h>


@class Gift;

@interface GiftService : NSObject

+(Gift*)findGiftByMerchantId:(NSNumber*)merchantId;
+(NSArray*)getGifts;
+(void)deleteAll;

@end
