//
//  FacebookSerialization.m
//  kikback
//
//  Created by Ian Barile on 10/1/12.
//  Copyright (c) 2012 Ian Barile. All rights reserved.
//

#import "FacebookSerialization.h"
#import "Parse/Parse.h"
#import "FBUserInfo.h"
#import "AppDelegate.h"
#import "FBConstants.h"
#import "Hash.h"
#import "ParseConstants.h"



@interface FacebookSerialization()

+(BOOL)shouldUpdateFBFriends:(id)friends;
-(void)deleteFBFriends:(id)friends;
-(void)saveFBFriends:(id)friends;

@end

@implementation FacebookSerialization

+(void)persistMe{
  
  AppDelegate* delegate = [UIApplication sharedApplication].delegate;
  PFQuery* query = [[PFQuery alloc]initWithClassName:FB_USER_CLASSNAME];
  [query whereKey:FBUSERID_KEY equalTo:[delegate.userInfo.me objectForKey:FBUSERID_KEY]];
  [query findObjectsInBackgroundWithBlock:^(NSArray *objects, NSError *error) {
    if (!error) {
      // The find succeeded.
      if( objects.count == 0){
        PFObject *me = [PFObject objectWithClassName:FB_USER_CLASSNAME];
        for( id key in delegate.userInfo.me){
          [me setObject:[delegate.userInfo.me objectForKey:key] forKey:key];
        }
        [me save];
      }
    } else {
      // Log details of the failure
      NSLog(@"Me Error: %@ %@", error, [error userInfo]);
    }
  }];
}

+(void)persistFriends{
  
  AppDelegate* delegate = [UIApplication sharedApplication].delegate;
  id friends = [delegate.userInfo.friends objectForKey:FB_FRIEND_ARRAY_KEY];
  if( friends){
    NSUserDefaults* prefs = [NSUserDefaults standardUserDefaults];
    NSString* userId = [prefs objectForKey:FBUSERID_KEY];
    
    PFQuery* query  = [[PFQuery alloc]initWithClassName:FB_FRIEND_CLASSNAME];
    query.limit = 1000;
    [query whereKey:FB_USER_ID equalTo:userId];
    [query findObjectsInBackgroundWithBlock:^(NSArray *objects, NSError *error) {
      if (!error) {
        // The find succeeded.
        FacebookSerialization* serializer = [[FacebookSerialization alloc]init];
        if( objects.count != 0){
          //delete all old friends
          [serializer performSelectorInBackground:@selector(deleteFBFriends:) withObject:objects];
        }

        [serializer performSelectorInBackground:@selector(saveFBFriends:) withObject:friends];
        
      } else {
        // Log details of the failure
        NSLog(@"Friend Error: %@ %@", error, [error userInfo]);
      }
    }];
  }
}

//need to research why this doesn't work
+(BOOL)shouldUpdateFBFriends:(id)friends{

  NSUserDefaults* prefs = [NSUserDefaults standardUserDefaults];
  NSString* strFriends = [NSString stringWithFormat:@"%@",friends];
  NSData* data = [NSData dataWithBytes:[strFriends UTF8String] length:[strFriends length]];
  NSData* storedHash = [prefs dataForKey:FBFRIEND_HASH_KEY];
  
  if (storedHash == nil || [data isEqualToData:storedHash]) {
    [prefs setObject:data forKey:FBFRIEND_HASH_KEY];
    [prefs synchronize];
    
    NSString* userId = [prefs objectForKey:FBUSERID_KEY];
    PFQuery* query = [[PFQuery alloc]initWithClassName:FB_FRIEND_HASH_CLASSHNAME];
    [query whereKey:FBUSERID_KEY equalTo:userId];
    [query findObjectsInBackgroundWithBlock:^(NSArray *objects, NSError *error) {
      if (!error) {
        // The find succeeded.
        if( objects.count == 0){
          PFObject *friendHash = [PFObject objectWithClassName:FB_FRIEND_HASH_CLASSHNAME];
          [friendHash setObject:data forKey:FBFRIEND_HASH_KEY];
          [friendHash setObject:userId forKey:FBUSERID_KEY];
          [friendHash save];
        }
      } else {
        // Log details of the failure
        NSLog(@"Error: %@ %@", error, [error userInfo]);
      }
    }];

    return YES;
  }
  
  return NO;
}

-(void)deleteFBFriends:(id)friends{
  NSLog(@"Start delete");
  int count = 0;
  for(id friend in friends){
    NSError* error;
    [((PFObject*)friend) delete:&error ];
    if (error) {
      NSLog(@"Delete Error: %@ %@", error, [error userInfo]);
    }
    count++;
  }
  
  NSLog(@"End delete, item count: %d", count);
}

-(void)saveFBFriends:(id)friends{
  NSLog(@"Start Save");
  int count = 0;
  NSUserDefaults* prefs = [NSUserDefaults standardUserDefaults];
  NSString* userId = [prefs objectForKey:FBUSERID_KEY];
  for(id friend in friends){
    PFObject *parseFriend = [PFObject objectWithClassName:FB_FRIEND_CLASSNAME];
    for( id key in friend){
      [parseFriend setObject:[friend objectForKey:key] forKey:key];
    }
    [parseFriend setObject:userId forKey:FB_USER_ID];
    NSError* error;
    [parseFriend save:&error];
    if (error) {
        NSLog(@"Sace Error: %@ %@", error, [error userInfo]);
      }
    count++;
  }
  NSLog(@"End Save, item count: %d", count);
}

@end
