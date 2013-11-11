//
//  ShareChannelSelectorView.m
//  Kikbak
//
//  Created by Ian Barile on 7/14/13.
//  Copyright (c) 2013 Ian Barile. All rights reserved.
//

#import "ShareChannelSelectorView.h"
#import <QuartzCore/QuartzCore.h>
#import "util.h"
#import "LocationTableViewCell.h"
#import "Location.h"
#import "UIDevice+Screen.h"
#import "Distance.h"
#import "Flurry.h"
#import "TwitterApi.h"


@interface ShareChannelSelectorView ()

@property (nonatomic,strong) UIView* backgroundView;
//@property (nonatomic,strong) UILabel* helped;
//@property (nonatomic,strong) UILabel* byWhom;
//@property (nonatomic,strong) UITextView* employee;
@property (nonatomic,strong) UILabel* giveOffer;
@property (nonatomic,strong) UILabel* preferredLocation;
@property (nonatomic,strong) UIImageView* employeeShadow;
@property (nonatomic,strong) UIView* location;
@property (nonatomic,strong) UILabel* address;
@property (nonatomic,strong) UIImageView* chevron;
@property (nonatomic,strong) UIButton* fbBtn;
@property (nonatomic,strong) UIButton* emailBtn;
@property (nonatomic,strong) UIButton* smsBtn;
@property (nonatomic,strong) UIButton* twitterBtn;
@property (nonatomic,strong) UIButton* closeBtn;

@property (nonatomic,strong) NSNumber* locationId;
@property (nonatomic,strong) NSString* employeeName;

@property (nonatomic,strong) UITableView* locationsTable;
@property (nonatomic) double distanceToClosestLocation;


-(IBAction)onEmail:(id)sender;
-(IBAction)onSms:(id)sender;
-(IBAction)onFacebook:(id)sender;
-(IBAction)onTwitter:(id)sender;
-(IBAction)onCloseBtn:(id)sender;

-(void)onLocationTap:(UITapGestureRecognizer *)sender ;

@end

@implementation ShareChannelSelectorView

- (id)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        // Initialization code
        self.backgroundColor = [UIColor colorWithRed:0 green:0 blue:0 alpha:.8];
    }
    [Flurry logEvent:@"give method select"];
    
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
-(void)createsubviews{
    
    self.employeeName = [[NSString alloc]init];
    
    int locationOffset = 60;
    if( [self.locations count] > 1){
        locationOffset = 0;
    }
    
    int backgroundY = 120;
    if( ![UIDevice hasFourInchDisplay]){
        backgroundY = 80;
    }
    
    
    self.backgroundView = [[UIView alloc] initWithFrame:CGRectMake(23, backgroundY, self.frame.size.width - 46, 200-locationOffset)];
    self.backgroundView.backgroundColor = UIColorFromRGB(0xE0E0E0);
    self.backgroundView.layer.cornerRadius = 10;
    self.backgroundView.layer.masksToBounds = YES;
    [self addSubview:self.backgroundView];
   
    self.giveOffer = [[UILabel alloc]initWithFrame:CGRectMake(0, 15, self.backgroundView.frame.size.width, 23)];
    self.giveOffer.font = [UIFont fontWithName:@"HelveticaNeue-Bold" size:20];
    self.giveOffer.text = NSLocalizedString(@"Give offer", nil);
    self.giveOffer.textAlignment = NSTextAlignmentCenter;
    self.giveOffer.backgroundColor = [UIColor clearColor];
    self.giveOffer.textColor = UIColorFromRGB(0x3a3a3a);
    [self.backgroundView addSubview:self.giveOffer];
    
    
    Location* location = [self.locations anyObject];
    CLLocation* current = [[CLLocation alloc]initWithLatitude:[location.latitude doubleValue] longitude:[location.longitude doubleValue]];
    self.distanceToClosestLocation = [Distance distanceToInFeet:current];
    for( Location* store in self.locations){
        CLLocation* next = [[CLLocation alloc]initWithLatitude:[store.latitude doubleValue] longitude:[store.longitude doubleValue]];
        if (self.distanceToClosestLocation > [Distance distanceToInFeet:next]) {
            location = store;
            self.distanceToClosestLocation = [Distance distanceToInFeet:next];
        }
    }
    
    //half mile in feet
    if( self.distanceToClosestLocation < 2640){
        self.locationId = location.locationId;
    }
    
    if( locationOffset == 0 ){
        
        self.preferredLocation = [[UILabel alloc]initWithFrame:CGRectMake(0, 52, self.backgroundView.frame.size.width, 19)];
        self.preferredLocation.font = [UIFont fontWithName:@"HelveticaNeue" size:12];
        self.preferredLocation.text = NSLocalizedString(@"Preferred Location", nil);
        self.preferredLocation.textAlignment = NSTextAlignmentCenter;
        self.preferredLocation.backgroundColor = [UIColor clearColor];
        self.preferredLocation.textColor = UIColorFromRGB(0x999999);
        [self.backgroundView addSubview:self.preferredLocation];
        
        
        self.location = [[UIView alloc]initWithFrame:CGRectMake(12, 80, self.backgroundView.frame.size.width-24, 35)];
        self.location.layer.cornerRadius = 4;
        self.location.layer.masksToBounds = YES;
        self.location.layer.borderColor = [UIColorFromRGB(0xa0a0a0) CGColor];
        self.location.layer.borderWidth = 1;
        self.location.layer.shadowRadius = 1;
        self.location.backgroundColor = [UIColor whiteColor];
        [self.backgroundView addSubview:self.location];
        
        self.address = [[UILabel alloc]initWithFrame:CGRectMake(8, 8, self.location.frame.size.width-16, 20)];
        self.address.backgroundColor = [UIColor clearColor];
        self.address.font = [UIFont fontWithName:@"HelveticaNeue" size:14];
        self.address.textColor = UIColorFromRGB(0x5a5a5a);
        self.address.textAlignment = NSTextAlignmentLeft;
        [self.location addSubview:self.address];
        
        //chose text based on distance
        if( self.distanceToClosestLocation > 2640){// half mile in feet
            self.address.text = NSLocalizedString(@"Pick Location", nil);
        }
        else{
            self.address.text = location.address;
        }
        
        self.chevron = [[UIImageView alloc] initWithFrame:CGRectMake(self.location.frame.size.width-22, 10, 11, 16)];
        self.chevron.image = [UIImage imageNamed:@"ic_friend_chevron"];
        [self.location addSubview:self.chevron];
        
        UITapGestureRecognizer* tap = [[UITapGestureRecognizer alloc]initWithTarget:self action:@selector(onLocationTap:)];
        [self.location addGestureRecognizer:tap];
        tap.numberOfTapsRequired = 1;
        tap.numberOfTouchesRequired = 1;
    }
    
    self.emailBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    self.emailBtn.frame = CGRectMake(11, self.backgroundView.frame.size.height - 65, 54, 50);
    [self.emailBtn setBackgroundImage:[UIImage imageNamed:@"btn_email_share"] forState:UIControlStateNormal];
    [self.emailBtn addTarget:self action:@selector(onEmail:) forControlEvents:UIControlEventTouchUpInside];
    [self.backgroundView addSubview:self.emailBtn];
    
    
    self.smsBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    self.smsBtn.frame = CGRectMake(77, self.backgroundView.frame.size.height - 65, 54, 50);
    [self.smsBtn setBackgroundImage:[UIImage imageNamed:@"btn_sms_share"] forState:UIControlStateNormal];
    [self.smsBtn addTarget:self action:@selector(onSms:) forControlEvents:UIControlEventTouchUpInside];
    [self.backgroundView addSubview:self.smsBtn];

    self.fbBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    self.fbBtn.frame = CGRectMake(144, self.backgroundView.frame.size.height - 65, 54, 50);
    [self.fbBtn setBackgroundImage:[UIImage imageNamed:@"btn_fb_share"] forState:UIControlStateNormal];
    [self.fbBtn addTarget:self action:@selector(onFacebook:) forControlEvents:UIControlEventTouchUpInside];
    [self.backgroundView addSubview:self.fbBtn];

    self.twitterBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    self.twitterBtn.frame = CGRectMake(210, self.backgroundView.frame.size.height - 65, 54, 50);
    [self.twitterBtn setBackgroundImage:[UIImage imageNamed:@"btn_twitter_share"] forState:UIControlStateNormal];
    [self.twitterBtn addTarget:self action:@selector(onTwitter:) forControlEvents:UIControlEventTouchUpInside];
    [self.backgroundView addSubview:self.twitterBtn];

    
    self.closeBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    [self.closeBtn addTarget:self action:@selector(onCloseBtn:) forControlEvents:UIControlEventTouchUpInside];
    [self.closeBtn setImage:[UIImage imageNamed:@"btn_cancel"] forState:UIControlStateNormal];
    self.closeBtn.frame = CGRectMake(8, self.backgroundView.frame.origin.y-15, 35, 35);
    [self addSubview:self.closeBtn];
}

-(IBAction)onEmail:(id)sender{
    [self.delegate onEmailSelected:self.locationId withEmployeeName:@""];
    [Flurry logEvent:@"share via email"];
    [self removeFromSuperview];
}

-(IBAction)onFacebook:(id)sender{
    [self.delegate onFBSelected:self.locationId withEmployeeName:@""];
    [Flurry logEvent:@"share via facebook"];
    [self removeFromSuperview];
}

-(IBAction)onSms:(id)sender{
    [self.delegate onSmsSelected:self.locationId withEmployeeName:@""];
    [Flurry logEvent:@"share via sms"];
    [self removeFromSuperview];
}

-(IBAction)onTwitter:(id)sender{
    TwitterApi* twitter = [[TwitterApi alloc]init];
    if( [twitter userHasAccessToTwitter] ){
        [self.delegate onTwitterSelected:self.locationId withEmployeeName:@""];
        
        [Flurry logEvent:@"share via twitter"];
        [self removeFromSuperview];
    }
    else{
        UIAlertView* alert = [[UIAlertView alloc]initWithTitle:NSLocalizedString(@"twitter", nil) message:NSLocalizedString(@"twitter access", nil) delegate:nil cancelButtonTitle:@"Ok" otherButtonTitles:nil];
        [alert show];
    }
}


-(IBAction)onCloseBtn:(id)sender{
    [self removeFromSuperview];
}

#pragma mark - text view
-(void)resignTextView
{
//    [self.employee resignFirstResponder];
}

- (BOOL)textView:(UITextView *)textView
        shouldChangeTextInRange:(NSRange)range
                replacementText:(NSString *)atext {
	
	//weird 1 pixel bug when clicking backspace when textView is empty
	if(![textView hasText] && [atext isEqualToString:@""]) return NO;
	
	
	if ([atext isEqualToString:@"\n"]) {
        [textView resignFirstResponder];
        return NO;
	}
	
	return YES;
}

- (void)textViewDidBeginEditing:(UITextView *)textView{
    
    if([textView.text compare:NSLocalizedString(@"Employee", nil)] == NSOrderedSame ){
        textView.text = @"";
        textView.textColor = UIColorFromRGB(0x3a3a3a);
    }
    
}

- (void)textViewDidEndEditing:(UITextView *)textView{
    
//    if(textView == self.employee && [textView.text compare:@""] == NSOrderedSame){
//        self.employee.text = NSLocalizedString(@"Employee", nil);
//        self.employee.textColor = UIColorFromRGB(0x7F7F7F);
//    }
//    else{
//        self.employeeName = self.employee.text;
//    }
}

#pragma mark - table datasource methods
- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath{
    return 35;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath{
    
    LocationTableViewCell* cell = [tableView dequeueReusableCellWithIdentifier:@"cell"];
    if( cell == nil){
        cell = [[LocationTableViewCell alloc]initWithStyle:UITableViewCellStyleDefault reuseIdentifier:@"cell"];
    }
    
    cell.selectionStyle = UITableViewCellSelectionStyleGray;
    Location* location = [[self.locations allObjects] objectAtIndex:indexPath.row];
    cell.address = location.address;
    [cell manuallyLayoutSubviews];
    return cell;
}


- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section{
    return [self.locations count];
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView{
    return 1;
}


- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath{
    Location* selected = ((Location*)[[self.locations allObjects] objectAtIndex:indexPath.row]);
    self.locationId = selected.locationId;
    self.address.text = selected.address;
    [tableView removeFromSuperview];
}


#pragma mark - guesture recognizer
-(void)onLocationTap:(UITapGestureRecognizer *)sender {
    
    float tableHeight = [self.locations count]*35;
    if( [self.locations count] > 4){
        tableHeight = 4*35;
    }
    if( self.locationsTable == nil){
        self.locationsTable = [[UITableView alloc]initWithFrame:CGRectMake(12, self.location.frame.origin.y + self.location.frame.size.height,
                                                                           self.backgroundView.frame.size.width-24, tableHeight) style:UITableViewStylePlain];
        self.locationsTable.dataSource = self;
        self.locationsTable.delegate = self;
        self.locationsTable.separatorStyle = UITableViewCellSeparatorStyleSingleLine;
        self.locationsTable.separatorColor = UIColorFromRGB(0xB9B9B9);
        self.locationsTable.backgroundColor = UIColorFromRGB(0xE0E0E0);
        self.locationsTable.backgroundView = nil;
        self.locationsTable.layer.borderWidth = 1;
        self.locationsTable.layer.borderColor = [UIColorFromRGB(0xa0a0a0) CGColor];
        [self.backgroundView addSubview:self.locationsTable];
    }
    else{
        [self.locationsTable removeFromSuperview];
        self.locationsTable = nil;
    }
}

@end
