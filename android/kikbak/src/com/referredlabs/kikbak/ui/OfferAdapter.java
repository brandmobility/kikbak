
package com.referredlabs.kikbak.ui;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.referredlabs.kikbak.R;
import com.referredlabs.kikbak.data.ClientOfferType;
import com.referredlabs.kikbak.data.DiscountType;
import com.referredlabs.kikbak.store.TheOffer;
import com.referredlabs.kikbak.ui.IconBarHelper.IconBarListener;
import com.referredlabs.kikbak.utils.LocaleUtils;
import com.referredlabs.kikbak.utils.Nearest;
import com.squareup.picasso.Picasso;

import java.util.List;

public class OfferAdapter extends BaseAdapter {

  private Context mContext;
  private LayoutInflater mInflater;
  private List<TheOffer> mOffers;
  IconBarListener mIconBarListener;

  public OfferAdapter(Context context, IconBarListener listener) {
    mContext = context;
    mInflater = LayoutInflater.from(context);
    mIconBarListener = listener;
  }

  @Override
  public int getCount() {
    return mOffers == null ? 0 : mOffers.size();
  }

  @Override
  public TheOffer getItem(int position) {
    return mOffers.get(position);
  }

  @Override
  public long getItemId(int position) {
    return mOffers.get(position).getOffer().id;
  }

  @Override
  public View getView(int position, View view, ViewGroup parent) {
    OfferHelper helper;
    if (view == null) {
      int layout = getItemViewType(position) == 0 ? R.layout.offer : R.layout.offer_off;
      view = mInflater.inflate(layout, parent, false);
      helper = new OfferHelper(view, mIconBarListener);
      view.setTag(helper);
    } else {
      helper = (OfferHelper) view.getTag();
    }

    TheOffer theOffer = getItem(position);
    ClientOfferType offer = theOffer.getOffer();
    Uri url = Uri.parse(offer.offerImageUrl);

    helper.mImage.setImageResource(R.color.no_image);
    Picasso.with(mContext).load(url).into(helper.mImage);
    helper.setName(offer.merchantName);
    helper.setGetValue(offer.giftDiscountType);

    helper.setLink(offer.merchantUrl);
    Nearest nearest = theOffer.getNearest();
    helper.setLocation(nearest);
    helper.setPhone(Long.toString(nearest.getPhoneNumber()));

    String text = LocaleUtils.getRibbonGiveString(mContext, offer);
    helper.setGiveValue(text);

    text = LocaleUtils.getRibbonGetString(mContext, offer);
    helper.setGetValue(text);

    return view;
  }

  @Override
  public int getItemViewType(int position) {
    return DiscountType.AMOUNT.equals(getItem(position).getOffer().giftDiscountType) ? 0 : 1;
  }

  @Override
  public int getViewTypeCount() {
    return 2;
  }

  public void swap(List<TheOffer> offers) {
    mOffers = offers;
    notifyDataSetChanged();
  }
}
