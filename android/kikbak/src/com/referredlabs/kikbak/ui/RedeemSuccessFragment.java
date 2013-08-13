
package com.referredlabs.kikbak.ui;

import java.util.EnumMap;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.referredlabs.kikbak.Kikbak;
import com.referredlabs.kikbak.R;
import com.referredlabs.kikbak.data.AvailableCreditType;
import com.referredlabs.kikbak.data.ClientOfferType;
import com.referredlabs.kikbak.data.GiftType;
import com.referredlabs.kikbak.data.ValidationType;
import com.referredlabs.kikbak.store.DataStore;

public class RedeemSuccessFragment extends Fragment implements OnClickListener {

  private GiftType mGift;
  private AvailableCreditType mCredit;

  private Bitmap mBarcodeBitmap;
  private String mBarcode;

  private TextView mName;
  private View mSuccessFrame;
  private TextView mTitle;
  private TextView mNoteFirst;
  private TextView mNoteSecond;
  private TextView mValue;
  private TextView mDesc;
  private ImageView mCode;
  private TextView mTextCode;
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
    mName = (TextView) root.findViewById(R.id.name);
    mSuccessFrame = root.findViewById(R.id.success_frame);
    mTitle = (TextView) root.findViewById(R.id.title);
    mNoteFirst = (TextView) root.findViewById(R.id.redeem_note_first);
    mNoteSecond = (TextView) root.findViewById(R.id.redeem_note_second);
    mValue = (TextView) root.findViewById(R.id.redeem_value);
    mDesc = (TextView) root.findViewById(R.id.redeem_desc);
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
    String validationType = mGift != null ? mGift.validationType : mCredit.validationType;
    return ValidationType.BARCODE.equals(validationType) ? R.layout.fragment_redeem_success_barcode
        : R.layout.fragment_redeem_success_qrcode;
  }

  private void setupViews() {
    if (mGift != null)
      setupGiftViews();
    else
      setupCreditViews();

    Bitmap bmp = mBarcodeBitmap;
    if (true || bmp == null) {
      bmp = generateQrCode(mBarcode);
    }
    mCode.setImageBitmap(bmp);
    mTextCode.setText(mBarcode);
  }

  private void setupGiftViews() {
    mName.setText(mGift.merchant.name);
    mSuccessFrame.setBackgroundColor(getResources().getColor(R.color.success_gift_background));

    int color = getResources().getColor(R.color.success_gift_text);
    mTitle.setTextColor(color);
    mNoteFirst.setTextColor(color);
    mNoteSecond.setTextColor(color);

    mNoteFirst.setText(R.string.redeem_success_note_first_gift);
    if (ValidationType.QRCODE.equals(mGift.validationType))
      mNoteSecond.setText(R.string.redeem_success_note_second);
    else
      mNoteSecond.setText(R.string.redeem_success_note_second_integrated);
    mValue.setText(mGift.desc);
    mDesc.setText(mGift.detailedDesc);
  }

  private void setupCreditViews() {
    mName.setText(mCredit.merchant.name);
    mSuccessFrame.setBackgroundColor(getResources().getColor(R.color.success_credit_background));

    int color = getResources().getColor(R.color.success_credit_text);
    mTitle.setTextColor(color);
    mNoteFirst.setTextColor(color);
    mNoteSecond.setTextColor(color);

    mNoteFirst.setText(R.string.redeem_success_note_first_credit);
    mNoteSecond.setText(R.string.redeem_success_note_second);
    mValue.setText(mCredit.desc);
    mDesc.setText(mCredit.detailedDesc);
  }

  private Bitmap generateQrCode(String code) {
    try {
      int size = getResources().getDimensionPixelSize(R.dimen.qr_code_size);
      Map<EncodeHintType, Object> hints = new EnumMap<EncodeHintType, Object>(EncodeHintType.class);
      hints.put(EncodeHintType.MARGIN, 0);
      QRCodeWriter writer = new QRCodeWriter();
      BitMatrix result = writer.encode(code, BarcodeFormat.QR_CODE, size, size, hints);
      int width = result.getWidth();
      int height = result.getHeight();
      int[] pixels = new int[width * height];
      for (int y = 0; y < height; y++) {
        int offset = y * width;
        for (int x = 0; x < width; x++) {
          pixels[offset + x] = result.get(x, y) ? 0xFF333333 : 0xFFFFFFFF;
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
    getActivity().finish();
    long id = mGift != null ? mGift.offerId : mCredit.offerId;
    ClientOfferType offer = DataStore.getInstance().getOffer(id);
    if (offer != null) {
      Intent intent = new Intent(getActivity(), GiveActivity.class);
      Gson gson = new Gson();
      String data = gson.toJson(offer);
      intent.putExtra(GiveActivity.ARG_OFFER, data);
      startActivity(intent);
    }
  }
}
