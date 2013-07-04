//
//  SpinnerView.m
//  Kikbak
//
//  Created by Ian Barile on 5/21/13.
//  Copyright (c) 2013 Ian Barile. All rights reserved.
//

#import "SpinnerView.h"

@interface SpinnerView()

@property (strong, nonatomic) UIActivityIndicatorView* indicator;

@end

@implementation SpinnerView



- (id)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        self.backgroundColor = [UIColor colorWithRed:.1 green:.1 blue:.1 alpha:.6];
    }
    return self;
}

-(void)startActivity{
    self.indicator = [[UIActivityIndicatorView alloc]initWithActivityIndicatorStyle:UIActivityIndicatorViewStyleWhiteLarge];
    self.indicator.center = self.center;
    [self addSubview:self.indicator];
    [self.indicator startAnimating];
}

/*
// Only override drawRect: if you perform custom drawing.
// An empty implementation adversely affects performance during animation.
- (void)drawRect:(CGRect)rect
{
    // Drawing code
}
*/

@end
