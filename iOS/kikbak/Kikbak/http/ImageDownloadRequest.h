//
//  ImageRequest.h
//  Kikbak
//
//  Created by Ian Barile on 4/8/13.
//  Copyright (c) 2013 Ian Barile. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "ResponseHandlerProtocol.h"
#import "HttpRequest.h"
#import "KikbakConstants.h"

@interface ImageDownloadRequest : NSObject <ResponseHandlerProtocol>
{
    HttpRequest* request;
}

-(void)requestImage;

@property (nonatomic, strong) NSNumber* fileId;
@property (nonatomic, strong) NSString* url;
@property (nonatomic) ImageType type;


@end
