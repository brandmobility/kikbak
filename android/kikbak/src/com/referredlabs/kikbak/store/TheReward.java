
package com.referredlabs.kikbak.store;

import com.referredlabs.kikbak.data.AvailableCreditType;
import com.referredlabs.kikbak.data.ClientMerchantType;
import com.referredlabs.kikbak.data.GiftType;
import com.referredlabs.kikbak.utils.Nearest;

import java.util.ArrayList;
import java.util.List;

public class TheReward {

  private ArrayList<GiftType> mGifts = new ArrayList<GiftType>();
  private AvailableCreditType mCredit;
  private final ClientMerchantType mMerchant;
  private Nearest mNearest;

  public TheReward(long offerId, ClientMerchantType merchant) {
    mMerchant = merchant;
    mNearest = new Nearest(merchant.locations);
  }

  public void addGift(GiftType gift) {
    mGifts.add(gift);
  }

  public List<GiftType> getGifts() {
    return mGifts;
  }

  public void addCredit(AvailableCreditType credit) {
    if (mCredit != null)
      throw new IllegalArgumentException("credit was already set, two credits for one offer ?");
    mCredit = credit;
  }

  public AvailableCreditType getCredit() {
    return mCredit;
  }

  public boolean hasGifts() {
    return mGifts.size() > 0;
  }

  public boolean hasCredit() {
    return mCredit != null;
  }

  public void calculateDistance(double latitude, double longitude) {
    mNearest.determineNearestLocation(latitude, longitude);
  }

  public float getDistance() {
    return mNearest.getDistance();
  }

  public Nearest getNearest() {
    return mNearest;
  }

  public ClientMerchantType getMerchant() {
    return mMerchant;
  }

  public String getImageUrl() {
    if (mGifts.size() > 0) {
      return mGifts.get(0).imageUrl;
    } else if (mCredit != null) {
      return mCredit.imageUrl;
    }
    return null;
  }

}
