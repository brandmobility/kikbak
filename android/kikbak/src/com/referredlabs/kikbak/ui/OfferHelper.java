
package com.referredlabs.kikbak.ui;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.referredlabs.kikbak.R;

public class OfferHelper extends IconBarHelper {

  private TextView mName;
  public ImageView mImage;
  private TextView mGiveValue;
  private TextView mGetValue;

  public OfferHelper(View view, IconBarListener listener) {
    super(view, listener);
    mName = (TextView) view.findViewById(R.id.offer_name);
    mImage = (ImageView) view.findViewById(R.id.offer_image);
    mGiveValue = (TextView) view.findViewById(R.id.ribbon_give_value);
    mGetValue = (TextView) view.findViewById(R.id.ribbon_get_value);
  }

  public void setName(String name) {
    mName.setText(name);
  }

  public void setGiveValue(String text) {
    mGiveValue.setText(text);
  }

  public void setGetValue(String text) {
    mGetValue.setText(text);
  }
}
