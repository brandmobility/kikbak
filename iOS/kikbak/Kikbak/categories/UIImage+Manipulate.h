//
//  UIImage+Manipulate.h
//  Kikbak
//
//  Created by Ian Barile on 3/29/13.
//  Copyright (c) 2013 Ian Barile. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface UIImage (Manipulate)

+(UIImage*)changeColor:(UIImage*)image newColor:(UIColor*)color;
-(UIImage*)imageByScalingAndCroppingForSize:(CGSize)targetSize;
-(UIImage*)imageCropToRect:(CGRect)cropRect;

@end
