
package com.referredlabs.kikbak.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.referredlabs.kikbak.R;

public class RewardListFragment extends Fragment implements OnItemClickListener {

  public interface OnRewardClickedListener {
    void onRewardClicked(Reward offer);
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
    mAdapter = new RewardAdapter(getActivity());
    mListView.setAdapter(mAdapter);
    new RefreshRewardTask(1, mAdapter).execute();
    return view;
  }

  @Override
  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    Reward reward = (Reward) mListView.getItemAtPosition(position);
    mListener.onRewardClicked(reward);
  }

}
