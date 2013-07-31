//
//  SMSMessageBodyRequest.m
//  Kikbak
//
//  Created by Ian Barile on 7/26/13.
//  Copyright (c) 2013 Ian Barile. All rights reserved.
//

#import "SMSMessageBodyRequest.h"
#import "NotificationContstants.h"
#import "HTTPConstants.h"
#import "NSURL+QueryString.h"


@implementation SMSMessageBodyRequest

-(void)requestSMSBody{
    
    request = [[HttpRequest alloc]init];
    request.restDelegate = self;

    NSString* host = [NSString stringWithFormat:@"%@%@", php_host, @"sms"];
    NSMutableDictionary* dict = [[NSMutableDictionary alloc]initWithCapacity:3];
    [dict setObject:self.name forKey:@"name"];
    [dict setObject:self.code forKey:@"code"];
    [dict setObject:self.description forKey:@"desc"];
    [request httpGetQueryString:[NSURL URLwithQueryString:host withQueryParams:dict]];
    
}



-(void)parseResponse:(NSData*)data{

    NSString* smsBody = [[NSString alloc]initWithData:data encoding:NSUTF8StringEncoding];
    [[NSNotificationCenter defaultCenter]postNotificationName:kKikbakSMSBodySuccess object:smsBody];
}

-(void)handleError:(NSInteger)statusCode withData:(NSData*)data{
    
    [[NSNotificationCenter defaultCenter]postNotificationName:kKikbakSMSBodyError object:nil];
}


@end
