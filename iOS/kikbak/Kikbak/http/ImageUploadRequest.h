//
//  ImageUploadRequest.h
//  Kikbak
//
//  Created by Ian Barile on 8/1/13.
//  Copyright (c) 2013 Ian Barile. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "ResponseHandlerProtocol.h"
#import "HttpRequest.h"


@interface ImageUploadRequest : NSObject <ResponseHandlerProtocol>
{
    HttpRequest* httpRequest;
}

-(void)postImage;

@property (nonatomic,strong) UIImage* image;


@end
