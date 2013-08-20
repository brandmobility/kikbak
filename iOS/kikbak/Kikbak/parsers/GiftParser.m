//
//  GiftParser.m
//  Kikbak
//
//  Created by Ian Barile on 4/11/13.
//  Copyright (c) 2013 Ian Barile. All rights reserved.
//

#import "GiftParser.h"
#import "AppDelegate.h"
#import "Gift.h"
#import "Location.h"
#import "GiftService.h"
#import "LocationParser.h"
#import "FBQuery.h"
#import "ShareInfo.h"
#import "ImagePersistor.h"
#import "ImageDownloadRequest.h"

@interface GiftParser()

@property (nonatomic, strong) NSMutableDictionary* gifts;

@end

@implementation GiftParser

-(void)parse:(NSDictionary*)dict{
    NSManagedObjectContext* context = ((AppDelegate*)[UIApplication sharedApplication].delegate).managedObjectContext;
    
    id merchant = [dict objectForKey:@"merchant"];
    if( merchant == [NSNull null]){
        return;
    }
    
    Gift* gift = [GiftService findGiftByMerchantId:[merchant objectForKey:@"id"]];
    if( gift == nil){
        gift = [NSEntityDescription insertNewObjectForEntityForName:@"Gift" inManagedObjectContext:context];
        gift.merchantId = [merchant objectForKey:@"id"];
    }
        
    
    gift.merchantId = [merchant objectForKey:@"id"];
    gift.merchantName = [merchant objectForKey:@"name"];
    gift.merchantUrl = [merchant objectForKey:@"merchantUrl"];
    
    NSArray* locations = [merchant objectForKey:@"locations"];
    for(id giftLocation in locations){
        Location* loc = [[[LocationParser alloc]init] parse:giftLocation withContext:gift.managedObjectContext];
        [gift addLocationObject:loc];
    }
    
    gift.desc = [dict objectForKey:@"desc"];
    gift.detailedDesc = [dict objectForKey:@"detailedDesc"];
    gift.name = [dict objectForKey:@"name"];
    gift.discountType = [dict objectForKey:@"discountType"];
    gift.validationType = [dict objectForKey:@"validationType"];
    gift.redemptionLocationType = [dict objectForKey:@"redemptionLocationType"];
    gift.tosUrl = [dict objectForKey:@"tosUrl"];
    gift.defaultGiveImageUrl = [dict objectForKey:@"defaultGiveImageUrl"];
    gift.value = [dict objectForKey:@"value"];
    
    NSArray* shares = [dict objectForKey:@"shareInfo"];
    for( id shareInfo in shares){
        NSNumber* allocatedGiftId = [shareInfo objectForKey:@"allocatedGiftId"];
        ShareInfo* info = [GiftService findShareInfoByAllocatedGift:allocatedGiftId];
        if(info == nil){
            info = [NSEntityDescription insertNewObjectForEntityForName:@"ShareInfo" inManagedObjectContext:context];
        }
        info.allocatedGiftId = allocatedGiftId;
        info.friendUserId = [shareInfo objectForKey:@"friendUserId"];
        info.fbFriendId = [shareInfo objectForKey:@"fbFriendId"];
        info.friendName = [shareInfo objectForKey:@"friendName"];
        info.imageUrl = [shareInfo objectForKey:@"imageUrl"];
        info.caption = [shareInfo objectForKey:@"caption"];
        [gift addShareInfoObject:info];
        
        [FBQuery requestProfileImage:info.fbFriendId];
        
        if(![ImagePersistor imageFileExists:info.allocatedGiftId imageType:UGC_GIVE_IMAGE_TYPE]) {
            ImageDownloadRequest* request = [[ImageDownloadRequest alloc]init];
            request.url = info.imageUrl;
            request.fileId = info.allocatedGiftId ;
            request.type = UGC_GIVE_IMAGE_TYPE;
            [request requestImage];
        }
    }
    
    NSError *error = nil;
    if (![context save:&error]) {
        NSLog(@"Unresolved error %@, %@", error, [error userInfo]);
    }

    
    if( self.gifts == nil){
        self.gifts = [[NSMutableDictionary alloc]initWithCapacity:1];
    }
    [self.gifts setObject:gift forKey:gift.merchantId];
    
}


-(void)resolveDiff{
    NSArray* pGifts = [GiftService getGifts];
    NSMutableDictionary* pGiftDict = [[NSMutableDictionary alloc] initWithCapacity:[pGifts count]];
    for(Gift* gift in pGifts){
        [pGiftDict setObject:gift forKey:gift.merchantId];
    }
    
    for(NSNumber* key in [self.gifts allKeys]){
        [pGiftDict removeObjectForKey:key];
    }
    
    
    NSManagedObjectContext* context = ((AppDelegate*)[UIApplication sharedApplication].delegate).managedObjectContext;
    for(Gift* gift in [pGiftDict allValues]){
        [context deleteObject:gift];
    }
    
    NSError* error = nil;
    if([[context deletedObjects]count] > 0 && ![context save:&error]){
        NSLog(@"Unresolved error %@, %@", error, [error userInfo]);
    }
}

@end
