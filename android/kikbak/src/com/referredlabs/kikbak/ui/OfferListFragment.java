
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
import com.referredlabs.kikbak.data.ClientOfferType;

public class OfferListFragment extends Fragment implements OnItemClickListener {

  public interface OnOfferClickedListener {
    void onOfferClicked(ClientOfferType offer);
  }

  private ListView mListView;
  private OfferAdapter mAdapter;
  private OnOfferClickedListener mListener;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    mListener = (OnOfferClickedListener) activity;
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_offer_list, container, false);
    mListView = (ListView) view.findViewById(R.id.list);
    mListView.setOnItemClickListener(this);
    mAdapter = new OfferAdapter(getActivity(), new IconBarActionHandler(getActivity()));
    mListView.setAdapter(mAdapter);
    new RefreshOfferTask(1, mAdapter).execute();
    return view;
  }

  @Override
  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    ClientOfferType offer = (ClientOfferType) mListView.getItemAtPosition(position);
    mListener.onOfferClicked(offer);
  }

}
