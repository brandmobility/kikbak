//
//  FBUserInfo.h
//  kikback
//
//  Created by Ian Barile on 9/30/12.
//  Copyright (c) 2012 Ian Barile. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <FacebookSDK/FacebookSDK.h>

@interface FBUserInfo : NSObject{
  FBGraphObject* friends;
  FBGraphObject* me;
}

@property (nonatomic, strong) NSMutableDictionary* friends;
@property (nonatomic, strong) NSMutableDictionary* me;

@end
