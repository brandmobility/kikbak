//
//  ImageUploadRequest.m
//  Kikbak
//
//  Created by Ian Barile on 8/1/13.
//  Copyright (c) 2013 Ian Barile. All rights reserved.
//

#import "ImageUploadRequest.h"
#import "KikbakConstants.h"
#import "NotificationContstants.h"
#import "SBJson.h"


@implementation ImageUploadRequest

-(void)postImage{
    
    NSUserDefaults* prefs = [NSUserDefaults standardUserDefaults];
    NSString* userId = [prefs objectForKey:KIKBAK_USER_ID];
    assert(userId != nil);
    if( userId == nil){
        return;
    }
 
    // encode the image as JPEG
    NSData *imageData = UIImageJPEGRepresentation(self.image, 0.9);
    
    // set up the request
    NSMutableURLRequest *request = [[NSMutableURLRequest alloc] init];
    [request setURL:[NSURL URLWithString:@"http://test.kikbak.me/s/upload.php"]];
    
    // create a boundary to delineate the file
    NSString *boundary = @"14737809831466499882746641449";
    // tell the server what to expect
    NSString *contentType =
    [NSString stringWithFormat:@"multipart/form-data; boundary=%@", boundary];
    [request addValue:contentType forHTTPHeaderField: @"Content-Type"];
    
    // make a buffer for the post body
    NSMutableData *body = [NSMutableData data];
    
    // add a boundary to show where the title starts
    [body appendData:[[NSString stringWithFormat:@"\r\n--%@\r\n", boundary]
                      dataUsingEncoding:NSASCIIStringEncoding]];
    
    // add the title
    [body appendData:[@"Content-Disposition: form-data; name=\"userId\"\r\n\r\n"
                      dataUsingEncoding:NSASCIIStringEncoding]];
    [body appendData:[userId
                      dataUsingEncoding:NSASCIIStringEncoding]];
    
    // add a boundary to show where the file starts
    [body appendData:[[NSString stringWithFormat:@"\r\n--%@\r\n", boundary]
                      dataUsingEncoding:NSASCIIStringEncoding]];
    
    // add a form field
    [body appendData:[@"Content-Disposition: form-data; name=\"file\"; filename=\"image.jpeg\"\r\n"
                      dataUsingEncoding:NSASCIIStringEncoding]];
    
    // tell the server to expect some binary
    [body appendData:[@"Content-Type: application/octet-stream\r\n"
                      dataUsingEncoding:NSASCIIStringEncoding]];
    [body appendData:[@"Content-Transfer-Encoding: binary\r\n"
                      dataUsingEncoding:NSASCIIStringEncoding]];
    [body appendData:[[NSString stringWithFormat:
                       @"Content-Length: %i\r\n\r\n", imageData.length]
                      dataUsingEncoding:NSASCIIStringEncoding]];
    
    // add the payload
    [body appendData:[NSData dataWithData:imageData]];
    
    // tell the server the payload has ended
    [body appendData:[[NSString stringWithFormat:@"\r\n--%@--\r\n", boundary]
                      dataUsingEncoding:NSASCIIStringEncoding]];
    
    // add the POST data as the request body
    [request setHTTPMethod:@"POST"];
    [request setHTTPBody:body];
    
    httpRequest = [[HttpRequest alloc]init];
    httpRequest.restDelegate = self;
    NSURLConnection *connection = [[NSURLConnection alloc]initWithRequest:request delegate:httpRequest];
    if (connection) {
        httpRequest.receivedData = [NSMutableData data];
    }
}

-(void)parseResponse:(NSData*)data{
     NSString* json = [[NSString alloc]initWithData:data encoding:NSUTF8StringEncoding];
    id dict = [json JSONValue];
    [[NSNotificationCenter defaultCenter]postNotificationName:kKikbakEmailBodySuccess object:[dict objectForKey:@"url"]];
}

-(void)handleError:(NSInteger)statusCode withData:(NSData*)data{
    
    [[NSNotificationCenter defaultCenter]postNotificationName:kKikbakEmailBodyError object:nil];
}

}

@end
