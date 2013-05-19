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
+(NSString*)getDirectory:(ImageType)type;
@end

const char* GIVE_IMAGE_PATH = "give";
const char* MERCHANT_PATH = "merchant";
const char* FRIEND_PATH = "friend";
const char* IMAGE_PNG = "image.png";

@implementation ImagePersistor

+(void)persisttImage:(NSData*)imageData fileId:(NSNumber*)fileId imageType:(ImageType)type
{
    NSArray* paths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES);
    NSString* fullPath = [NSString stringWithFormat:@"%@/%@/%@",[paths objectAtIndex:0],[ImagePersistor getDirectory:type], fileId];
    if(![ImagePersistor fileExists:fullPath]){
        [ImagePersistor createDirectory:fullPath];
    }
    
    NSString* file = [NSString stringWithFormat:@"%@/%s", fullPath, IMAGE_PNG];
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

+(NSString*)imageFileExists:(NSNumber*)fileId imageType:(ImageType)type{
    NSArray* paths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES);
    NSString* filepath = [NSString stringWithFormat:@"%@/%@/%@/%s",[paths objectAtIndex:0],[ImagePersistor getDirectory:type], fileId,IMAGE_PNG];
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

+(NSString*)getDirectory:(ImageType)type{
    if(type == MERCHANT_IMAGE_TYPE){
        return @(MERCHANT_PATH);
    }
    else if(type == GIVE_IMAGE_TYPE){
        return @(GIVE_IMAGE_PATH);
    }

    return @(FRIEND_PATH);
}

@end
