
package com.referredlabs.kikbak.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ViewFlipper;

import com.referredlabs.kikbak.R;
import com.referredlabs.kikbak.data.AvailableCreditType;
import com.referredlabs.kikbak.data.GiftType;
import com.referredlabs.kikbak.store.RewardsLoader;
import com.referredlabs.kikbak.store.TheReward;
import com.referredlabs.kikbak.ui.RedeemChooserDialog.OnRedeemOptionSelectedListener;
import com.referredlabs.kikbak.ui.RewardAdapter.ItemAreaListener;
import com.referredlabs.kikbak.ui.SelectFriend.OnFriendSelectedListener;
import com.referredlabs.kikbak.utils.LocaleUtils;

import java.util.List;

public class RewardListFragment extends Fragment implements OnItemClickListener,
    OnFriendSelectedListener, OnRedeemOptionSelectedListener, LoaderCallbacks<List<TheReward>>,
    OnClickListener, ItemAreaListener {

  public interface OnRedeemListener {
    void onRedeemGift(GiftType gift);

    void onRedeemCredit(AvailableCreditType credit);

    void onShowOffersCliked();
  }

  private ViewFlipper mFlipper;
  private ListView mListView;
  private RewardAdapter mAdapter;
  private OnRedeemListener mListener;
  private TheReward mSelectedReward;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    mListener = (OnRedeemListener) activity;
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_reward_list, container, false);
    mFlipper = (ViewFlipper) view.findViewById(R.id.flipper);
    mListView = (ListView) view.findViewById(R.id.list);
    mListView.setOnItemClickListener(this);
    mAdapter = new RewardAdapter(getActivity(), new IconBarActionHandler(getActivity()), this);
    mListView.setAdapter(mAdapter);

    view.findViewById(R.id.offers).setOnClickListener(this);

    return view;
  }

  @Override
  public void onClick(View v) {
    mListener.onShowOffersCliked();
  }

  public void showList() {
    mFlipper.setDisplayedChild(0);
  }

  public void showEmpty() {
    mFlipper.setDisplayedChild(1);
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    getLoaderManager().initLoader(0, null, this);
  }

  @Override
  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    TheReward reward = (TheReward) mListView.getItemAtPosition(position);

    if (reward.hasGifts() && reward.hasCredit()) {
      selectWhatToRedeem(reward);
    } else if (reward.hasCredit()) {
      redeemCredit(reward.getCredit());
    } else if (reward.hasGifts()) {
      redeemGift(reward.getGifts());
    }
  }

  @Override
  public void onGiftAreaClicked(TheReward reward) {
    if (reward.hasGifts())
      redeemGift(reward.getGifts());
  }

  @Override
  public void onCreditAreaClicked(TheReward reward) {
    if (reward.hasCredit())
      redeemCredit(reward.getCredit());
  }

  private void selectWhatToRedeem(TheReward reward) {
    mSelectedReward = reward;
    String gift = LocaleUtils.getGiftValueString(getActivity(), reward);
    String credit = LocaleUtils.getCreditValueString(getActivity(), reward);
    RedeemChooserDialog dialog = RedeemChooserDialog.newInstance(gift, credit);
    dialog.setTargetFragment(this, 0);
    dialog.show(getFragmentManager(), null);
  }

  @Override
  public void onRedeemGiftSelected() {
    redeemGift(mSelectedReward.getGifts());
  }

  @Override
  public void onRedeemCreditSelected() {
    mListener.onRedeemCredit(mSelectedReward.getCredit());
  }

  private void redeemCredit(AvailableCreditType credit) {
    mListener.onRedeemCredit(credit);
  }

  private void redeemGift(List<GiftType> gifts) {
    if (gifts.size() == 1) {
      redeemGift(gifts.get(0));
    } else {
      selectGiftToRedeem(gifts);
    }
  }

  private void redeemGift(GiftType gift) {
    mListener.onRedeemGift(gift);
  }

  private void selectGiftToRedeem(List<GiftType> gifts) {
    SelectFriend dialog = SelectFriend.newInstance(gifts);
    dialog.setTargetFragment(this, 0);
    dialog.show(getFragmentManager(), null);
  }

  @Override
  public void onFriendSelected(GiftType gift) {
    mListener.onRedeemGift(gift);
  }

  @Override
  public Loader<List<TheReward>> onCreateLoader(int id, Bundle args) {
    RewardsLoader loader = new RewardsLoader(getActivity());
    return loader;
  }

  @Override
  public void onLoadFinished(Loader<List<TheReward>> loader, List<TheReward> result) {
    mAdapter.swap(result);
    RewardsLoader myLoader = (RewardsLoader) loader;
    if (!myLoader.isPending() && result.isEmpty()) {
      showEmpty();
    } else {
      mAdapter.swap(result);
      showList();
    }
  }

  @Override
  public void onLoaderReset(Loader<List<TheReward>> laoder) {
    // ???
  }
}
