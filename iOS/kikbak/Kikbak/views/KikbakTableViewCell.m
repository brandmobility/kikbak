//
//  RedeemTableViewCell.m
//  Kikbak
//
//  Created by Ian Barile on 3/27/13.
//  Copyright (c) 2013 Ian Barile. All rights reserved.
//

#import "KikbakTableViewCell.h"

@interface RedeemTableViewCell()

@property (nonatomic, strong) UIView* backgroundView;
@property (nonatomic, strong) UIView* overlayView;
@property (nonatomic, strong) UIImageView* storeImage;
@property (nonatomic, strong) UILabel* store;
@property (nonatomic, strong) UIImageView* map_mark;
@property (nonatomic, strong) UILabel* distance;
@property (nonatomic, strong) UIImageView* leftArrow;
@property (nonatomic, strong) UILabel* leftText;
@property (nonatomic, strong) UIImageView* rightArrow;
@property (nonatomic, strong) UILabel* rightText;

-(void)manuallyLayoutSubview;

@end

@implementation RedeemTableViewCell

- (id)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier
{
    self = [super initWithStyle:style reuseIdentifier:reuseIdentifier];
    if (self) {
        // Initialization code
        self.frame = CGRectMake(0, 0, 320, 155);
        [self manuallyLayoutSubview];
    }
    return self;
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated
{
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}

-(void)manuallyLayoutSubview{

    self.backgroundView = [[UIView alloc]initWithFrame:CGRectMake(8, 0, 304, 155)];
    self.backgroundView.backgroundColor = [UIColor colorWithRed:0.835 green:0.835 blue:0.835 alpha:1];
    [self addSubview:self.backgroundView];
    
    
    self.overlayView = [[UIView alloc]initWithFrame:CGRectMake(8, 0, 304, 155)];
    self.overlayView.backgroundColor = [UIColor whiteColor];
    [self addSubview:self.overlayView];
    
    self.storeImage = [[UIImageView alloc]initWithImage:[UIImage imageNamed:@"logo.png"]];
    CGRect fr = self.storeImage.frame;
    fr.origin.x = 16;
    fr.origin.y = 8;
    self.storeImage.frame = fr;
    [self addSubview:self.storeImage];
    
    self.store = [[UILabel alloc]initWithFrame:CGRectMake(74, 10, 240, 20)];
    self.store.font = [UIFont boldSystemFontOfSize:18];
    self.store.text = @"Kohls";
    self.store.backgroundColor = [UIColor clearColor];
    self.store.textColor = [UIColor colorWithRed:0.435 green:0.435 blue:0.435 alpha:1];
    [self addSubview:self.store];
    
    self.map_mark =[[UIImageView alloc]initWithImage:[UIImage imageNamed:@"marker_map"]];
    fr = self.map_mark.frame;
    fr.origin.x = 74;
    fr.origin.y = 32;
    self.map_mark.frame = fr;
    [self addSubview:self.map_mark];
    
    self.distance = [[UILabel alloc]initWithFrame:CGRectMake(103, 33, 200, 21)];
    self.distance.text = @"3.2 miles";
    self.distance.font = [UIFont systemFontOfSize:12];
    self.distance.backgroundColor = [UIColor clearColor];
    self.distance.textColor = [UIColor colorWithRed:0.435 green:0.435 blue:0.435 alpha:1.0];
    [self addSubview:self.distance];
    
    self.leftArrow = [[UIImageView alloc]initWithImage:[UIImage imageNamed:@"left_ribbon"]];
    fr = self.leftArrow.frame;
    fr.origin.y = 65;
    self.leftArrow.frame = fr;
    [self addSubview:self.leftArrow];
    
    self.leftText = [[UILabel alloc]initWithFrame:CGRectMake(12, 72, 183, 21)];
    self.leftText.font = [UIFont boldSystemFontOfSize:20];
    self.leftText.textColor = [UIColor whiteColor];
    self.leftText.text = @"$10 off";
    self.leftText.backgroundColor = [UIColor clearColor];
    [self addSubview:self.leftText];
    
    
    self.rightArrow = [[UIImageView alloc]initWithImage:[UIImage imageNamed:@"right_ribbon"]];
    fr = self.rightArrow.frame;
    fr.origin.x = 101;
    fr.origin.y = 105;
    self.rightArrow.frame = fr;
    [self addSubview:self.rightArrow];
    
    self.rightText = [[UILabel alloc]initWithFrame:CGRectMake(122, 112, 186, 21)];
    self.rightText.font = [UIFont boldSystemFontOfSize:20];
    self.rightText.textColor = [UIColor whiteColor];
    self.rightText.text = @"$10 Credit";
    self.rightText.textAlignment = NSTextAlignmentRight;
    self.rightText.backgroundColor = [UIColor clearColor];
    [self addSubview:self.rightText];
}


-(void)setup:(UIColor*)arrowColor withImage:(UIImage*)img giveText:(NSString*)give redeemText:(NSString*)redeem{
    
}


@end
