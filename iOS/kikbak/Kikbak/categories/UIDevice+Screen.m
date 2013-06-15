//
//  UIDevice+Screen.m
//  kikit
//
//  Created by Ian Barile on 3/12/13.
//  Copyright (c) 2013 Ian Barile. All rights reserved.
//

#import "UIDevice+Screen.h"

@implementation UIDevice (Screen)

+ (BOOL)hasFourInchDisplay {
    return ([[UIDevice currentDevice] userInterfaceIdiom] == UIUserInterfaceIdiomPhone && [UIScreen mainScreen].bounds.size.height == 568.0);
}


@end
