//
//  KikbakLoader.h
//  Kikbak
//
//  Created by Ian Barile on 4/17/13.
//  Copyright (c) 2013 Ian Barile. All rights reserved.
//

#import <Foundation/Foundation.h>

@class Credit;

@interface CreditService : NSObject

+(Credit*)findKikbaktById:(NSNumber*)creditId;
+(NSArray*)getCredits;
+(void)deleteAll;

@end
