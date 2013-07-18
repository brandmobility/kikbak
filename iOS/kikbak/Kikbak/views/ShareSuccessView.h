//
//  ShareSuccessView.h
//  kikit
//
//  Created by Ian Barile on 3/25/13.
//  Copyright (c) 2013 Ian Barile. All rights reserved.
//

#import <UIKit/UIKit.h>

@protocol ShareCompleteDelegate <NSObject>

-(void)onShareFinished;

@end


@interface ShareSuccessView : UIView{
}

@property (nonatomic,strong) id<ShareCompleteDelegate> delegate;

-(void) manuallyLayoutSubviews;

@end
