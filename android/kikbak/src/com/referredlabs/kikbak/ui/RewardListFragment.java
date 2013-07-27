
package com.referredlabs.kikbak.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.referredlabs.kikbak.R;
import com.referredlabs.kikbak.store.RewardsLoader;
import com.referredlabs.kikbak.store.TheReward;

import java.util.List;

public class RewardListFragment extends Fragment implements OnItemClickListener,
    LoaderCallbacks<List<TheReward>> {

  public interface OnRewardClickedListener {
    void onRewardClicked(TheReward offer);
  }

  private ListView mListView;
  private RewardAdapter mAdapter;
  private OnRewardClickedListener mListener;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    mListener = (OnRewardClickedListener) activity;
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_reward_list, container, false);
    mListView = (ListView) view.findViewById(R.id.list);
    mListView.setOnItemClickListener(this);
    mAdapter = new RewardAdapter(getActivity(), new IconBarActionHandler(getActivity()));
    mListView.setAdapter(mAdapter);
    return view;
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    getLoaderManager().initLoader(0, null, this);
  }

  @Override
  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    TheReward reward = (TheReward) mListView.getItemAtPosition(position);
    mListener.onRewardClicked(reward);
  }

  @Override
  public Loader<List<TheReward>> onCreateLoader(int id, Bundle args) {
    RewardsLoader loader = new RewardsLoader(getActivity());
    return loader;
  }

  @Override
  public void onLoadFinished(Loader<List<TheReward>> loader, List<TheReward> result) {
    mAdapter.swap(result);
  }

  @Override
  public void onLoaderReset(Loader<List<TheReward>> laoder) {
    // ???
  }

}
