
package com.referredlabs.kikbak.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
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

  private String mBarcode;

  private TextView mName;
  private TextView mValue;
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

    mBarcode = args.getString(SuccessActivity.ARG_BARCODE);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View root = inflater.inflate(R.layout.fragment_redeem_credit_success, container, false);
    mName = (TextView) root.findViewById(R.id.name);
    mValue = (TextView) root.findViewById(R.id.redeem_value);
    mGive = (Button) root.findViewById(R.id.give);
    mGive.setOnClickListener(this);

    setupCreditViews();
    setupBarcodeViews(root);
    return root;
  }

  private void setupCreditViews() {
    mName.setText(mCredit.merchant.name);
    double value = mCredit.value;
    if (mCreditUsed > 0) {
      value = mCreditUsed;
    }

    mValue.setText(String.format("$%.2f", value));
  }

  private void setupBarcodeViews(View root) {
    ViewStub stub = (ViewStub) root.findViewById(R.id.barcode_area);
    int layout = getBarcodeLayout();
    if (layout != 0) {
      stub.setLayoutResource(layout);
      root = stub.inflate();
      if (mCredit.validationType == ValidationType.barcode) {
        Bitmap bmp = BarcodeGenerator.generateUpcaCode(getActivity(), mBarcode);
        ImageView img = (ImageView) root.findViewById(R.id.code);
        TextView text = (TextView) root.findViewById(R.id.text_code);
        img.setImageBitmap(bmp);
        text.setText(mBarcode);
      } else if (mCredit.validationType == ValidationType.qrcode && !TextUtils.isEmpty(mBarcode)) {
        Bitmap bmp = BarcodeGenerator.generateQrCode(getActivity(), mBarcode);
        ImageView img = (ImageView) root.findViewById(R.id.code);
        TextView text = (TextView) root.findViewById(R.id.text_code);
        img.setImageBitmap(bmp);
        text.setText(mBarcode);
      }
    }
  }

  private int getBarcodeLayout() {
    switch (mCredit.validationType) {
      case barcode:
        return R.layout.fragment_redeem_success_barcode;

      case qrcode:
        return R.layout.fragment_redeem_success_qrcode;

      default:
        return 0;
    }
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
