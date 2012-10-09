//
//  TestViewController.m
//  kikback
//
//  Created by Ian Barile on 9/20/12.
//  Copyright (c) 2012 Ian Barile. All rights reserved.
//

#import "PostReviewViewController.h"
#import <FacebookSDK/FacebookSDK.h>
#import "AppDelegate.h"
#import "Flurry.h"


@interface PostReviewViewController ()

@end

@implementation PostReviewViewController

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
	// Do any additional setup after loading the view.
  self.descriptionText.delegate = self;
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}


-(IBAction)takePhoto:(id)sender{
  [self.descriptionText resignFirstResponder];
  UIActionSheet* sheet = [[UIActionSheet alloc] initWithTitle:@"Share It" delegate:self cancelButtonTitle:@"Cancel" destructiveButtonTitle:nil otherButtonTitles:@"Take Photo", nil];
  [sheet showInView:self.view];
}

-(IBAction)submit:(id)sender{

  self.submit.enabled = NO;
  FBRequestConnection* connection = [[FBRequestConnection alloc]initWithTimeout:30];

  
  FBRequest* request = [FBRequest requestForUploadPhoto:self.photo.image];
  [request.parameters setValue:self.descriptionText.text forKey:@"message"];
  
  
  [connection addRequest:request completionHandler:^(FBRequestConnection *connection, id result, NSError *error) {
    if(error == nil){
      UIAlertView* alert = [[UIAlertView alloc]initWithTitle:@"KikBak" message:@"Thanks for sharing" delegate:nil cancelButtonTitle:nil otherButtonTitles:@"OK", nil];
      [Flurry logEvent:@"ShareEvent" timed:YES];
      [alert show];
    }
    else{
      [Flurry logEvent:@"FailedShareEvent" timed:YES];
      NSLog(@"Submit Error: %@", error);
    }
    self.submit.enabled = YES;
  }];
  

  [connection start];
}

- (IBAction)chooseLocation:(id)sender {
  FBPlacePickerViewController *placePickerController = [[FBPlacePickerViewController alloc] init];
  placePickerController.title = @"Pick a Seattle Place";
  placePickerController.locationCoordinate = CLLocationCoordinate2DMake(47.6097, -122.3331);
  [placePickerController loadData];
  
  // Use the modal wrapper method to display the picker.
  [placePickerController presentModallyFromViewController:self animated:YES handler:
   ^(FBViewController *sender, BOOL donePressed) {
     if (!donePressed) {
       return;
     }
     
     [[[UIAlertView alloc] initWithTitle:@"You Picked:"
                                 message:placePickerController.selection.name
                                delegate:nil
                       cancelButtonTitle:@"OK"
                       otherButtonTitles:nil]
      show];
   }];
}

- (void)actionSheet:(UIActionSheet *)actionSheet clickedButtonAtIndex:(NSInteger)buttonIndex{
  if(buttonIndex == 0){
    if([UIImagePickerController isSourceTypeAvailable:UIImagePickerControllerSourceTypeCamera]){
      UIImagePickerController* picker = [[UIImagePickerController alloc]init];
      picker.view.frame = self.view.frame;
      picker.sourceType = UIImagePickerControllerSourceTypeCamera;
      picker.delegate = self;
      
      [Flurry logEvent:@"PhotoEvent" timed:YES];
      
      [self presentViewController:picker animated:YES completion:nil];
    }
    else{
      //for simulator testing
      UIImagePickerController* picker = [[UIImagePickerController alloc]init];
      picker.view.frame = self.view.frame;
      picker.sourceType = UIImagePickerControllerSourceTypePhotoLibrary;
      picker.delegate = self;
      
      [self presentViewController:picker animated:YES completion:nil];
    }
  }
  
}

#pragma mark - image picker delegates
- (void)imagePickerController:(UIImagePickerController *)picker didFinishPickingMediaWithInfo:(NSDictionary *)info{
  
  self.photo.image = [info valueForKey:UIImagePickerControllerOriginalImage];
  
  [self dismissViewControllerAnimated:YES completion:nil];
}

- (void)imagePickerControllerDidCancel:(UIImagePickerController *)picker{

    [self dismissViewControllerAnimated:YES completion:nil];
}


#pragma mark - UITextFieldDelegate methods

- (BOOL)textView:(UITextView *)textView shouldChangeTextInRange:(NSRange)range replacementText:(NSString *)text{
  if([text caseInsensitiveCompare:@"\n"] == NSOrderedSame){
    [textView resignFirstResponder];
  }
  return YES;
}

- (void)textViewDidBeginEditing:(UITextView *)textView{
  
  CGRect fr = textView.frame;
  self.scrollView.contentOffset = CGPointMake(0, fr.origin.y);
  
}

- (void)textViewDidEndEditing:(UITextView *)textView{
  self.scrollView.contentOffset = CGPointMake(0, 0);
}

@end
