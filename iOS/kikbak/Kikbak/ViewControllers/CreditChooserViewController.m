//
//  CreditChooserViewController.m
//  Kikbak
//
//  Created by Ian Barile on 5/25/13.
//  Copyright (c) 2013 Ian Barile. All rights reserved.
//

#import "CreditChooserViewController.h"

@interface CreditChooserViewController ()

@property (nonatomic, strong) UIPickerView* picker;

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

    self.picker = [[UIPickerView alloc]initWithFrame:CGRectMake(0, 266, 320, self.view.frame.size.height)];
    self.picker.delegate = self;
    self.picker.showsSelectionIndicator = YES;
    [self.view addSubview:self.picker];
	// Do any additional setup after loading the view.
}

-(void)viewWillAppear:(BOOL)animated{
    [super viewWillAppear:animated];
    
    [self.picker selectRow:[self.creditAvailable integerValue] inComponent:0 animated:NO];
    int row = round(([self.creditAvailable doubleValue] - floor([self.creditAvailable doubleValue])) * 100);
    [self.picker selectRow:row inComponent:1 animated:NO];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

#pragma mark - UIPickerDelegate Methods
- (NSInteger)numberOfComponentsInPickerView:(UIPickerView *)pickerView{
    return 2;
}


- (NSInteger)pickerView:(UIPickerView *)pickerView numberOfRowsInComponent:(NSInteger)component{
    
    if(component == 0){
        return [self.creditAvailable integerValue] + 1;
    }

    return 100;
}

//- (CGFloat)pickerView:(UIPickerView *)pickerView rowHeightForComponent:(NSInteger)component{
//    return 20;
//}
//
//
//- (CGFloat)pickerView:(UIPickerView *)pickerView widthForComponent:(NSInteger)component{
//    if (component == 0) {
//        return 60;
//    }
//    return 80;
//}

- (NSString *)pickerView:(UIPickerView *)pickerView titleForRow:(NSInteger)row forComponent:(NSInteger)component{
    return [NSString stringWithFormat:@"%d", row];
}


@end
