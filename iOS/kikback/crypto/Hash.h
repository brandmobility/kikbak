//
//  Hash.h
//  kikback
//
//  Created by Ian Barile on 10/6/12.
//  Copyright (c) 2012 Ian Barile. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface Hash : NSObject{
  
}

+(NSData*)sha256:(NSData*)data;
@end
