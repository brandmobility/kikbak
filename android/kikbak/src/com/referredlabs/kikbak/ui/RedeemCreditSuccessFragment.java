
package com.referredlabs.kikbak.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.referredlabs.kikbak.R;
import com.referredlabs.kikbak.data.AvailableCreditType;
import com.referredlabs.kikbak.data.ClientOfferType;
import com.referredlabs.kikbak.data.ValidationType;
import com.referredlabs.kikbak.store.DataStore;
import com.referredlabs.kikbak.utils.BarcodeGenerator;

public class RedeemCreditSuccessFragment extends Fragment implements OnClickListener {

  private AvailableCreditType mCredit;
  private double mCreditUsed;

  private Bitmap mBarcodeBitmap;
  private String mBarcode;

  private TextView mName;
  private TextView mValue;
  private ImageView mCode;
  private TextView mTextCode;
  private Button mGive;

  public static RedeemCreditSuccessFragment newInstance() {
    RedeemCreditSuccessFragment fragment = new RedeemCreditSuccessFragment();
    return fragment;
  }

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    Bundle args = getActivity().getIntent().getExtras();

    String data = args.getString(SuccessActivity.ARG_CREDIT);
    mCredit = new Gson().fromJson(data, AvailableCreditType.class);
    mCreditUsed = args.getDouble(SuccessActivity.ARG_CREDIT_USED, -1);

    mBarcodeBitmap = args.getParcelable(SuccessActivity.ARG_BARCODE_BITMAP);
    mBarcode = args.getString(SuccessActivity.ARG_BARCODE);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View root = inflater.inflate(R.layout.fragment_redeem_credit_success, container, false);
    mName = (TextView) root.findViewById(R.id.name);
    mValue = (TextView) root.findViewById(R.id.redeem_value);
    mGive = (Button) root.findViewById(R.id.give);
    mGive.setOnClickListener(this);

    ViewStub stub = (ViewStub) root.findViewById(R.id.barcode_area);
    stub.setLayoutResource(getBarcodeLayout());
    stub.inflate();

    mCode = (ImageView) root.findViewById(R.id.code);
    mTextCode = (TextView) root.findViewById(R.id.text_code);

    setupViews();
    return root;
  }

  private int getBarcodeLayout() {
    return mCredit.validationType == ValidationType.barcode ? R.layout.fragment_redeem_success_barcode
        : R.layout.fragment_redeem_success_qrcode;
  }

  private void setupViews() {
    setupCreditViews();

    Bitmap bmp = mBarcodeBitmap;
    if (bmp == null) {
      bmp = BarcodeGenerator.generateQrCode(getActivity(), mBarcode);
    }
    mCode.setImageBitmap(bmp);
    mTextCode.setText(mBarcode);
  }

  private void setupCreditViews() {
    mName.setText(mCredit.merchant.name);
    double value = mCredit.value;
    if (mCreditUsed > 0) {
      value = mCreditUsed;
    }

    mValue.setText(String.format("$%.2f", value));
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.give:
        onGiveClicked();
        break;
    }
  }

  private void onGiveClicked() {
    getActivity().finish();
    ClientOfferType offer = DataStore.getInstance().getOffer(mCredit.offerId);
    if (offer != null) {
      Intent intent = new Intent(getActivity(), GiveActivity.class);
      Gson gson = new Gson();
      String data = gson.toJson(offer);
      intent.putExtra(GiveActivity.ARG_OFFER, data);
      startActivity(intent);
    }
  }
}
