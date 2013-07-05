
package com.referredlabs.kikbak.ui;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.referredlabs.kikbak.R;
import com.referredlabs.kikbak.data.ClientOfferType;
import com.referredlabs.kikbak.ui.IconBarHelper.IconBarListener;
import com.referredlabs.kikbak.utils.DataUtils;
import com.referredlabs.kikbak.utils.Nearest;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class OfferAdapter extends BaseAdapter {

  private Context mContext;
  private LayoutInflater mInflater;
  private ClientOfferType[] mOffers;
  Map<ClientOfferType, Nearest> mNearestMap;
  IconBarListener mIconBarListener;
  
  public OfferAdapter(Context context, IconBarListener listener) {
    mContext = context;
    mInflater = LayoutInflater.from(context);
    mIconBarListener = listener;
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
      helper = new OfferHelper(view, mIconBarListener);
      view.setTag(helper);
    } else {
      helper = (OfferHelper) view.getTag();
    }

    ClientOfferType offer = getItem(position);
    Uri url = Uri.parse(offer.merchantImageUrl);

    Picasso.with(mContext).load(url).into(helper.mImage);
    helper.setName(offer.merchantName);
    helper.setGetValue(offer.giftType);

    helper.setLink(offer.merchantUrl);
    Nearest nearest = mNearestMap.get(offer);
    helper.setLocation(nearest);
    helper.setPhone(Long.toString(nearest.getPhoneNumber()));
    
    String text = DataUtils.getRibbonGiveString(offer.giftType, offer.giftValue);
    helper.setGetValue(text);

    text = DataUtils.getRibbonGetString(offer.kikbakValue);
    helper.setGetValue(text);

    return view;
  }

  public void swap(ClientOfferType[] offers, HashMap<ClientOfferType, Nearest> mLocationMap) {
    mOffers = offers;
    mNearestMap = mLocationMap;
    notifyDataSetChanged();
  }

}
