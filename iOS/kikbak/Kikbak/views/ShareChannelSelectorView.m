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


@interface ShareChannelSelectorView ()

@property (nonatomic,strong) UIView* backgroundView;
@property (nonatomic,strong) UILabel* helped;
@property (nonatomic,strong) UILabel* byWhom;
@property (nonatomic,strong) UITextView* employee;
@property (nonatomic,strong) UIImageView* employeeShadow;
@property (nonatomic,strong) UIView* location;
@property (nonatomic,strong) UILabel* address;
@property (nonatomic,strong) UIImageView* chevron;
@property (nonatomic,strong) UIButton* fbBtn;
@property (nonatomic,strong) UIImageView* or1;
@property (nonatomic,strong) UIButton* emailBtn;
@property (nonatomic,strong) UIImageView* or2;
@property (nonatomic,strong) UIButton* smsBtn;
@property (nonatomic,strong) UIButton* closeBtn;

@property (nonatomic,strong) NSNumber* locationId;
@property (nonatomic,strong) NSString* employeeName;

@property (nonatomic,strong) UITableView* locationsTable;

-(IBAction)onEmail:(id)sender;
-(IBAction)onSms:(id)sender;
-(IBAction)onTimeline:(id)sender;
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
    
    self.locationId = ((Location*)[[self.locations allObjects]objectAtIndex:0]).locationId;
    self.employeeName = [[NSString alloc]init];
    
    int locationOffset = 0;
    if( [self.locations count] > 1){
        locationOffset = 0;
    }
    
    int backgroundY = 80;
    if( ![UIDevice hasFourInchDisplay]){
        backgroundY = 40;
    }
    
    
    self.backgroundView = [[UIView alloc] initWithFrame:CGRectMake(23, backgroundY, self.frame.size.width - 46, 356-locationOffset)];
    self.backgroundView.backgroundColor = UIColorFromRGB(0xE0E0E0);
    self.backgroundView.layer.cornerRadius = 10;
    self.backgroundView.layer.masksToBounds = YES;
    [self addSubview:self.backgroundView];
    
    self.helped = [[UILabel alloc]initWithFrame:CGRectMake(0, 15, self.backgroundView.frame.size.width, 19)];
    self.helped.font = [UIFont fontWithName:@"HelveticaNeue" size:16];
    self.helped.text = NSLocalizedString(@"Helped", nil);
    self.helped.textAlignment = NSTextAlignmentCenter;
    self.helped.backgroundColor = [UIColor clearColor];
    self.helped.textColor = UIColorFromRGB(0x3a3a3a);
    [self.backgroundView addSubview:self.helped];

    self.byWhom = [[UILabel alloc]initWithFrame:CGRectMake(40, 38, self.backgroundView.frame.size.width-80, 28)];
    self.byWhom.font = [UIFont fontWithName:@"HelveticaNeue" size:12];
    self.byWhom.text = NSLocalizedString(@"Let your nearby friends know", nil);
    self.byWhom.textAlignment = NSTextAlignmentCenter;
    self.byWhom.numberOfLines = 2;
    self.byWhom.backgroundColor = [UIColor clearColor];
    self.byWhom.textColor = UIColorFromRGB(0x808080);
    [self.backgroundView addSubview:self.byWhom];
    
    
    self.employee = [[UITextView alloc]initWithFrame:CGRectMake(12, 72, self.backgroundView.frame.size.width-24, 35)];
    self.employee.delegate = self;
    self.employee.text = NSLocalizedString(@"Employee", nil);
    self.employee.textColor = UIColorFromRGB(0x7F7F7F);
    self.employee.scrollEnabled = NO;
    self.employee.returnKeyType = UIReturnKeyDone;
    self.employee.contentInset = UIEdgeInsetsMake(2, 5, 2, 5);
    self.employee.font = [UIFont fontWithName:@"HelveticaNeue" size:13];
    self.employee.backgroundColor = [UIColor clearColor];
    self.employee.layer.cornerRadius = 5;
    self.employee.layer.masksToBounds = YES;
    self.employee.layer.borderWidth = 1;
    self.employee.layer.borderColor = [UIColorFromRGB(0xb9b9b9) CGColor];
    self.employeeShadow = [[UIImageView alloc]initWithImage:[UIImage imageNamed:@"bg_textview_suggest_business"]];
    self.employeeShadow.frame = CGRectMake(11, 72, self.backgroundView.frame.size.width-22, 35);
    [self.backgroundView addSubview:self.employeeShadow];
    [self.backgroundView addSubview:self.employee];
    
    if( locationOffset == 0 ){
        self.location = [[UIView alloc]initWithFrame:CGRectMake(12, 114, self.backgroundView.frame.size.width-24, 35)];
        self.location.layer.cornerRadius = 4;
        self.location.layer.masksToBounds = YES;
        self.location.layer.borderColor = [UIColorFromRGB(0xa0a0a0) CGColor];
        self.location.layer.borderWidth = 1;
        self.location.layer.shadowRadius = 1;
        self.location.backgroundColor = [UIColor clearColor];
        [self.backgroundView addSubview:self.location];
        
        self.address = [[UILabel alloc]initWithFrame:CGRectMake(8, 8, self.location.frame.size.width-16, 20)];
        self.address.text = ((Location*)[self.locations anyObject]).address;
        self.address.backgroundColor = [UIColor clearColor];
        self.address.font = [UIFont fontWithName:@"HelveticaNeue" size:14];
        self.address.textColor = UIColorFromRGB(0x5a5a5a);
        self.address.textAlignment = NSTextAlignmentCenter;
        [self.location addSubview:self.address];
        
        self.chevron = [[UIImageView alloc] initWithFrame:CGRectMake(self.location.frame.size.width-22, 10, 11, 16)];
        self.chevron.image = [UIImage imageNamed:@"ic_friend_chevron"];
        [self.location addSubview:self.chevron];
        
        UITapGestureRecognizer* tap = [[UITapGestureRecognizer alloc]initWithTarget:self action:@selector(onLocationTap:)];
        [self.location addGestureRecognizer:tap];
        tap.numberOfTapsRequired = 1;
        tap.numberOfTouchesRequired = 1;
    }
    
    self.fbBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    self.fbBtn.frame = CGRectMake(12, self.backgroundView.frame.size.height - 195, self.backgroundView.frame.size.width - 24, 40);
    [self.fbBtn setBackgroundImage:[UIImage imageNamed:@"btn_blue"] forState:UIControlStateNormal];
    [self.fbBtn setTitle:NSLocalizedString(@"Post on my timeline", nil) forState:UIControlStateNormal];
    self.fbBtn.titleLabel.font = [UIFont fontWithName:@"HelveticaNeue-Bold" size:16];
    self.fbBtn.titleLabel.textColor = [UIColor whiteColor];
    [self.fbBtn addTarget:self action:@selector(onTimeline:) forControlEvents:UIControlEventTouchUpInside];
    [self.backgroundView addSubview:self.fbBtn];
    

    UIImage* orSeperator = [UIImage imageNamed:@"seperator_share_or"];
    self.or1 = [[UIImageView alloc]initWithImage:orSeperator];
    CGRect fr = CGRectMake(69, self.backgroundView.frame.size.height - 145, orSeperator.size.width/2, orSeperator.size.height/2);
    self.or1.frame = fr;
    [self.backgroundView addSubview:self.or1];
    
    self.emailBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    self.emailBtn.frame = CGRectMake(12, self.backgroundView.frame.size.height - 125, self.backgroundView.frame.size.width - 24, 40);
    [self.emailBtn setBackgroundImage:[UIImage imageNamed:@"btn_grey"] forState:UIControlStateNormal];
    [self.emailBtn setTitle:NSLocalizedString(@"Email", nil) forState:UIControlStateNormal];
    self.emailBtn.titleLabel.font = [UIFont fontWithName:@"HelveticaNeue-Bold" size:16];
    self.emailBtn.titleLabel.textColor = [UIColor whiteColor];
    [self.emailBtn addTarget:self action:@selector(onEmail:) forControlEvents:UIControlEventTouchUpInside];
    [self.backgroundView addSubview:self.emailBtn];
    
    
    self.or2 = [[UIImageView alloc]initWithImage:orSeperator];
    fr = CGRectMake(69, self.backgroundView.frame.size.height - 75, orSeperator.size.width/2, orSeperator.size.height/2);
    self.or2.frame = fr;
    [self.backgroundView addSubview:self.or2];
    
    self.smsBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    self.smsBtn.frame = CGRectMake(12, self.backgroundView.frame.size.height - 55, self.backgroundView.frame.size.width - 24, 40);
    [self.smsBtn setBackgroundImage:[UIImage imageNamed:@"btn_grey"] forState:UIControlStateNormal];
    [self.smsBtn setTitle:NSLocalizedString(@"SMS", nil) forState:UIControlStateNormal];
    self.smsBtn.titleLabel.font = [UIFont fontWithName:@"HelveticaNeue-Bold" size:16];
    self.smsBtn.titleLabel.textColor = [UIColor whiteColor];
    [self.smsBtn addTarget:self action:@selector(onSms:) forControlEvents:UIControlEventTouchUpInside];
    [self.backgroundView addSubview:self.smsBtn];

    
    self.closeBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    [self.closeBtn addTarget:self action:@selector(onCloseBtn:) forControlEvents:UIControlEventTouchUpInside];
    [self.closeBtn setImage:[UIImage imageNamed:@"btn_cancel"] forState:UIControlStateNormal];
    self.closeBtn.frame = CGRectMake(8, self.backgroundView.frame.origin.y-15, 35, 35);
    [self addSubview:self.closeBtn];
}

-(IBAction)onEmail:(id)sender{
    [self.delegate onEmailSelected:self.locationId withEmployeeName:self.employeeName];
    [self removeFromSuperview];
}

-(IBAction)onTimeline:(id)sender{
    [self.delegate onTimelineSelected:self.locationId withEmployeeName:self.employeeName];
    [self removeFromSuperview];
}

-(IBAction)onSms:(id)sender{
    [self.delegate onSmsSelected:self.locationId withEmployeeName:self.employeeName];
    [self removeFromSuperview];
}

-(IBAction)onCloseBtn:(id)sender{
    [self removeFromSuperview];
}

#pragma mark - text view
-(void)resignTextView
{
    [self.employee resignFirstResponder];
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
    
    if(textView == self.employee && [textView.text compare:@""] == NSOrderedSame){
        self.employee.text = NSLocalizedString(@"Employee", nil);
        self.employee.textColor = UIColorFromRGB(0x7F7F7F);
    }
    else{
        self.employeeName = self.employee.text;
    }
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
