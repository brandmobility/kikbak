
package com.referredlabs.kikbak.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;

import com.referredlabs.kikbak.R;

public class TtfButton extends Button {

  public TtfButton(Context context, AttributeSet attrs) {
    super(context, attrs);

    // Typeface.createFromAsset doesn't work in the layout editor. Skipping...
    if (isInEditMode()) {
      return;
    }

    TypedArray styledAttrs = context.obtainStyledAttributes(attrs, R.styleable.TtfTextView);
    String fontName = styledAttrs.getString(R.styleable.TtfTextView_typeface);
    styledAttrs.recycle();

    if (fontName != null) {
      Typeface typeface = Typeface.createFromAsset(context.getAssets(), fontName);
      setTypeface(typeface);
    }
  }
}
