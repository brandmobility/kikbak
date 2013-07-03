
package com.referredlabs.kikbak.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.referredlabs.kikbak.R;
import com.referredlabs.kikbak.ui.ConfirmationDialog.ConfirmationListener;

public class RedeemGiftFragment extends Fragment implements OnClickListener, ConfirmationListener {

  private ImageView mFriendPhoto;
  private TextView mFriendName;
  private TextView mFriendComment;

  private TextView mGiftValue;
  private TextView mGiftDesc;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View root = inflater.inflate(R.layout.fragment_redeem_gift, container, false);

    root.findViewById(R.id.scan).setOnClickListener(this);
    root.findViewById(R.id.terms).setOnClickListener(this);
    root.findViewById(R.id.learn_more).setOnClickListener(this);

    mFriendPhoto = (ImageView) root.findViewById(R.id.friend_photo);
    mFriendName = (TextView) root.findViewById(R.id.friend_name);
    mFriendComment = (TextView) root.findViewById(R.id.friend_comment);

    mGiftValue = (TextView) root.findViewById(R.id.gift_value);
    mGiftDesc = (TextView) root.findViewById(R.id.gift_desc);

    return root;
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.scan:
        onScanClicked();
        break;
      case R.id.terms:
        onTermsClicked();
        break;
      case R.id.learn_more:
        onLearnMoreClicked();
        break;
    }
  }

  private void onScanClicked() {
    String msg = getString(R.string.redeem_gift_confirmation);
    ConfirmationDialog dialog = ConfirmationDialog.newInstance(msg);
    dialog.setTargetFragment(this, 0);
    dialog.show(getFragmentManager(), null);
  }

  private void onTermsClicked() {
    String title = getString(R.string.terms_title);
    String msg = getString(R.string.terms_example);
    NoteDialog dialog = NoteDialog.newInstance(title, msg);
    dialog.show(getFragmentManager(), null);
  }

  private void onLearnMoreClicked() {
    String title = getString(R.string.learn_more_title);
    String msg = getString(R.string.terms_example);
    NoteDialog dialog = NoteDialog.newInstance(title, msg);
    dialog.show(getFragmentManager(), null);
  }

  @Override
  public void onYesClick() {
    Toast.makeText(getActivity(), "Not implemented", Toast.LENGTH_SHORT).show();
  }

  @Override
  public void onNoClick() {
    // do nothing
  }

}
