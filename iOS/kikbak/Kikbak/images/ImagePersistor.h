//
//  ImagePersistor.h
//  Kikbak
//
//  Created by Ian Barile on 4/9/13.
//  Copyright (c) 2013 Ian Barile. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "KikbakConstants.h"

@interface ImagePersistor : NSObject

+(NSString*)persisttImage:(NSData*)imageData fileId:(NSNumber*)fileId imageType:(ImageType)type;
+(NSString*)imageFileExists:(NSNumber*)fileId imageType:(ImageType)type;

@end
