
package com.referredlabs.kikbak.ui;

import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.referredlabs.kikbak.R;

public class ShareOptionsFragment extends DialogFragment implements OnClickListener {

  public interface OnShareMethodSelectedListener {
    public void onSendViaEmail();

    public void onSendViaFacebook();

    public void onPostOnFacebook();
  }

  private OnShareMethodSelectedListener mListener;

  public static ShareOptionsFragment newInstance() {
    ShareOptionsFragment dialog = new ShareOptionsFragment();
    return dialog;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setStyle(STYLE_NO_TITLE, 0);
  }

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    mListener = (OnShareMethodSelectedListener) activity;
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View v = inflater.inflate(R.layout.fragment_share_options, container, false);
    v.findViewById(R.id.email_send_to_friends).setOnClickListener(this);
    v.findViewById(R.id.facebook_post_on_timeline).setOnClickListener(this);
    v.findViewById(R.id.facebook_send_to_friends).setOnClickListener(this);
    return v;
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.email_send_to_friends:
        mListener.onSendViaEmail();
        break;
      case R.id.facebook_send_to_friends:
        mListener.onSendViaFacebook();
        break;
      case R.id.facebook_post_on_timeline:
        mListener.onPostOnFacebook();
        break;
    }
    dismiss();
  }

}
