//
//  Hash.m
//  kikback
//
//  Created by Ian Barile on 10/6/12.
//  Copyright (c) 2012 Ian Barile. All rights reserved.
//

#import "Hash.h"
#import <CommonCrypto/CommonDigest.h>

@implementation Hash

+(NSData*)sha256:(NSData*)data{
  unsigned char hash[CC_SHA256_DIGEST_LENGTH];
  if ( CC_SHA1([data bytes], [data length], hash) ) {
    NSData *sha1 = [NSData dataWithBytes:hash length:CC_SHA256_DIGEST_LENGTH];
    return sha1;
  }
  return nil;
}

@end
