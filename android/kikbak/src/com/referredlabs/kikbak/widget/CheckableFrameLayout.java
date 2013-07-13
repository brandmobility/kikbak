
package com.referredlabs.kikbak.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.FrameLayout;

import com.referredlabs.kikbak.R;

/**
 * FrameLayout that implements Checkable interface. For using in ListViews that has multi checkable
 * rows with checkbox - to preserve checked state of the rows. This Layout requires the presence of
 * a checkbox with id R.id.check.
 */
public class CheckableFrameLayout extends FrameLayout implements Checkable {

  CheckBox mCheckBox;

  public CheckableFrameLayout(Context context) {
    super(context);
  }

  public CheckableFrameLayout(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public CheckableFrameLayout(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
  }

  @Override
  protected void onFinishInflate() {
    super.onFinishInflate();
    mCheckBox = (CheckBox) findViewById(R.id.check);
    mCheckBox.setClickable(false);
    mCheckBox.setFocusable(false);
  }

  @Override
  public void setChecked(boolean checked) {
    mCheckBox.setChecked(checked);
  }

  @Override
  public boolean isChecked() {
    return mCheckBox.isChecked();
  }

  @Override
  public void toggle() {
    mCheckBox.toggle();
  }

}
