//
//  LocationParser.h
//  Kikbak
//
//  Created by Ian Barile on 4/6/13.
//  Copyright (c) 2013 Ian Barile. All rights reserved.
//

#import <Foundation/Foundation.h>

@class Location;

@interface LocationParser : NSObject

+(Location*)parse:(NSDictionary*)dict withContext:(NSManagedObjectContext*)context;

@end
