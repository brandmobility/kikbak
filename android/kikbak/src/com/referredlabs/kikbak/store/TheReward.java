
package com.referredlabs.kikbak.store;

import com.referredlabs.kikbak.data.AvailableCreditType;
import com.referredlabs.kikbak.data.ClientMerchantType;
import com.referredlabs.kikbak.data.GiftType;
import com.referredlabs.kikbak.utils.Nearest;

public class TheReward {

  private GiftType mGift;
  private AvailableCreditType mCredit;
  private final ClientMerchantType mMerchant;
  private Nearest mNearest;

  public TheReward(long offerId, ClientMerchantType merchant) {
    mMerchant = merchant;
    mNearest = new Nearest(merchant.locations);
  }

  public void addGift(GiftType gift) {
    if (mGift != null)
      throw new IllegalArgumentException("gift was already set, two same gifts for one offer ?");
    mGift = gift;
  }

  public GiftType getGift() {
    return mGift;
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
    return mGift != null;
  }

  public boolean hasMultipleGifts() {
    return mGift != null && mGift.shareInfo.length > 1;
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
    if (mGift != null) {
      return mGift.defaultGiveImageUrl;
    } else if (mCredit != null) {
      return mCredit.imageUrl;
    }
    return null;
  }
}
