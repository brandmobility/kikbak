
package com.referredlabs.kikbak.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import com.referredlabs.kikbak.R;

public class TtfTextView extends TextView {

  public TtfTextView(Context context, AttributeSet attrs) {
    super(context, attrs);

    // Typeface.createFromAsset doesn't work in the layout editor. Skipping...
    if (isInEditMode()) {
      return;
    }

    TypedArray styledAttrs = context.obtainStyledAttributes(attrs, R.styleable.TtfTextView);
    String fontName = styledAttrs.getString(R.styleable.TtfTextView_typeface);
    final float shadowRadius = styledAttrs.getDimension(R.styleable.TtfTextView_shadowRadius, 0f);
    final float shadowDx = styledAttrs.getDimension(R.styleable.TtfTextView_shadowDx, 0f);
    final float shadowDy = styledAttrs.getDimension(R.styleable.TtfTextView_shadowDy, 0f);
    final int shadowColor = styledAttrs.getColor(R.styleable.TtfTextView_shadowColor, 0);
    styledAttrs.recycle();

    if (shadowColor != 0) {
      setShadowLayer(shadowRadius, shadowDx, shadowDy, shadowColor);
    } else {
      getPaint().clearShadowLayer();
    }

    if (fontName != null) {
      Typeface typeface = Typeface.createFromAsset(context.getAssets(), fontName);
      setTypeface(typeface);
    }
  }
}
