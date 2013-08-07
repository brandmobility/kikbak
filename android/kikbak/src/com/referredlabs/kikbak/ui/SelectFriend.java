
package com.referredlabs.kikbak.ui;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.referredlabs.kikbak.R;
import com.referredlabs.kikbak.data.GiftType;
import com.referredlabs.kikbak.fb.Fb;
import com.squareup.picasso.Picasso;

public class SelectFriend extends DialogFragment implements OnItemClickListener {

  private static final String ARG_GIFTS = "gifts";

  private ListView mList;
  OnFriendSelectedListener mListener;

  public interface OnFriendSelectedListener {
    void onFriendSelected(GiftType gift);
  }

  public static SelectFriend newInstance(List<GiftType> gifts) {
    SelectFriend dialog = new SelectFriend();
    Bundle args = new Bundle();
    Gson gson = new Gson();
    ArrayList<String> data = new ArrayList<String>(gifts.size());
    for (GiftType gift : gifts) {
      data.add(gson.toJson(gift));
    }
    args.putStringArrayList(ARG_GIFTS, data);
    dialog.setArguments(args);
    return dialog;
  }
  
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setStyle(DialogFragment.STYLE_NO_TITLE, 0);
  }

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    Fragment f = getTargetFragment();
    if (f instanceof OnFriendSelectedListener) {
      mListener = (OnFriendSelectedListener) f;
    } else {
      mListener = (OnFriendSelectedListener) activity;
    }
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View root = inflater.inflate(R.layout.fragment_select_friend, container, false);
    mList = (ListView) root.findViewById(R.id.list);
    mList.setAdapter(new Adapter(inflater, getGifts()));
    mList.setOnItemClickListener(this);
    return root;
  }

  @Override
  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    GiftType gift = (GiftType) parent.getItemAtPosition(position);
    mListener.onFriendSelected(gift);
    dismiss();
  }

  List<GiftType> getGifts() {
    ArrayList<GiftType> result = new ArrayList<GiftType>();
    ArrayList<String> data = getArguments().getStringArrayList(ARG_GIFTS);
    if (data != null) {
      Gson gson = new Gson();
      for (String d : data) {
        result.add(gson.fromJson(d, GiftType.class));
      }
    }
    return result;
  }

  private static class Adapter extends BaseAdapter {

    List<GiftType> mGifts;
    LayoutInflater mInflater;

    public Adapter(LayoutInflater inflater, List<GiftType> gifts) {
      mInflater = inflater;
      mGifts = gifts;
    }

    @Override
    public int getCount() {
      return mGifts.size();
    }

    @Override
    public GiftType getItem(int position) {
      return mGifts.get(position);
    }

    @Override
    public long getItemId(int position) {
      return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      if (convertView == null) {
        convertView = mInflater.inflate(R.layout.fragment_select_friend_row, parent, false);
      }

      GiftType gift = getItem(position);

      TextView text = (TextView) convertView.findViewById(R.id.name);
      text.setText(gift.friendName);

      Uri uri = Fb.getFriendPhotoUri(gift.fbFriendId);
      ImageView image = (ImageView) convertView.findViewById(R.id.image);
      Picasso.with(convertView.getContext()).load(uri).into(image);

      return convertView;
    }
  }

}
