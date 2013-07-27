
package com.referredlabs.kikbak.store;

import com.referredlabs.kikbak.data.ClientOfferType;
import com.referredlabs.kikbak.utils.Nearest;

public class TheOffer {

  private ClientOfferType mOffer;
  private Nearest mNearest;

  public TheOffer(long offerId, ClientOfferType offer) {
    mOffer = offer;
    mNearest = new Nearest(offer.locations);
  }

  public void calculateDistance(double latitude, double longitude) {
    mNearest.determineNearestLocation(latitude, longitude);
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
