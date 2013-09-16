//
//  UIImage+Manipulate.m
//  Kikbak
//
//  Created by Ian Barile on 3/29/13.
//  Copyright (c) 2013 Ian Barile. All rights reserved.
//

#import "UIImage+Manipulate.h"

@implementation UIImage (Manipulate)

-(UIImage*)imageCropToRect:(CGRect)cropRect{
    CGImageRef cropImage = CGImageCreateWithImageInRect([self CGImage], cropRect);
    UIImage* cropped = [UIImage imageWithCGImage:cropImage];
    CGImageRelease(cropImage);
    return cropped;
}

- (UIImage*)imageByScalingAndCroppingForSize:(CGSize)targetSize
{
    UIImage *sourceImage = self;
    UIImage *newImage = nil;
    
    UIGraphicsBeginImageContext(targetSize); // this will crop
        
    CGRect newFr = CGRectMake(0, 0, targetSize.width, targetSize.height);
    [sourceImage drawInRect:newFr];
    
    newImage = UIGraphicsGetImageFromCurrentImageContext();
    
    if(newImage == nil)
    {
        NSLog(@"could not scale image");
    }
    
    //pop the context to get back to the default
    UIGraphicsEndImageContext();
    
    return newImage;
}

+(UIImage*)changeColor:(UIImage*)image newColor:(UIColor*)color
{
    CGRect rect = CGRectMake(0, 0, image.size.width, image.size.height);
    UIGraphicsBeginImageContext(rect.size);
    CGContextRef context = UIGraphicsGetCurrentContext();
    CGContextClipToMask(context, rect, image.CGImage);
    CGContextSetFillColorWithColor(context, [color CGColor]);
    CGContextFillRect(context, rect);
    UIImage *img = UIGraphicsGetImageFromCurrentImageContext();
    UIGraphicsEndImageContext();
    
    return [UIImage imageWithCGImage:img.CGImage
                    scale:1.0 orientation: UIImageOrientationDownMirrored];

}

@end
