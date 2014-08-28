
package com.referredlabs.kikbak.ui;

import java.util.Date;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.referredlabs.kikbak.R;
import com.referredlabs.kikbak.data.ClientOfferType;
import com.referredlabs.kikbak.data.GiftType;
import com.referredlabs.kikbak.data.ValidationType;
import com.referredlabs.kikbak.store.DataStore;
import com.referredlabs.kikbak.utils.BarcodeGenerator;

public class RedeemGiftSuccessFragment extends Fragment implements OnClickListener {

  private GiftType mGift;

  private String mBarcode;
  private boolean mIsInStore;

  private TextView mName;
  private TextView mValue;
  private TextView mDesc;
  private Button mGive;
  private Button mUseOnline;

  public static RedeemGiftSuccessFragment newInstance() {
    RedeemGiftSuccessFragment fragment = new RedeemGiftSuccessFragment();
    return fragment;
  }

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    Bundle args = getActivity().getIntent().getExtras();
    String data = args.getString(SuccessActivity.ARG_GIFT);
    mGift = new Gson().fromJson(data, GiftType.class);
    mBarcode = args.getString(SuccessActivity.ARG_BARCODE);
    mIsInStore = args.getBoolean(SuccessActivity.ARG_IN_STORE, false);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View root = inflater.inflate(R.layout.fragment_redeem_gift_success, container, false);
    mName = (TextView) root.findViewById(R.id.name);
    mValue = (TextView) root.findViewById(R.id.redeem_value);
    mDesc = (TextView) root.findViewById(R.id.redeem_desc);
    mDesc.setPaintFlags(mDesc.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
    mDesc.setOnClickListener(this);
    mGive = (Button) root.findViewById(R.id.give);
    mGive.setOnClickListener(this);
    mUseOnline = (Button) root.findViewById(R.id.use_online);
    mUseOnline.setOnClickListener(this);

    setupGiftViews();
    setupBarcodeViews(root);

    if (!mIsInStore) {
      ((TextView) root.findViewById(R.id.redeem_success_message))
          .setText(R.string.redeem_gift_success_note_first_online);
      mUseOnline.setVisibility(View.VISIBLE);

      root.findViewById(R.id.give_note).setVisibility(View.GONE);
      mGive.setVisibility(View.GONE);
    }

    return root;
  }

  private void setupGiftViews() {
    mName.setText(mGift.merchant.name);
    // mValue.setText(LocaleUtils.getGiftValueString(getActivity(), mGift));
    mValue.setText(mGift.desc);
    mDesc.setText(mGift.detailedDesc);
  }

  private void setupBarcodeViews(View root) {
    ViewStub stub = (ViewStub) root.findViewById(R.id.barcode_area);
    int layout = getBarcodeLayout();
    if (layout != 0) {
      stub.setLayoutResource(layout);
      root = stub.inflate();
      if (mGift.validationType == ValidationType.barcode) {
        if (mIsInStore) {
          Bitmap bmp = BarcodeGenerator.generateCode128(getActivity(), mBarcode);
          ImageView img = (ImageView) root.findViewById(R.id.code);
          img.setImageBitmap(bmp);
        }
        TextView text = (TextView) root.findViewById(R.id.text_code);
        TextView exp = (TextView) root.findViewById(R.id.expiration);
        text.setText(mBarcode);
        exp.setText(getExpirationString());
        if (!mIsInStore) {
          root.findViewById(R.id.copy).setOnClickListener(this);
        }
      } else if (mGift.validationType == ValidationType.qrcode && !TextUtils.isEmpty(mBarcode)) {
        Bitmap bmp = BarcodeGenerator.generateQrCode(getActivity(), mBarcode);
        ImageView img = (ImageView) root.findViewById(R.id.code);
        TextView text = (TextView) root.findViewById(R.id.text_code);
        TextView exp = (TextView) root.findViewById(R.id.expiration);
        img.setImageBitmap(bmp);
        text.setText(mBarcode);
        exp.setText(getExpirationString());
      }
    }
  }

  private String getExpirationString() {
    ClientOfferType offer = DataStore.getInstance().getOffer(mGift.offerId);
    if (offer != null) {
      String date = DateFormat.getMediumDateFormat(getActivity()).format(new Date(offer.endDate));
      return getString(R.string.offer_expires, date);
    }
    return null;
  }

  private int getBarcodeLayout() {
    if (mIsInStore) {
      switch (mGift.validationType) {
        case barcode:
          return R.layout.fragment_redeem_success_barcode;

        case qrcode:
          return R.layout.fragment_redeem_success_qrcode;

        default:
          return 0;
      }
    } else {
      return R.layout.fragment_redeem_success_textcode;
    }
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.give:
        onGiveClicked();
        break;
      case R.id.use_online:
        onUseOnlineClicked();
        break;
      case R.id.copy:
        onCopyClicked();
        break;
      case R.id.redeem_desc:
        onTermsClicked();
        break;
    }
  }

  private void onGiveClicked() {
    getActivity().finish();
    ClientOfferType offer = DataStore.getInstance().getOffer(mGift.offerId);
    if (offer != null) {
      Intent intent = new Intent(getActivity(), GiveActivity.class);
      Gson gson = new Gson();
      String data = gson.toJson(offer);
      intent.putExtra(GiveActivity.ARG_OFFER, data);
      startActivity(intent);
    }
  }

  private void onUseOnlineClicked() {
    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mGift.merchant.url));
    startActivity(intent);
  }

  @SuppressLint("NewApi")
  @SuppressWarnings("deprecation")
  private void onCopyClicked() {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
      android.text.ClipboardManager clipboard = (android.text.ClipboardManager) getActivity()
          .getSystemService(Context.CLIPBOARD_SERVICE);
      clipboard.setText(mBarcode);
    } else {
      ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(
          Context.CLIPBOARD_SERVICE);
      ClipData clip = ClipData.newPlainText(getString(R.string.coupon_code_clip), mBarcode);
      clipboard.setPrimaryClip(clip);
    }
    Toast.makeText(getActivity(), R.string.copied_to_clipboard, Toast.LENGTH_SHORT).show();
  }

  private void onTermsClicked() {
    String url = mGift.tosUrl;
    TermsDialog dialog = TermsDialog.newInstance(url);
    dialog.show(getFragmentManager(), null);
  }

}
