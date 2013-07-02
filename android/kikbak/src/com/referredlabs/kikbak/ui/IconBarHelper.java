
package com.referredlabs.kikbak.ui;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.referredlabs.kikbak.R;
import com.referredlabs.kikbak.utils.Distance;

public class IconBarHelper implements OnClickListener {

  protected View mRootView;
  protected TextView mDistance;
  protected View mPhoneIcon;
  protected View mWebIcon;
  protected String mUrl;
  protected String mPhone;

  public IconBarHelper(View root) {
    mRootView = root;

    mDistance = (TextView) root.findViewById(R.id.distance);
    mDistance.setOnClickListener(this);

    mPhoneIcon = root.findViewById(R.id.phone_icon);
    mPhoneIcon.setOnClickListener(this);

    mWebIcon = root.findViewById(R.id.web_icon);
    mWebIcon.setOnClickListener(this);
  }

  public void setLink(String url) {
    mUrl = url;
  }

  public void setPhone(String phone) {
    mPhone = phone;
  }

  public void setDistance(float distance, Object location) {
    String txt = Distance.getLocalizedDistance(mRootView.getContext(), distance);
    mDistance.setText(txt);
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.web_icon:
        onWebIconClicked();
        break;
      case R.id.phone_icon:
        onPhoneIconClicked();
        break;
      case R.id.distance:
        onMapIconClicked();
        break;
    }
  }

  protected void onMapIconClicked() {
  }

  protected void onPhoneIconClicked() {
  }

  protected void onWebIconClicked() {
  }
}
