//
//  PostRequest.m
//  kikback
//
//  Created by Ian Barile on 12/8/12.
//  Copyright (c) 2012 Ian Barile. All rights reserved.
//

#import "PostRequest.h"
#import "HTTPConstants.h"


@implementation PostRequest


-(void)makeSyncRequest{
  
  NSURL* url = [NSURL URLWithString:[NSString stringWithFormat:@"http://%s/%s/%@",hostname, kikbak_service, _resource]];
  
  NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:url
                                                         cachePolicy:NSURLRequestReloadIgnoringLocalCacheData timeoutInterval:60.0];
  
  NSData *requestData = [NSData dataWithBytes:[_body UTF8String] length:[_body lengthOfBytesUsingEncoding:NSUTF8StringEncoding]];
  
  [request setHTTPMethod:@"POST"];
  [request setValue:@"application/json" forHTTPHeaderField:@"Accept"];
  [request setValue:@"application/json" forHTTPHeaderField:@"Content-Type"];
  [request setValue:[NSString stringWithFormat:@"%d", [requestData length]] forHTTPHeaderField:@"Content-Length"];
  [request setHTTPBody: requestData];
  
  NSURLConnection *connection = [[NSURLConnection alloc]initWithRequest:request delegate:self];
  if (connection) {
    _receivedData = [NSMutableData data];
  }
}

- (void)connection:(NSURLConnection *)connection didFailWithError:(NSError *)error{
  NSLog(@"Error: %@", error);
}

- (NSURLRequest *)connection:(NSURLConnection *)connection willSendRequest:(NSURLRequest *)request redirectResponse:(NSURLResponse *)response{
  return request;
}

- (void)connection:(NSURLConnection *)connection didReceiveResponse:(NSURLResponse *)response{

}

- (void)connection:(NSURLConnection *)connection didReceiveData:(NSData *)data{
  
  if (_restDelegate) {
    [_restDelegate receivedData:data];
  }
}



- (void)connection:(NSURLConnection *)connection didSendBodyData:(NSInteger)bytesWritten
                  totalBytesWritten:(NSInteger)totalBytesWritten totalBytesExpectedToWrite:(NSInteger)totalBytesExpectedToWrite{
  NSLog(@"didSendBodyData: written: %d; totalWritten: %d; expected written: %d",bytesWritten, totalBytesWritten, totalBytesExpectedToWrite);
  
}

- (NSCachedURLResponse *)connection:(NSURLConnection *)connection willCacheResponse:(NSCachedURLResponse *)cachedResponse{
  
  return cachedResponse;
}

- (void)connectionDidFinishLoading:(NSURLConnection *)connection{
  NSLog(@"connectionDidFinishLoading");
  
}

@end
