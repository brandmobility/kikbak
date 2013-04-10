//
//  ImagePersistor.h
//  Kikbak
//
//  Created by Ian Barile on 4/9/13.
//  Copyright (c) 2013 Ian Barile. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface ImagePersistor : NSObject

+(void)persistMerchantImage:(NSData*)imageData forMerchantId:(NSNumber*)merchantId;
+(NSString*)merchantImageFileExists:(NSNumber*)merchantId;

@end
