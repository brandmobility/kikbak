
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
  private TextView mGiveLabel;
  private TextView mGetLabel;

  public OfferHelper(View view, IconBarListener listener) {
    super(view, listener);
    mName = (TextView) view.findViewById(R.id.offer_name);
    mImage = (ImageView) view.findViewById(R.id.offer_image);
    mGiveValue = (TextView) view.findViewById(R.id.ribbon_give_value);
    mGetValue = (TextView) view.findViewById(R.id.ribbon_get_value);
    mGiveLabel = (TextView) view.findViewById(R.id.ribbon_give);
    mGetLabel = (TextView) view.findViewById(R.id.ribbon_get);
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

  public void showGivePart(boolean show) {
    mGiveLabel.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
    mGiveValue.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
  }

  public void showGetPart(boolean show) {
    mGetLabel.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
    mGetValue.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
  }

}
