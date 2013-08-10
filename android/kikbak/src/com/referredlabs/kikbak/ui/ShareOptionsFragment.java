
package com.referredlabs.kikbak.ui;

import java.util.Locale;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.referredlabs.kikbak.R;

public class ShareOptionsFragment extends DialogFragment implements OnClickListener {

  public static final String ARG_MERCHANT_NAME = "merchant";

  private EditText mEmplName;

  public interface OnShareMethodSelectedListener {
    public void onSendViaEmail(String id);

    public void onSendViaFacebook(String id);

    public void onSendViaSms(String id);
  }

  private OnShareMethodSelectedListener mListener;

  public static ShareOptionsFragment newInstance(String merchantName) {
    ShareOptionsFragment dialog = new ShareOptionsFragment();
    Bundle args = new Bundle();
    args.putString(ARG_MERCHANT_NAME, merchantName);
    dialog.setArguments(args);
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
    String subs = getCustomizedSubstitution();
    boolean isCustomized = subs != null;
    int layout = isCustomized ? R.layout.fragment_share_options_customized
        : R.layout.fragment_share_options;
    View v = inflater.inflate(layout, container, false);
    v.findViewById(R.id.via_email).setOnClickListener(this);
    v.findViewById(R.id.via_sms).setOnClickListener(this);
    v.findViewById(R.id.via_facebook).setOnClickListener(this);

    if (isCustomized) {
      mEmplName = (EditText) v.findViewById(R.id.employee_name);
      TextView title = (TextView) v.findViewById(R.id.title);
      String text = getResources().getString(R.string.share_customized_title, subs);
      title.setText(text);
    }
    return v;
  }

  private String getCustomizedSubstitution() {
    String[] names = getResources().getStringArray(R.array.customized_merchants_names);
    String[] subs = getResources().getStringArray(R.array.customized_merchants_subs);

    String merchant = getArguments().getString(ARG_MERCHANT_NAME).toLowerCase(Locale.getDefault());
    for (int i = 0; i < names.length; ++i) {
      if (merchant.contains(names[i]))
        return subs[i];
    }
    return null;
  }

  @Override
  public void onClick(View v) {
    String id = mEmplName != null ? mEmplName.getText().toString() : null;

    switch (v.getId()) {
      case R.id.via_email:
        mListener.onSendViaEmail(id);
        break;
      case R.id.via_facebook:
        mListener.onSendViaFacebook(id);
        break;
      case R.id.via_sms:
        mListener.onSendViaSms(id);
        break;
    }
    dismiss();
  }

}
