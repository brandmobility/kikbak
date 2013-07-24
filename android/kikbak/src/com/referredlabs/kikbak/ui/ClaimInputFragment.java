
package com.referredlabs.kikbak.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.referredlabs.kikbak.R;
import com.referredlabs.kikbak.data.AvailableCreditType;
import com.referredlabs.kikbak.data.ClaimCreditRequest;
import com.referredlabs.kikbak.data.ClaimCreditResponse;
import com.referredlabs.kikbak.data.ClaimType;
import com.referredlabs.kikbak.http.Http;
import com.referredlabs.kikbak.utils.Register;

public class ClaimInputFragment extends Fragment implements OnClickListener {
  private static final String TAG_INVALID_INFO = "tag_invalid_info";
  private static final String TAG_CLAIM_SUBMITTED = "tag_claim_submitted";

  private AvailableCreditType mCredit;

  private String mRewardDescription = "Gift Card";
  private String mCompanyName = "Verizon Wireless";

  private TextView mRewardValueTv;
  private TextView mRewardDescriptionTv;
  private TextView mCompanyNameTv;

  private TextView mPhoneTv;
  private TextView mNameTv;
  private TextView mStreetTv;
  private TextView mAptTv;
  private TextView mCityTv;
  private TextView mStateTv;
  private TextView mZipTv;

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    String data = activity.getIntent().getStringExtra(RedeemCreditActivity.EXTRA_CREDIT);
    mCredit = new Gson().fromJson(data, AvailableCreditType.class);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View root = inflater.inflate(R.layout.fragment_claim_reward, container, false);

    root.findViewById(R.id.button_submit).setOnClickListener(this);

    mRewardValueTv = (TextView) root.findViewById(R.id.reward_value);
    mRewardDescriptionTv = (TextView) root.findViewById(R.id.reward_description);
    mCompanyNameTv = (TextView) root.findViewById(R.id.company_name);

    mPhoneTv = (TextView) root.findViewById(R.id.phone_number);
    mNameTv = (TextView) root.findViewById(R.id.first_last_name);
    mStreetTv = (TextView) root.findViewById(R.id.address_street);
    mAptTv = (TextView) root.findViewById(R.id.address_apartment);
    mCityTv = (TextView) root.findViewById(R.id.address_city);
    mStateTv = (TextView) root.findViewById(R.id.address_state);
    mZipTv = (TextView) root.findViewById(R.id.address_zip);

    setupViews();

    return root;
  }

  private void setupViews() {
    setCreditAmount(mCredit.value);
    mRewardDescriptionTv.setText(mRewardDescription);
    mCompanyNameTv.setText(mCompanyName);
  }

  private void setCreditAmount(double value) {
    String txt = getString(R.string.credit_amount_fmt, value);
    mRewardValueTv.setText(txt);
  }

  void onClaimSuccess() {
    showClaimSubmittedPopup();

  }

  void onClaimFailed() {
    showInvalidInfoPopup();
  }

  @Override
  public void onClick(View v) {
    new ClaimTask().execute();
  }

  private void showInvalidInfoPopup() {
    DialogFragment popup = new InvalidInfoPopup();
    popup.show(getChildFragmentManager(), TAG_INVALID_INFO);
  }

  private void showClaimSubmittedPopup() {
    DialogFragment popup = new ClaimSubmittedPopup();
    popup.show(getChildFragmentManager(), TAG_CLAIM_SUBMITTED);
  }

  public static class InvalidInfoPopup extends DialogFragment {
    String message = "Sorry, the information you entered is not recognized as valid."
        + "\n\nPlease ensure that it is accurate and try again.";

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
      return new AlertDialog.Builder(getActivity())
          .setMessage(message)
          .setPositiveButton("Retry", null)
          .create();
    }
  }

  public static class ClaimSubmittedPopup extends DialogFragment {
    String message = "Your reward claim has been submitted.";
    RedeemSuccessCallback mCallback;

    @Override
    public void onAttach(Activity activity) {
      super.onAttach(activity);
      mCallback = (RedeemSuccessCallback) activity;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
      return new AlertDialog.Builder(getActivity())
          .setTitle("Success!")
          .setMessage(message)
          .setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
              mCallback.finished();
            }
          })
          .setPositiveButton(android.R.string.ok, null)
          .create();
    }
  }

  private class ClaimTask extends AsyncTask<Void, Void, Void> {

    private ClaimCreditRequest mRequest;
    ClaimCreditResponse mResponse;
    private long mUserId;

    @Override
    protected void onPreExecute() {
      mRequest = new ClaimCreditRequest();
      mRequest.claim = new ClaimType();
      mRequest.claim.creditId = mCredit.id;
      mRequest.claim.PhoneNumber = mPhoneTv.getText().toString();
      mRequest.claim.name = mNameTv.getText().toString();
      mRequest.claim.street = mStreetTv.getText().toString();
      mRequest.claim.apt = mAptTv.getText().toString();;
      mRequest.claim.city = mCityTv.getText().toString();
      mRequest.claim.state = mStateTv.getText().toString();
      mRequest.claim.zipcode = mZipTv.getText().toString();

      mUserId = Register.getInstance().getUserId();
    }

    @Override
    protected Void doInBackground(Void... params) {

      try {
        String uri = Http.getUri(ClaimCreditRequest.PATH + mUserId + "/");
        mResponse = Http.execute(uri, mRequest, ClaimCreditResponse.class);
      } catch (Exception e) {
        Log.d("MMM", "Exception e");
      }
      return null;
    }

    @Override
    protected void onPostExecute(Void result) {
      if (mResponse != null && mResponse.status.code == 0) {
        onClaimSuccess();
      } else {
        onClaimFailed();
      }
    }
  }
}
