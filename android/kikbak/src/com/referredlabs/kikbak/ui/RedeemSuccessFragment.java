
package com.referredlabs.kikbak.ui;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.referredlabs.kikbak.R;
import com.referredlabs.kikbak.data.AvailableCreditType;
import com.referredlabs.kikbak.data.GiftType;

public class RedeemSuccessFragment extends Fragment implements OnClickListener {

  private GiftType mGift;
  private AvailableCreditType mCredit;

  private Bitmap mBarcodeBitmap;
  private String mBarcode;

  private TextView mName;
  private View mSuccessFrame;
  private TextView mNote;
  private TextView mTypeLabel;
  private TextView mValue;
  private TextView mDesc;
  private TextView mCode;
  private ImageView mQrCode;
  private Button mGive;

  public static RedeemSuccessFragment newInstance() {
    RedeemSuccessFragment fragment = new RedeemSuccessFragment();
    return fragment;
  }

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    Bundle args = getActivity().getIntent().getExtras();
    String data = args.getString(SuccessActivity.ARG_GIFT);
    mGift = new Gson().fromJson(data, GiftType.class);

    data = args.getString(SuccessActivity.ARG_CREDIT);
    mCredit = new Gson().fromJson(data, AvailableCreditType.class);

    mBarcodeBitmap = args.getParcelable(SuccessActivity.ARG_BARCODE_BITMAP);
    mBarcode = args.getString(SuccessActivity.ARG_BARCODE);
    assert ((mGift == null && mCredit != null) || (mGift != null && mCredit == null));
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View root = inflater.inflate(R.layout.fragment_redeem_success, container, false);
    mSuccessFrame = root.findViewById(R.id.success_frame);
    mName = (TextView) root.findViewById(R.id.name);
    mNote = (TextView) root.findViewById(R.id.redeem_note);
    mTypeLabel = (TextView) root.findViewById(R.id.redeem_type_label);
    mValue = (TextView) root.findViewById(R.id.redeem_value);
    mDesc = (TextView) root.findViewById(R.id.redeem_desc);
    mCode = (TextView) root.findViewById(R.id.code);
    mQrCode = (ImageView) root.findViewById(R.id.qr_code);
    mGive = (Button) root.findViewById(R.id.give);
    mGive.setOnClickListener(this);
    setupViews();
    return root;
  }

  private void setupViews() {
    if (mGift != null)
      setupForGift();
    else
      setupForCredit();

    Bitmap bmp = mBarcodeBitmap;
    if (bmp == null) {
      bmp = generateQrCode(mBarcode);
    }
    mQrCode.setImageBitmap(bmp);
    mCode.setText(mBarcode);
  }

  private void setupForGift() {
    mSuccessFrame.setBackgroundColor(getResources().getColor(R.color.success_gift_background));
    mNote.setText(R.string.redeem_success_gift_note);
    mTypeLabel.setText(R.string.redeem_success_offer);
  }

  private void setupForCredit() {
    mSuccessFrame.setBackgroundColor(getResources().getColor(R.color.success_credit_background));
    mNote.setText(R.string.redeem_success_credit_note);
    mTypeLabel.setText(R.string.redeem_success_credit);
  }

  private Bitmap generateQrCode(String code) {
    try {
      QRCodeWriter writer = new QRCodeWriter();
      BitMatrix result = writer.encode(code, BarcodeFormat.QR_CODE, 300, 300);
      int width = result.getWidth();
      int height = result.getHeight();
      int[] pixels = new int[width * height];
      for (int y = 0; y < height; y++) {
        int offset = y * width;
        for (int x = 0; x < width; x++) {
          pixels[offset + x] = result.get(x, y) ? 0xFF000000 : 0xFFFFFFFF;
        }
      }
      Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
      bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
      return bitmap;
    } catch (Exception e) {
      Log.e("MMM", "generating failed");
    }
    return null;
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
    Toast.makeText(getActivity(), "Not implemented", Toast.LENGTH_SHORT).show();
  }
}
