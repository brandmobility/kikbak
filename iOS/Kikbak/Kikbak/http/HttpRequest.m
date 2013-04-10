//
//  PostRequest.m
//  kikback
//
//  Created by Ian Barile on 12/8/12.
//  Copyright (c) 2012 Ian Barile. All rights reserved.
//

#import "HttpRequest.h"
#import "HTTPConstants.h"

@interface HttpRequest()

@end

@implementation HttpRequest


-(void)restPostRequest{
  
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

-(void)httpGetRequest{
    NSURL* url = [NSURL URLWithString:[NSString stringWithFormat:@"%@", _resource]];
    
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:url
                                                           cachePolicy:NSURLRequestReloadIgnoringLocalCacheData timeoutInterval:60.0];
    
    [request setHTTPMethod:@"GET"];
    
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
    if( [response class] == [ NSHTTPURLResponse class]){
        NSHTTPURLResponse* httpResponse = (NSHTTPURLResponse*)response;
        statusCode = [httpResponse statusCode];
    }
}

- (void)connection:(NSURLConnection *)connection didReceiveData:(NSData *)data{
    [self.receivedData appendData:data];
}



- (void)connection:(NSURLConnection *)connection didSendBodyData:(NSInteger)bytesWritten
                  totalBytesWritten:(NSInteger)totalBytesWritten totalBytesExpectedToWrite:(NSInteger)totalBytesExpectedToWrite{
  NSLog(@"didSendBodyData: written: %d; totalWritten: %d; expected written: %d",bytesWritten, totalBytesWritten, totalBytesExpectedToWrite);
  
}

- (NSCachedURLResponse *)connection:(NSURLConnection *)connection willCacheResponse:(NSCachedURLResponse *)cachedResponse{
  
  return cachedResponse;
}

- (void)connectionDidFinishLoading:(NSURLConnection *)connection{
    if(self.restDelegate && statusCode == 200 ){
        [self.restDelegate parseResponse:self.receivedData];
    }
}

#pragma mark - authentication -

- (BOOL)connection:(NSURLConnection *)connection canAuthenticateAgainstProtectionSpace:(NSURLProtectionSpace *)protectionSpace{
  NSLog(@"canAuthenticateAgainstProtectionSpace: \n authenticationMethod - %@\n distinguishedNames - %@\nprotocol - %@,\nserverTrust - %@", [protectionSpace authenticationMethod], [protectionSpace distinguishedNames], [protectionSpace protocol], [protectionSpace serverTrust]);
  return true;
}

- (void)connection:(NSURLConnection *)connection didCancelAuthenticationChallenge:(NSURLAuthenticationChallenge *)challenge{
  
}

- (void)connection:(NSURLConnection *)connection didReceiveAuthenticationChallenge:(NSURLAuthenticationChallenge *)challenge{
  if ([challenge.protectionSpace.authenticationMethod isEqualToString:NSURLAuthenticationMethodServerTrust]) {
    
    // Verify certificate:
    SecTrustResultType trustResult;
    OSStatus status = SecTrustEvaluate(challenge.protectionSpace.serverTrust, &trustResult);
    BOOL trusted = (status == errSecSuccess) && ((trustResult == kSecTrustResultProceed) || (trustResult == kSecTrustResultUnspecified));
    
    if (trusted) {
      [challenge.sender useCredential:[NSURLCredential credentialForTrust:challenge.protectionSpace.serverTrust]
           forAuthenticationChallenge:challenge];
    } else {
      if (YES) {
        [challenge.sender useCredential:[NSURLCredential credentialForTrust:challenge.protectionSpace.serverTrust]
             forAuthenticationChallenge:challenge];
      } else {
        [challenge.sender cancelAuthenticationChallenge:challenge];
      }
    }
  } else {
    [challenge.sender performDefaultHandlingForAuthenticationChallenge:challenge];
  }
}

@end
