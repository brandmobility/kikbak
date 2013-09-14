//
//  LocationTableViewCell.m
//  Kikbak
//
//  Created by Ian Barile on 8/19/13.
//  Copyright (c) 2013 Ian Barile. All rights reserved.
//

#import "LocationTableViewCell.h"
#import "util.h"

@interface LocationTableViewCell()

@property(nonatomic,strong) UILabel* locationAddress;

@end

@implementation LocationTableViewCell

- (id)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier
{
    self = [super initWithStyle:style reuseIdentifier:reuseIdentifier];
    if (self) {
        self.locationAddress = [[UILabel alloc]initWithFrame:CGRectMake(8, 0, 244, 35)];
        self.locationAddress.font = [UIFont fontWithName:@"HelveticaNeue" size:14];
        self.locationAddress.textAlignment = NSTextAlignmentLeft;
        self.locationAddress.backgroundColor = [UIColor clearColor];
        self.locationAddress.textColor = UIColorFromRGB(0x808080);
        [self addSubview:self.locationAddress];
    }
    return self;
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated
{
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}

-(void)manuallyLayoutSubviews{
    self.locationAddress.text = self.address;
}



@end
