
package com.referredlabs.kikbak.ui;

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
import com.referredlabs.kikbak.data.ShareInfoType;
import com.referredlabs.kikbak.fb.Fb;
import com.referredlabs.kikbak.utils.LocaleUtils;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

public class SelectFriend extends DialogFragment implements OnItemClickListener {

  private static final String ARG_GIFT = "gift";

  private TextView mValue;
  private ListView mList;
  GiftType mGift;
  OnFriendSelectedListener mListener;

  public interface OnFriendSelectedListener {
    void onFriendSelected(GiftType gift, int idx);
  }

  public static SelectFriend newInstance(GiftType gift) {
    SelectFriend dialog = new SelectFriend();
    Bundle args = new Bundle();
    String data = new Gson().toJson(gift);
    args.putString(ARG_GIFT, data);
    dialog.setArguments(args);
    return dialog;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setStyle(DialogFragment.STYLE_NO_TITLE, 0);

    String data = getArguments().getString(ARG_GIFT);
    mGift = new Gson().fromJson(data, GiftType.class);
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
    mValue = (TextView) root.findViewById(R.id.value);

    String text = LocaleUtils.getGiftValueString(getActivity(), mGift);
    mValue.setText(text);

    mList = (ListView) root.findViewById(R.id.list);
    mList.setAdapter(new Adapter(inflater, mGift.shareInfo));
    mList.setOnItemClickListener(this);
    return root;
  }

  @Override
  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    mListener.onFriendSelected(mGift, position);
    dismiss();
  }

  private static class Adapter extends BaseAdapter {

    ShareInfoType[] mFriends;
    LayoutInflater mInflater;

    public Adapter(LayoutInflater inflater, ShareInfoType[] friends) {
      mInflater = inflater;
      mFriends = friends;
    }

    @Override
    public int getCount() {
      return mFriends.length;
    }

    @Override
    public ShareInfoType getItem(int position) {
      return mFriends[position];
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

      ShareInfoType friend = getItem(position);

      TextView text = (TextView) convertView.findViewById(R.id.name);
      text.setText(friend.friendName);

      Uri uri = Fb.getFriendPhotoUri(friend.fbFriendId);
      ImageView image = (ImageView) convertView.findViewById(R.id.image);
      Picasso.with(convertView.getContext()).load(uri).into((Target) image);

      return convertView;
    }
  }

}
