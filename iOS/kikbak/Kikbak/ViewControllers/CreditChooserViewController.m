//
//  CreditChooserViewController.m
//  Kikbak
//
//  Created by Ian Barile on 5/25/13.
//  Copyright (c) 2013 Ian Barile. All rights reserved.
//

#import "CreditChooserViewController.h"
#import "RedeemCreditViewController.h"
#import "util.h"


@interface CreditChooserViewController ()

@property (nonatomic,strong) UIImageView* dropShadow;
@property (nonatomic,strong) UILabel* available;
@property (nonatomic,strong) UILabel* totalCredit;
@property (nonatomic,strong) UILabel* amountToUse;
@property (nonatomic,strong) UILabel* unit;
@property (nonatomic,strong) UITextField* textField;
@property (nonatomic,strong) UIButton* apply;

-(IBAction)onCancel:(id)sender;
-(IBAction)onAppy:(id)sender;

-(void) createViews;
-(void) manuallyLayoutSubiews;

@end

@implementation CreditChooserViewController

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];

    self.title = NSLocalizedString(@"Change Amount", nil);
    
	UIImage *rect = [UIImage imageNamed:@"btn_navbar_rect"];
    UIButton *cancelBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    cancelBtn.frame = CGRectMake(0, 0, rect.size.width, rect.size.height);
    [cancelBtn setBackgroundImage:rect forState:UIControlStateNormal];
    [cancelBtn setBackgroundImage:rect forState:UIControlStateHighlighted];
    [cancelBtn setTitle:NSLocalizedString(@"Cancel",nil) forState:UIControlStateNormal];
    cancelBtn.titleLabel.font = [UIFont boldSystemFontOfSize:12];
    cancelBtn.contentHorizontalAlignment = UIControlContentHorizontalAlignmentCenter;
    [cancelBtn addTarget:self action:@selector(onCancel:)    forControlEvents:UIControlEventTouchUpInside];
    UIBarButtonItem *backBarButtonItem = [[UIBarButtonItem alloc] initWithCustomView:cancelBtn];
    backBarButtonItem.style = UIBarButtonItemStylePlain;
    
    self.navigationItem.hidesBackButton = YES;
    self.navigationItem.leftBarButtonItem = backBarButtonItem;
    
    [self createViews];
}

-(void)viewWillAppear:(BOOL)animated{
    [super viewWillAppear:animated];
    
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

-(void) createViews{
    self.view.backgroundColor = [UIColor colorWithPatternImage:[UIImage imageNamed:@"bg_credit_choice"]];
    
    self.dropShadow = [[UIImageView alloc]initWithFrame:CGRectMake(0, 0, 320, 4)];
    self.dropShadow.image = [UIImage imageNamed:@"grd_credit_amount_navbar_drop_shadow"];
    [self.view addSubview:self.dropShadow];
    
    self.available = [[UILabel alloc]initWithFrame:CGRectMake(11, 15, 298, 23)];
    self.available.text = NSLocalizedString(@"Credit Available", nil);
    self.available.backgroundColor = [UIColor clearColor];
    self.available.textColor = UIColorFromRGB(0x3a3a3a);
    self.available.font = [UIFont fontWithName:@"HelveticaNeue" size:21];
    self.available.textAlignment = NSTextAlignmentLeft;
    [self.view addSubview:self.available];
    
    self.totalCredit = [[UILabel alloc]initWithFrame:CGRectMake(11, 40, 150, 38)];
    self.totalCredit.text = [NSString stringWithFormat:NSLocalizedString(@"Redeem Amount", nil), [self.credit doubleValue]];
    self.totalCredit.backgroundColor = [UIColor clearColor];
    self.totalCredit.textColor = UIColorFromRGB(0x3a3a3a);
    self.totalCredit.font = [UIFont fontWithName:@"HelveticaNeue-Bold" size:36];
    self.totalCredit.textAlignment = NSTextAlignmentLeft;
    [self.view addSubview:self.totalCredit];
    
    self.amountToUse = [[UILabel alloc]initWithFrame:CGRectMake(11, 100, 298, 23)];
    self.amountToUse.text = NSLocalizedString(@"Set amount to user", nil);
    self.amountToUse.backgroundColor = [UIColor clearColor];
    self.amountToUse.textColor = UIColorFromRGB(0x3a3a3a);
    self.amountToUse.font = [UIFont fontWithName:@"HelveticaNeue" size:21];
    self.amountToUse.textAlignment = NSTextAlignmentLeft;
    [self.view addSubview:self.amountToUse];
    
    self.unit = [[UILabel alloc]initWithFrame:CGRectMake(11, 138, 26, 38)];
    self.unit.text = NSLocalizedString(@"Currency Symbol", nil);
    self.unit.backgroundColor = [UIColor clearColor];
    self.unit.textColor = UIColorFromRGB(0x3a3a3a);
    self.unit.font = [UIFont fontWithName:@"HelveticaNeue-Bold" size:36];
    self.unit.textAlignment = NSTextAlignmentLeft;
    [self.view addSubview:self.unit];
    
    self.textField = [[UITextField alloc]initWithFrame:CGRectMake(37, 132, 271, 50)];
    self.textField.font = [UIFont fontWithName:@"HelveticaNeue-Bold" size:36];
    self.textField.textAlignment = NSTextAlignmentLeft;
    self.textField.textColor = UIColorFromRGB(0x3a3a3a);
    self.textField.text = [NSString stringWithFormat:@"%.2f", [self.credit doubleValue]];
    self.textField.keyboardType = UIKeyboardTypeDecimalPad;
    self.textField.background = [UIImage imageNamed:@"bg_credit_amount_textfield"];
    self.textField.delegate = self;
    UIView *paddingView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, 5, 20)];
    self.textField.leftView = paddingView;
    self.textField.leftViewMode = UITextFieldViewModeAlways;
    [self.textField becomeFirstResponder];
    
    [self.view addSubview:self.textField];
    

    self.apply = [UIButton buttonWithType:UIButtonTypeCustom];
    [self.apply addTarget:self action:@selector(onAppy:) forControlEvents:UIControlEventTouchUpInside];
    self.apply.frame = CGRectMake(11, 202, 298, 40);
    [self.apply setTitle:NSLocalizedString(@"Apply", nil) forState:UIControlStateNormal];
    [self.apply setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
    self.apply.titleLabel.font = [UIFont fontWithName:@"HelveticaNeue-Bold" size:15];
    [self.apply setBackgroundImage:[UIImage imageNamed:@"btn_blue"] forState:UIControlStateNormal];
    [self.view addSubview:self.apply];
}

-(void) manuallyLayoutSubiews{
    
}


#pragma mark - btn actions
-(IBAction)onCancel:(id)sender{
    [self.navigationController popViewControllerAnimated:YES];
    
}

-(IBAction)onAppy:(id)sender{
    if( [self.updateDelegate respondsToSelector:@selector(onUpdateAmount:)] ){
        NSNumberFormatter* formatter = [[NSNumberFormatter alloc] init];
        formatter.numberStyle           = NSNumberFormatterDecimalStyle;
        formatter.maximumFractionDigits = 2;
        formatter.minimumFractionDigits = 2;
        formatter.usesGroupingSeparator = NO;        
        [formatter setMaximum:self.credit];

        [self.updateDelegate onUpdateAmount: [formatter numberFromString:self.textField.text]];
    }
    
    [self.navigationController popViewControllerAnimated:YES];
}

#pragma mark - textFieldDelegate
- (BOOL)textField:(UITextField *)textField shouldChangeCharactersInRange:(NSRange)range replacementString:(NSString *)replacement
{
    NSString *text             = _textField.text;
    NSString *decimalSeperator = @".";
    NSCharacterSet *charSet    = nil;
    NSString *numberChars      = @"0123456789";
    
    
    // the number formatter will only be instantiated once ...
    
    static NSNumberFormatter *numberFormatter;
    if (!numberFormatter)
    {
        numberFormatter = [[NSNumberFormatter alloc] init];
        numberFormatter.numberStyle           = NSNumberFormatterDecimalStyle;
        numberFormatter.maximumFractionDigits = 2;
        numberFormatter.minimumFractionDigits = 0;
        numberFormatter.decimalSeparator      = decimalSeperator;
        numberFormatter.usesGroupingSeparator = NO;

        [numberFormatter setMaximum:self.credit];
//        [numberFormatter setRoundingIncrement:[NSNumber numberWithDouble:0.01]];
    }
    
    
    // create a character set of valid chars (numbers and optionally a decimal sign) ...
    
    NSRange decimalRange = [text rangeOfString:decimalSeperator];
    BOOL isDecimalNumber = (decimalRange.location != NSNotFound);
    if (isDecimalNumber)
    {
        charSet = [NSCharacterSet characterSetWithCharactersInString:numberChars];
    }
    else
    {
        numberChars = [numberChars stringByAppendingString:decimalSeperator];
        charSet = [NSCharacterSet characterSetWithCharactersInString:numberChars];
    }
    
    
    // remove amy characters from the string that are not a number or decimal sign ...
    
    NSCharacterSet *invertedCharSet = [charSet invertedSet];
    NSString *trimmedString = [replacement stringByTrimmingCharactersInSet:invertedCharSet];
    text = [text stringByReplacingCharactersInRange:range withString:trimmedString];
    
    
    // whenever a decimalSeperator is entered, we'll just update the textField.
    // whenever other chars are entered, we'll calculate the new number and update the textField accordingly.
    
    if ([replacement hasSuffix:decimalSeperator] == YES)
    {
        textField.text = text;
    }
    else if(text.length < textField.text.length){
        textField.text = text;
    }
    else
    {

        NSNumber *number = [numberFormatter numberFromString:text];
        if (number == nil)
        {
            number = self.credit;
            numberFormatter.minimumFractionDigits = 2;
        }
        textField.text = [numberFormatter stringFromNumber:number];
        numberFormatter.minimumFractionDigits = 0;
    }

    return NO; // we return NO because we have manually edited the textField contents.
}


@end
