//
//  EmailBodyRequest.m
//  Kikbak
//
//  Created by Ian Barile on 7/30/13.
//  Copyright (c) 2013 Ian Barile. All rights reserved.
//

#import "EmailBodyRequest.h"
#import "NotificationContstants.h"
#import "HTTPConstants.h"
#import "NSURL+QueryString.h"
#import "EmailFields.h"
#import "SBJson.h"

@implementation EmailBodyRequest

-(void)requestEmailBody{
    
    request = [[HttpRequest alloc]init];
    request.restDelegate = self;
    
    NSString* host = [NSString stringWithFormat:@"%@%@", @(php_host), @"email.php"];
    NSMutableDictionary* dict = [[NSMutableDictionary alloc]initWithCapacity:3];
    [dict setObject:self.name forKey:@"name"];
    [dict setObject:self.code forKey:@"code"];
    [dict setObject:self.description forKey:@"desc"];
    [request httpGetQueryString:[NSURL URLwithQueryString:host withQueryParams:dict]];
    
}



-(void)parseResponse:(NSData*)data{
    
    NSString* json = [[NSString alloc]initWithData:data encoding:NSUTF8StringEncoding];
    EmailFields* fields = [[EmailFields alloc]init];
    id dict = [json JSONValue];
    fields.subject = [dict objectForKey:@"subject"];
    fields.body = [dict objectForKey:@"body"];
    
    [[NSNotificationCenter defaultCenter]postNotificationName:kKikbakEmailBodySuccess object:fields];
}

-(void)handleError:(NSInteger)statusCode withData:(NSData*)data{
    
    [[NSNotificationCenter defaultCenter]postNotificationName:kKikbakEmailBodyError object:nil];
}

@end
