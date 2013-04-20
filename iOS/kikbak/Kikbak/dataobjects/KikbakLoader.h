//
//  KikbakLoader.h
//  Kikbak
//
//  Created by Ian Barile on 4/17/13.
//  Copyright (c) 2013 Ian Barile. All rights reserved.
//

#import <Foundation/Foundation.h>

@class Kikbak;

@interface KikbakLoader : NSObject

+(Kikbak*)findKikbaktById:(NSNumber*)kikbakId;
+(NSArray*)getKikbaks;

@end
