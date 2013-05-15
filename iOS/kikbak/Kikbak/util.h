//
//  util.h
//  Kikbak
//
//  Created by Ian Barile on 5/13/13.
//  Copyright (c) 2013 Ian Barile. All rights reserved.
//

#ifndef Kikbak_util_h
#define Kikbak_util_h


#define UIColorFromRGB(rgbValue) [UIColor \
colorWithRed: ((float)((rgbValue & 0xFF0000) >> 16))/255.0 \
green:        ((float)((rgbValue & 0xFF00) >> 8))/255.0 \
blue:         ((float)(rgbValue & 0xFF))/255.0 alpha:1.0]

#define UIColorFromRGBWithOpacity(rgbValue, opacity) [UIColor \
colorWithRed: ((float)((rgbValue & 0xFF0000) >> 16))/255.0 \
green:        ((float)((rgbValue & 0xFF00) >> 8))/255.0 \
blue:         ((float)(rgbValue & 0xFF))/255.0 alpha:opacity]


#endif
