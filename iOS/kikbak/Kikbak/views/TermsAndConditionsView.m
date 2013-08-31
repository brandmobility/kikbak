//
//  TermsAndConditionsView.m
//  kikit
//
//  Created by Ian Barile on 3/14/13.
//  Copyright (c) 2013 Ian Barile. All rights reserved.
//

#import "TermsAndConditionsView.h"

#import "UIDevice+Screen.h"
#import "util.h"
#import <QuartzCore/QuartzCore.h>

@interface TermsAndConditionsView()

@property (nonatomic, strong) UIView* backgroundView;
@property (nonatomic, strong) UILabel* terms;
@property (nonatomic, strong) UIWebView* conditions;
@property (nonatomic, strong) UILabel* status;
@property (nonatomic, strong) UILabel* error;
@property (nonatomic, strong) UIButton* doneBtn;
@property (nonatomic, strong) UIActivityIndicatorView* indicator;
@property (nonatomic, strong) NSTimer* webViewTimer;



-(IBAction)onDone:(id)sender;
-(void)onLoadError:(NSString*) error;
-(void)onTimeout;

@end

@implementation TermsAndConditionsView

- (id)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        self.backgroundColor = [UIColor colorWithRed:0 green:0 blue:0 alpha:.8];
    }
    return self;
}


-(void) manuallyLayoutSubviews{
    
    self.backgroundView = [[UIView alloc] initWithFrame:CGRectMake(17, 38, self.frame.size.width - 34, self.frame.size.height - 56)];
    self.backgroundView.backgroundColor = UIColorFromRGB(0xE0E0E0);
    self.backgroundView.layer.cornerRadius = 10;
    self.backgroundView.layer.masksToBounds = YES;
    [self addSubview:self.backgroundView];
    
    self.terms = [[UILabel alloc]initWithFrame:CGRectMake(35, 25, self.backgroundView.frame.size.width - 70, 44)];
    self.terms.font = [UIFont fontWithName:@"HelveticaNeue-Bold" size:19];
    self.terms.text = NSLocalizedString(@"Terms for offer", nil);
    self.terms.textAlignment = NSTextAlignmentCenter;
    self.terms.textColor = UIColorFromRGB(0x3a3a3a);
    self.terms.backgroundColor = [UIColor clearColor];
    self.terms.numberOfLines = 2;
    [self.backgroundView addSubview:self.terms];
    
    self.conditions = [[UIWebView alloc]initWithFrame:CGRectMake(16, 85,
                                                                 self.backgroundView.frame.size.width - 36,
                                                                 self.backgroundView.frame.size.height - 85 - 61)];
    self.conditions.delegate = self;
    [self.conditions loadRequest:[NSURLRequest requestWithURL:[NSURL URLWithString:self.tosUrl]]];
    self.webViewTimer = [NSTimer scheduledTimerWithTimeInterval:5.0 target:self selector:@selector(onTimeout) userInfo:nil repeats:NO];
    
    
    self.status = [[UILabel alloc]initWithFrame:CGRectMake(35, 130, self.backgroundView.frame.size.width - 70, 22)];
    self.status.font = [UIFont fontWithName:@"HelveticaNeue-Bold" size:19];
    self.status.text = NSLocalizedString(@"Loading", nil);
    self.status.textAlignment = NSTextAlignmentCenter;
    self.status.textColor = UIColorFromRGB(0x3a3a3a);
    self.status.backgroundColor = [UIColor clearColor];
    self.status.numberOfLines = 2;
    [self.backgroundView addSubview:self.status];

    self.indicator = [[UIActivityIndicatorView alloc]initWithActivityIndicatorStyle:UIActivityIndicatorViewStyleWhiteLarge];
    self.indicator.center = self.center;
    self.indicator.color = [UIColor blackColor];
    [self addSubview:self.indicator];
    [self.indicator startAnimating];
    
    //[self.backgroundView addSubview:self.conditions];
    
    self.doneBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    self.doneBtn.frame = CGRectMake(16,self.backgroundView.frame.size.height - 50, self.backgroundView.frame.size.width - 36, 40);
    [self.doneBtn setBackgroundImage:[UIImage imageNamed:@"btn_blue"] forState:UIControlStateNormal];
    [self.doneBtn setTitle:NSLocalizedString(@"Done", nil) forState:UIControlStateNormal];
    self.doneBtn.titleLabel.textColor = [UIColor whiteColor];
    [self.doneBtn addTarget:self action:@selector(onDone:) forControlEvents:UIControlEventTouchUpInside];
    [self.backgroundView addSubview:self.doneBtn];
    
}

#pragma mark - Btn Actions
-(IBAction)onDone:(id)sender{
    self.conditions.delegate = nil;
    [self.conditions stopLoading];
    [self removeFromSuperview];
}

#pragma mark - UIWebViewDelegate methods
- (void)webViewDidFinishLoad:(UIWebView *)webView{
    [self.backgroundView addSubview:webView];
    [self.indicator removeFromSuperview];
    
    if( self.webViewTimer ){
        [self.webViewTimer invalidate];
        self.webViewTimer = nil;
    }
}

- (void)webView:(UIWebView *)webView didFailLoadWithError:(NSError *)error{
    [self onLoadError:[error localizedDescription]];
}

#pragma mark - Error handling
-(void)onLoadError:(NSString*) error{
    if( self.webViewTimer ){
        [self.webViewTimer invalidate];
        self.webViewTimer = nil;
    }
    
    self.status.text = NSLocalizedString(@"Error", nil);
    self.status.frame = CGRectMake(35, 160, self.backgroundView.frame.size.width - 70, 22);
    self.error = [[UILabel alloc]initWithFrame:CGRectMake(16, 180, self.backgroundView.frame.size.width - 36, 70)];
    self.error.font = [UIFont fontWithName:@"HelveticaNeue" size:15];
    self.error.text = error;
    self.error.textAlignment = NSTextAlignmentCenter;
    self.error.textColor = UIColorFromRGB(0x3a3a3a);
    self.error.backgroundColor = [UIColor clearColor];
    self.error.numberOfLines = 4;
    [self.backgroundView addSubview:self.error];
    
    [self.indicator removeFromSuperview];
}

#pragma mark - timer selectors
-(void)onTimeout{
    [self onLoadError:NSLocalizedString(@"TOS timeout", nil)];
}
@end
