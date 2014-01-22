//
//  ClaimSuccess.h
//  Kikbak
//
//  Created by Ian Barile on 8/25/13.
//  Copyright (c) 2013 Ian Barile. All rights reserved.
//

#import <UIKit/UIKit.h>

@protocol ClaimCompleteDelegate <NSObject>

-(void)onClaimFinished;
-(void)onClaimDone;

@end

@interface ClaimSuccessView : UIView

@property (nonatomic,strong) id<ClaimCompleteDelegate> delegate;

-(void) createSubviews;

@end
