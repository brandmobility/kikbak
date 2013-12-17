
package com.referredlabs.kikbak.ui;

import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.telephony.PhoneNumberUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.referredlabs.kikbak.R;
import com.referredlabs.kikbak.data.AvailableCreditType;
import com.referredlabs.kikbak.data.ClaimCreditRequest;
import com.referredlabs.kikbak.data.ClaimCreditResponse;
import com.referredlabs.kikbak.data.ClaimType;
import com.referredlabs.kikbak.data.ClientOfferType;
import com.referredlabs.kikbak.http.Http;
import com.referredlabs.kikbak.store.DataStore;
import com.referredlabs.kikbak.tasks.TaskEx;
import com.referredlabs.kikbak.utils.Register;
import com.squareup.picasso.Picasso;

public class ClaimInputFragmentVerizon extends KikbakFragment implements OnClickListener {
  private static final String TAG_INVALID_INFO = "tag_invalid_info";
  private static final String TAG_CLAIM_SUBMITTED = "tag_claim_submitted";

  private AvailableCreditType mCredit;

  private TextView mRewardValueTv;
  private TextView mCompanyNameTv;
  private ImageView mImage;
  private Button mButton;

  private TextView mPhoneTv;

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    String data = activity.getIntent().getStringExtra(RedeemCreditActivity.EXTRA_CREDIT);
    mCredit = new Gson().fromJson(data, AvailableCreditType.class);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View root = inflater.inflate(R.layout.fragment_claim_reward_verizon, container, false);

    mButton = (Button) root.findViewById(R.id.button_submit);
    mButton.setOnClickListener(this);

    mRewardValueTv = (TextView) root.findViewById(R.id.reward_value);
    mCompanyNameTv = (TextView) root.findViewById(R.id.company_name);
    mImage = (ImageView) root.findViewById(R.id.image);

    mPhoneTv = (TextView) root.findViewById(R.id.phone_number);

    setupViews();

    return root;
  }

  private void setupViews() {
    mCompanyNameTv.setText(mCredit.merchant.name);
    Uri uri = Uri.parse(mCredit.imageUrl);
    Picasso.with(getActivity()).load(uri).into(mImage);
    setCreditAmount(mCredit.value);
  }

  private void setCreditAmount(double value) {
    String txt = getString(R.string.gift_card_amount_fmt, value);
    mRewardValueTv.setText(txt);
  }

  void onClaimSuccess() {
    DataStore.getInstance().creditUsed(mCredit.id, mCredit.value);
    showClaimSubmittedPopup();
  }

  void onClaimFailed() {
    showInvalidInfoPopup();
    mButton.setEnabled(true);
  }

  @Override
  public void onClick(View v) {
    if (validateData()) {
      mButton.setEnabled(false);
      resetTask();
      mTask = new ClaimTask();
      mTask.execute();
    }
  }

  private boolean validateData() {
    boolean isOk = true;
    isOk &= validateNotEmpty(mPhoneTv, R.string.claim_phone_empty)
        && validatePhone(mPhoneTv, R.string.claim_phone_wrong_format);
    return isOk;
  }

  boolean validateNotEmpty(TextView tv, int error) {
    if (tv.getText().toString().trim().length() == 0) {
      tv.setError(getString(error));
      tv.requestFocus();
      return false;
    }
    tv.setError(null);
    return true;
  }

  boolean validatePhone(TextView tv, int error) {
    if (!PhoneNumberUtils.isGlobalPhoneNumber(tv.getText().toString())) {
      tv.setError(getString(R.string.claim_phone_wrong_format));
      tv.requestFocus();
      return false;
    }
    tv.setError(null);
    return true;
  }

  private void showInvalidInfoPopup() {
    DialogFragment popup = new InvalidInfoPopup();
    popup.show(getChildFragmentManager(), TAG_INVALID_INFO);
  }

  private void showClaimSubmittedPopup() {
    DialogFragment popup = ClaimSubmittedPopup.newInstance(mCredit.offerId);
    popup.show(getChildFragmentManager(), TAG_CLAIM_SUBMITTED);
  }

  public static class InvalidInfoPopup extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
      return new AlertDialog.Builder(getActivity())
          .setMessage(R.string.claim_failed)
          .setPositiveButton(R.string.claim_failed_retry, null)
          .create();
    }
  }

  public static class ClaimSubmittedPopup extends DialogFragment implements OnClickListener {
    ClaimInputFragment.ClaimCompletedCallback mCallback;

    private static final String OFFER_ID = "offerId";
    private long mOfferId;

    public static ClaimSubmittedPopup newInstance(long offerId) {
      ClaimSubmittedPopup popup = new ClaimSubmittedPopup();
      Bundle args = new Bundle();
      args.putLong(OFFER_ID, offerId);
      popup.setArguments(args);
      return popup;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setStyle(STYLE_NO_TITLE, 0);
      mOfferId = getArguments().getLong(OFFER_ID);
    }

    @Override
    public void onAttach(Activity activity) {
      super.onAttach(activity);
      mCallback = (ClaimInputFragment.ClaimCompletedCallback) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
      View root = inflater.inflate(R.layout.fragment_claim_success, container, false);
      root.findViewById(R.id.done).setOnClickListener(this);
      root.findViewById(R.id.give).setOnClickListener(this);
      if (DataStore.getInstance().getOffer(mOfferId) == null) {
        root.findViewById(R.id.give).setVisibility(View.GONE);
      }
      return root;
    }

    @Override
    public void onClick(View v) {
      if (v.getId() == R.id.give) {
        showShareGift();
      }

      dismiss();
    }

    private void showShareGift() {
      ClientOfferType offer = DataStore.getInstance().getOffer(mOfferId);
      if (offer != null) {
        Intent intent = new Intent(getActivity(), GiveActivity.class);
        Gson gson = new Gson();
        String data = gson.toJson(offer);
        intent.putExtra(GiveActivity.ARG_OFFER, data);
        startActivity(intent);
      }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
      super.onDismiss(dialog);
      mCallback.onClaimCompleted();
    }
  }

  private class ClaimTask extends TaskEx {

    private ClaimCreditRequest mRequest;
    private long mUserId;

    ClaimTask() {
      mRequest = new ClaimCreditRequest();
      mRequest.claim = new ClaimType();
      mRequest.claim.creditId = mCredit.id;
      mRequest.claim.phoneNumber =
          PhoneNumberUtils.stripSeparators(mPhoneTv.getText().toString());

      mUserId = Register.getInstance().getUserId();
    }

    @Override
    protected void doInBackground() throws IOException {
      String uri = Http.getUri(ClaimCreditRequest.PATH + mUserId + "/");
      Http.execute(uri, mRequest, ClaimCreditResponse.class);
    }

    @Override
    protected void onSuccess() {
      onClaimSuccess();
    }

    @Override
    protected void onFailed(Exception exception) {
      onClaimFailed();
    }
  }
}
