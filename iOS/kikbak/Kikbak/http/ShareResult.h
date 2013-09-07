//
//  ShareResult.h
//  Kikbak
//
//  Created by Ian Barile on 8/9/13.
//  Copyright (c) 2013 Ian Barile. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface ShareResult : NSObject

@property (nonatomic,strong) NSString* landingUrl;
@property (nonatomic,strong) NSString* subject;
@property (nonatomic,strong) NSString* body;

@end
