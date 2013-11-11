//
//  TwitterApi.m
//  Kikbak
//
//  Created by Ian Barile on 11/9/13.
//  Copyright (c) 2013 Ian Barile. All rights reserved.
//

#import "TwitterApi.h"
#import <Accounts/Accounts.h>
#import <Social/Social.h>
#import "NotificationContstants.h"

@interface TwitterApi()

@property (nonatomic,strong) ACAccountStore *accountStore;

-(void)getUsername:(void (^)(NSString* username))completion;
-(void)post:(NSString*)username withText:(NSString*)text;

@end

@implementation TwitterApi

- (id)init
{
    self = [super init];
    if (self) {
        self.accountStore = [[ACAccountStore alloc] init];
    }
    return self;
}

- (BOOL)userHasAccessToTwitter{
    return [SLComposeViewController isAvailableForServiceType:SLServiceTypeTwitter];
}

- (void)postToTimeline:(NSString*)post{

    [self getUsername:^(NSString* username){
        if( username != nil){
            [self post:username withText:post];
        }
        else{
            [[NSNotificationCenter defaultCenter]postNotificationName:kKikbakTwitterError object:nil];
        }
    }];
    
}

-(void)getUsername:(void (^)(NSString* username))completion{
    
    ACAccountStore *account = [[ACAccountStore alloc] init];
    ACAccountType *accountType = [account accountTypeWithAccountTypeIdentifier:
                                  ACAccountTypeIdentifierTwitter];
    
    [account requestAccessToAccountsWithType:accountType options:nil
                                  completion:^(BOOL granted, NSError *error)
     {
         if (granted == YES)
         {
             NSArray *arrayOfAccounts = [account
                                         accountsWithAccountType:accountType];
             
             if ([arrayOfAccounts count] > 0)
             {
                 ACAccount *twitterAccount = [arrayOfAccounts lastObject];
                 completion(twitterAccount.username);
             }
             else{
                 completion(nil);
             }

         }
         else{
             completion(nil);
         }
        
     }];

}

-(void)post:(NSString*)username withText:(NSString*)text{
    //  Step 0: Check that the user has local Twitter accounts
    if ([self userHasAccessToTwitter]) {
        
        //  Step 1:  Obtain access to the user's Twitter accounts
        ACAccountType *twitterAccountType =
        [self.accountStore accountTypeWithAccountTypeIdentifier:
         ACAccountTypeIdentifierTwitter];
        
        [self.accountStore
         requestAccessToAccountsWithType:twitterAccountType
         options:NULL
         completion:^(BOOL granted, NSError *error) {
             if (granted) {
                 //  Step 2:  Create a request
                 NSArray *twitterAccounts =
                 [self.accountStore accountsWithAccountType:twitterAccountType];
                 NSURL *url = [NSURL URLWithString:@"https://api.twitter.com"
                               @"/1.1/statuses/update.json"];
                 NSDictionary *params = @{@"status" : text};
                 SLRequest *request =
                 [SLRequest requestForServiceType:SLServiceTypeTwitter
                                    requestMethod:SLRequestMethodPOST
                                              URL:url
                                       parameters:params];
                 
                 //  Attach an account to the request
                 [request setAccount:[twitterAccounts lastObject]];
                 
                 //  Step 3:  Execute the request
                 [request performRequestWithHandler:
                  ^(NSData *responseData,
                    NSHTTPURLResponse *urlResponse,
                    NSError *error) {
                      
                      if (responseData) {
                          if (urlResponse.statusCode >= 200 &&
                              urlResponse.statusCode < 300) {
                              
                              NSError *jsonError;
                              NSDictionary *timelineData =
                              [NSJSONSerialization
                               JSONObjectWithData:responseData
                               options:NSJSONReadingAllowFragments error:&jsonError];
                              if (timelineData) {
//                                  NSLog(@"Timeline Response: %@\n", timelineData);
                                  [[NSNotificationCenter defaultCenter]postNotificationName:kKikbakTwitterSuccess object:nil];
                              }
                              else {
                                  // Our JSON deserialization went awry
//                                  NSLog(@"JSON Error: %@", [jsonError localizedDescription]);
                                  [[NSNotificationCenter defaultCenter]postNotificationName:kKikbakTwitterError object:nil];
                              }
                          }
                          else {
                              // The server did not respond ... were we rate-limited?
//                              NSLog(@"The response status code is %d", urlResponse.statusCode);
                              [[NSNotificationCenter defaultCenter]postNotificationName:kKikbakTwitterError object:nil];
                          }
                      }
                  }];
             }
             else {
                 // Access was not granted, or an error occurred
                 NSLog(@"%@", [error localizedDescription]);
             }
         }];
    }
}

@end
