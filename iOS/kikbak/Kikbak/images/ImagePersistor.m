//
//  ImagePersistor.m
//  Kikbak
//
//  Created by Ian Barile on 4/9/13.
//  Copyright (c) 2013 Ian Barile. All rights reserved.
//

#import "ImagePersistor.h"
#include <sys/xattr.h>

@interface ImagePersistor()
+(void)setBackUpAttribute:(NSString*)directory;
+(void)createDirectory:(NSString*)path;
+(bool)fileExists:(NSString*)path;
@end

const char* MERCHANT_PATH = "merchant";
const char* MERCHANT_PNG = "merchant.png";

@implementation ImagePersistor

+(void)persistMerchantImage:(NSData*)imageData forMerchantId:(NSNumber*)merchantId
{
    NSArray* paths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES);
    NSString* fullPath = [NSString stringWithFormat:@"%@/%s/%@",[paths objectAtIndex:0],MERCHANT_PATH, merchantId];
    if(![ImagePersistor fileExists:fullPath]){
        [ImagePersistor createDirectory:fullPath];
    }
    
    NSString* file = [NSString stringWithFormat:@"%@/%s", fullPath, MERCHANT_PNG];
    [imageData writeToFile:file atomically:YES];
}

+(void)setBackUpAttribute:(NSString*)directory{
    
    BOOL isDir = NO;
    if ([[NSFileManager defaultManager] fileExistsAtPath:directory isDirectory:&isDir] && isDir)
    {
        NSDirectoryEnumerator *directoryEnumerator = [[NSFileManager defaultManager] enumeratorAtPath:directory];
        for (NSString *fileName in directoryEnumerator) {
            NSString* fullFileName = [NSString stringWithFormat:@"%@/%@", directory, fileName];
            const char* attrName = "com.apple.MobileBackup";
            u_int8_t attrValue = 1;
            if (!setxattr([fullFileName fileSystemRepresentation], attrName, &attrValue, sizeof(attrValue), 0, 0))
            {
                NSLog( @"succeeded to set '%@'",  fullFileName);
            }
        }
    }
}

+(NSString*)merchantImageFileExists:(NSNumber*)merchantId{
    NSArray* paths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES);
    NSString* filepath = [NSString stringWithFormat:@"%@/%s/%@/%s",[paths objectAtIndex:0],MERCHANT_PATH, merchantId,MERCHANT_PNG];
    if( [ImagePersistor fileExists:filepath] ){
        return filepath;
    }
    
    return nil;
}

+(bool)fileExists:(NSString*)path{
    return [[NSFileManager defaultManager] fileExistsAtPath:path];
}

+(void)createDirectory:(NSString*)path{
    NSError* error;
    if (![[NSFileManager defaultManager] createDirectoryAtPath:path
                                   withIntermediateDirectories:YES
                                                    attributes:nil
                                                         error:&error])
    {
        NSLog(@"Create directory error: %@", error);
    }
}



@end
