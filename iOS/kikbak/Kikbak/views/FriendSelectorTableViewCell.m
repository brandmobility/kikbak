//
//  FriendSelectorTableViewCell.m
//  Kikbak
//
//  Created by Ian Barile on 8/15/13.
//  Copyright (c) 2013 Ian Barile. All rights reserved.
//

#import "FriendSelectorTableViewCell.h"
#import <QuartzCore/QuartzCore.h>
#import "ImagePersistor.h"
#import "util.h"

@interface FriendSelectorTableViewCell()

@property (nonatomic,strong) UIImageView* friendImage;
@property (nonatomic,strong) UILabel* displayName;
@property (nonatomic,strong) UIImageView* chevron;

@end

@implementation FriendSelectorTableViewCell

- (id)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier
{
    self = [super initWithStyle:style reuseIdentifier:reuseIdentifier];
    if (self) {
        // Initialization code
    }
    return self;
}


-(void)createSubviews{
    self.friendImage = [[UIImageView alloc] initWithFrame:CGRectMake(15, 9, 32, 32)];
    NSString* imagePath = [ImagePersistor imageFileExists:self.fbFriendId imageType:FRIEND_IMAGE_TYPE];
    if(imagePath != nil){
        self.friendImage.image = [[UIImage alloc]initWithContentsOfFile:imagePath];
    }
    else{
        self.friendImage.image = [UIImage imageNamed:@"ic_profile"];
    }
    
    self.friendImage.layer.cornerRadius = 5;
    self.friendImage.layer.masksToBounds = YES;
    self.friendImage.layer.borderWidth = 1;
    self.friendImage.layer.borderColor= [UIColorFromRGB(0xBABABA) CGColor];
    [self addSubview:self.friendImage];
    
    self.displayName = [[UILabel alloc]initWithFrame:CGRectMake(56, 18, 177, 18)];
    self.displayName.font = [UIFont fontWithName:@"HelveticaNeue-Bold" size:16];
    self.displayName.text = self.name;
    self.displayName.textAlignment = NSTextAlignmentLeft;
    self.displayName.backgroundColor = [UIColor clearColor];
    self.displayName.textColor = UIColorFromRGB(0x3a3a3a);
    [self addSubview:self.displayName];
    
    self.chevron = [[UIImageView alloc] initWithFrame:CGRectMake(244, 16, 11, 16)];
    self.chevron.image = [UIImage imageNamed:@"ic_friend_chevron"];
    [self addSubview:self.chevron];
}

@end
