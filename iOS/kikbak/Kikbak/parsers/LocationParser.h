//
//  LocationParser.h
//  Kikbak
//
//  Created by Ian Barile on 4/6/13.
//  Copyright (c) 2013 Ian Barile. All rights reserved.
//

#import <Foundation/Foundation.h>

@class Offer;
@class Gift;

@interface LocationParser : NSObject

+(void)parse:(NSDictionary*)dict forOffer:(Offer*) offer;
+(void)parse:(NSDictionary*)dict forGift:(Gift*) gift;

@end
