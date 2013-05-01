//
//  FBQuery.h
//  kikback
//
//  Created by Ian Barile on 9/30/12.
//  Copyright (c) 2012 Ian Barile. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface FBQuery : NSObject

+(void)createFBSession;
+(void)fbFriends;
+(void)fbMe;
+(void)resolveImageUrl:(NSNumber*)fbPictureId;
+(NSArray*)FBPermissions;

@end
