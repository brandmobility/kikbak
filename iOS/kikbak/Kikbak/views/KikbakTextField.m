//
//  KikbakTextField.m
//  Kikbak
//
//  Created by Ian Barile on 8/23/13.
//  Copyright (c) 2013 Ian Barile. All rights reserved.
//

#import "KikbakTextField.h"
#import "util.h"

@implementation KikbakTextField

- (id)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        self.background = [UIImage imageNamed:@"bg_textview_suggest_business"];
    }
    return self;
}

/*
// Only override drawRect: if you perform custom drawing.
// An empty implementation adversely affects performance during animation.
- (void)drawRect:(CGRect)rect
{
    // Drawing code
}
*/

- (CGRect)textRectForBounds:(CGRect)bounds{
    bounds.origin.x = 6;
    bounds.origin.y = 7;
    return bounds;
}

- (CGRect)editingRectForBounds:(CGRect)bounds{
    return  [self textRectForBounds:bounds];
}

- (void) drawPlaceholderInRect:(CGRect)rect {
    [UIColorFromRGB(0x7f7f7f) setFill];
    [[self placeholder] drawInRect:rect withFont:[UIFont systemFontOfSize:16]];
}

@end
