
package com.referredlabs.kikbak.ui;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.referredlabs.kikbak.R;
import com.referredlabs.kikbak.data.ClientOfferType;
import com.squareup.picasso.Picasso;

public class OfferAdapter extends BaseAdapter {

  private Context mContext;
  private LayoutInflater mInflater;
  private ClientOfferType[] mOffers;

  public OfferAdapter(Context context) {
    mContext = context;
    mInflater = LayoutInflater.from(context);
    mOffers = new ClientOfferType[0];
  }

  @Override
  public int getCount() {
    return mOffers == null ? 0 : mOffers.length;
  }

  @Override
  public ClientOfferType getItem(int position) {
    return mOffers[position];
  }

  @Override
  public long getItemId(int position) {
    return mOffers[position].id;
  }

  @Override
  public View getView(int position, View view, ViewGroup parent) {
    OfferHelper helper;
    if (view == null) {
      view = mInflater.inflate(R.layout.offer, parent, false);
      helper = new OfferHelper(view);
      view.setTag(helper);
    } else {
      helper = (OfferHelper) view.getTag();
    }

    ClientOfferType offer = getItem(position);
    Uri url = Uri.parse(offer.imageUrl);

    Picasso.with(mContext).load(url).into(helper.mImage);
    helper.setName(offer.merchantName);
    helper.setDistance(offer.mCurrentDistance);
    return view;
  }

  public void swap(ClientOfferType[] offers) {
    mOffers = offers;
    notifyDataSetChanged();
  }

}
