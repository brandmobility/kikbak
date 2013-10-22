
package com.referredlabs.kikbak.ui;

import java.util.List;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.referredlabs.kikbak.R;
import com.referredlabs.kikbak.data.ClientOfferType;
import com.referredlabs.kikbak.data.DiscountType;
import com.referredlabs.kikbak.data.OfferType;
import com.referredlabs.kikbak.store.TheOffer;
import com.referredlabs.kikbak.ui.IconBarHelper.IconBarListener;
import com.referredlabs.kikbak.utils.LocaleUtils;
import com.referredlabs.kikbak.utils.Nearest;
import com.squareup.picasso.Picasso;

public class OfferAdapter extends BaseAdapter {

  private static int TYPE_BOTH_AMOUNT = 0;
  private static int TYPE_BOTH_PERCENTAGE = 1;
  private static int TYPE_GIVE_ONLY = 2;
  private static int TYPE_GET_ONLY = 3;

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
      int layout = getLayoutFromType(getItemViewType(position));
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
    helper.setLink(offer.merchantUrl);
    Nearest nearest = theOffer.getNearest();
    helper.setLocation(nearest);
    helper.setPhone(Long.toString(nearest.get().phoneNumber));

    if (offer.offerType == OfferType.both || offer.offerType == OfferType.give_only) {
      String text = LocaleUtils.getRibbonGiveString(mContext, offer);
      helper.setGiveValue(text);
    }

    if (offer.offerType == OfferType.both || offer.offerType == OfferType.get_only) {
      String text = LocaleUtils.getRibbonGetString(mContext, offer);
      helper.setGetValue(text);
    }

    return view;
  }

  private int getLayoutFromType(int type) {
    if (type == TYPE_BOTH_AMOUNT)
      return R.layout.offer_both_amount;
    else if (type == TYPE_BOTH_PERCENTAGE)
      return R.layout.offer_both_percentage;
    else if (type == TYPE_GIVE_ONLY)
      return R.layout.offer_give_only;
    else
      return R.layout.offer_get_only;
  }

  @Override
  public int getItemViewType(int position) {
    ClientOfferType offer = getItem(position).getOffer();
    if (offer.offerType == OfferType.both) {
      return DiscountType.AMOUNT.equals(getItem(position).getOffer().giftDiscountType)
          ? TYPE_BOTH_AMOUNT : TYPE_BOTH_PERCENTAGE;
    } else if (offer.offerType == OfferType.give_only) {
      return TYPE_GIVE_ONLY;
    } else {
      return TYPE_GET_ONLY;
    }
  }

  @Override
  public int getViewTypeCount() {
    return 4;
  }

  public void swap(List<TheOffer> offers) {
    mOffers = offers;
    notifyDataSetChanged();
  }
}
