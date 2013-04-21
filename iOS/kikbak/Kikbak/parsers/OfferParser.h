//
//  OfferParser.h
//  Kikbak
//
//  Created by Ian Barile on 4/6/13.
//  Copyright (c) 2013 Ian Barile. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface OfferParser : NSObject

-(void)parse:(NSDictionary*)dict;
-(void)resolveDiff;

@end
