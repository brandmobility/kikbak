
package com.referredlabs.kikbak.ui;

import java.util.HashMap;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.google.gson.Gson;
import com.referredlabs.kikbak.R;
import com.referredlabs.kikbak.data.AvailableCreditType;
import com.referredlabs.kikbak.log.Log;
import com.referredlabs.kikbak.utils.Nearest;
import com.squareup.picasso.Picasso;

public class RedeemGiftCardFragment extends Fragment implements OnClickListener {

  private AvailableCreditType mCredit;
  private TextView mName;
  private ImageView mImage;
  private TextView mRedeemCount;
  private TextView mCreditValue;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    String data = getArguments().getString(RedeemCreditActivity.EXTRA_CREDIT);
    mCredit = new Gson().fromJson(data, AvailableCreditType.class);
    reportSeen();
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View root = inflater.inflate(R.layout.fragment_redeem_gift_card, container, false);

    root.findViewById(R.id.terms).setOnClickListener(this);
    root.findViewById(R.id.claim).setOnClickListener(this);

    mName = (TextView) root.findViewById(R.id.name);
    mImage = (ImageView) root.findViewById(R.id.image);
    mCreditValue = (TextView) root.findViewById(R.id.credit_value);
    mRedeemCount = (TextView) root.findViewById(R.id.redeem_count);

    setupViews();
    return root;
  }

  private void setupViews() {
    mName.setText(mCredit.merchant.name);
    Uri uri = Uri.parse(mCredit.imageUrl);
    Picasso.with(getActivity()).load(uri).into(mImage);
    setCreditAmount(mCredit.value);
    setRedeemCount(mCredit.redeemedGiftsCount);
  }

  private void setCreditAmount(double value) {
    String txt = getString(R.string.gift_card_amount_fmt, value);
    mCreditValue.setText(txt);
  }

  private void setRedeemCount(int count) {
    String txt = getResources().getQuantityString(R.plurals.credit_redeem_count, count, count);
    mRedeemCount.setText(txt);
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.terms:
        onTermsClicked();
        break;
      case R.id.claim:
        onClaimClicked();
        break;
    }
  }

  private void onTermsClicked() {
    String url = mCredit.tosUrl;
    TermsDialog dialog = TermsDialog.newInstance(url);
    dialog.show(getFragmentManager(), null);
  }

  private void onClaimClicked() {
    FragmentManager mgr = getFragmentManager();
    FragmentTransaction t = mgr.beginTransaction();
    t.replace(getId(), new ClaimInputFragmentVerizon());//FIXME
    t.addToBackStack(null);
    t.commit();
  }

  private void reportSeen() {
    HashMap<String, String> map = new HashMap<String, String>();
    map.put(Log.ARG_OFFER_ID, Long.toString(mCredit.offerId));
    float distance = new Nearest(mCredit.merchant.name, mCredit.merchant.locations).getDistance();
    map.put(Log.ARG_DISTANCE, Float.toString(distance));
    FlurryAgent.logEvent(Log.EVENT_CREDIT_SEEN, map);
  }

}
