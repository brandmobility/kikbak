
package com.referredlabs.kikbak.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;

import com.squareup.picasso.Target;

public class RoundedImageView extends com.makeramen.RoundedImageView implements Target {

  public RoundedImageView(Context context) {
    super(context);
  }

  public RoundedImageView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public RoundedImageView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
  }

  @Override
  public void onError() {
  }

  @Override
  public void onSuccess(Bitmap bmp) {
    setImageBitmap(bmp);
  }
}
