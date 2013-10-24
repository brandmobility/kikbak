
package com.referredlabs.kikbak.ui;

import java.util.List;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
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
  private static int TYPE_GIVE_ONLY_AMOUNT = 2;
  private static int TYPE_GIVE_ONLY_PERCENTAGE = 3;
  private static int TYPE_GET_ONLY = 4;

  private static int[] RIBBONS = {
      R.layout.ribbon_both_amount,
      R.layout.ribbon_both_percentage,
      R.layout.ribbon_give_only_amount,
      R.layout.ribbon_give_only_percentage,
      R.layout.ribbon_get_only
  };

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
      view = mInflater.inflate(R.layout.offer, parent, false);
      ViewStub ribbon = (ViewStub) view.findViewById(R.id.ribbon);
      int layout = RIBBONS[getItemViewType(position)];
      ribbon.setLayoutResource(layout);
      View rr = ribbon.inflate();
      rr.setLayoutParams(rr.getLayoutParams());
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

  @Override
  public int getItemViewType(int position) {
    ClientOfferType offer = getItem(position).getOffer();
    OfferType offerType = offer.offerType;
    DiscountType discountType = offer.giftDiscountType;

    if (offerType == OfferType.both && discountType == DiscountType.amount)
      return TYPE_BOTH_AMOUNT;
    if (offerType == OfferType.both && discountType == DiscountType.percentage)
      return TYPE_BOTH_PERCENTAGE;
    if (offerType == OfferType.give_only && discountType == DiscountType.amount)
      return TYPE_GIVE_ONLY_AMOUNT;
    if (offerType == OfferType.give_only && discountType == DiscountType.percentage)
      return TYPE_GIVE_ONLY_PERCENTAGE;
    if (offerType == OfferType.get_only)
      return TYPE_GET_ONLY;

    return TYPE_BOTH_AMOUNT;
  }

  @Override
  public int getViewTypeCount() {
    return RIBBONS.length;
  }

  public void swap(List<TheOffer> offers) {
    mOffers = offers;
    notifyDataSetChanged();
  }
}
