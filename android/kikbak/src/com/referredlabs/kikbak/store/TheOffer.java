
package com.referredlabs.kikbak.store;

import com.referredlabs.kikbak.data.ClientOfferType;
import com.referredlabs.kikbak.utils.Nearest;

public class TheOffer {

  private ClientOfferType mOffer;
  private Nearest mNearest;

  public TheOffer(long offerId, ClientOfferType offer, double latitude, double longitude) {
    mOffer = offer;
    mNearest = new Nearest(mOffer.locations, latitude, longitude);
  }

  public ClientOfferType getOffer() {
    return mOffer;
  }

  public float getDistance() {
    return mNearest.getDistance();
  }

  public Nearest getNearest() {
    return mNearest;
  }
}
