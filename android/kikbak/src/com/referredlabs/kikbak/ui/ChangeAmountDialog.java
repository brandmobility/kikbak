
package com.referredlabs.kikbak.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.referredlabs.kikbak.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChangeAmountDialog extends DialogFragment implements OnClickListener {

  private static final String ARG_CREDIT = "credit";

  private TextView mCreditAvaiable;
  private EditText mCreditToUse;
  private OnCreditChangedListener mListener;

  public interface OnCreditChangedListener {
    void onCreditChanged(double value);
  }

  public static ChangeAmountDialog newInstance(double credit) {
    ChangeAmountDialog dialog = new ChangeAmountDialog();
    Bundle args = new Bundle();
    args.putDouble(ARG_CREDIT, credit);
    dialog.setArguments(args);
    return dialog;
  }

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    Fragment f = getTargetFragment();
    if (f instanceof OnCreditChangedListener) {
      mListener = (OnCreditChangedListener) f;
    } else {
      mListener = (OnCreditChangedListener) activity;
    }
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    getDialog().setTitle(R.string.change_amount_title);
    View root = inflater.inflate(R.layout.fragment_change_amount, container, false);

    root.findViewById(R.id.apply).setOnClickListener(this);

    mCreditAvaiable = (TextView) root.findViewById(R.id.credit_avaiable);
    mCreditToUse = (EditText) root.findViewById(R.id.credit_to_use);

    double credit = getArguments().getDouble(ARG_CREDIT);
    String amount = getString(R.string.credit_amount_fmt, credit);
    mCreditAvaiable.setText(amount);
    mCreditToUse.setText(String.format("%.2f", credit));
    mCreditToUse.setSelection(mCreditToUse.getText().length());
    mCreditToUse.setFilters(new InputFilter[] {
        new InputFilterCredit(credit)
    });

    // let "done" on soft keyboard works like apply button
    mCreditToUse.setOnEditorActionListener(new OnEditorActionListener() {
      @Override
      public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE)
          onApplyClicked();
        return false;
      }
    });

    // show soft keyboard TODO: phones with HW keyboard ?
    getDialog().getWindow().setSoftInputMode(
        WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    return root;
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.apply:
        onApplyClicked();
        break;
    }
  }

  protected void onApplyClicked() {
    double value = Double.parseDouble(mCreditToUse.getText().toString());
    mListener.onCreditChanged(value);
    dismiss();
  }

  private static class InputFilterCredit implements InputFilter {

    private Pattern mPattern = Pattern.compile("(0|[1-9]+[0-9]*)(\\.[0-9]{0,2})?");
    private double mMaxCredit;

    public InputFilterCredit(double credit) {
      mMaxCredit = credit;
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart,
        int dend) {
      try {
        String result = "" +
            dest.subSequence(0, dstart)
            + source.subSequence(start, end)
            + dest.subSequence(dend, dest.length());

        if (result.isEmpty())
          return null;

        Matcher matcher = mPattern.matcher(result);
        if (!matcher.matches())
          return dest.subSequence(dstart, dend);

        double value = Double.parseDouble(result);
        if (value >= 0 && value <= mMaxCredit)
          return null;
      } catch (NumberFormatException nfe) {
        // ignore
      }
      return dest.subSequence(dstart, dend);
    }
  }
}
