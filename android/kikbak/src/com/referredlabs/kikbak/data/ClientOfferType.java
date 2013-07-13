
package com.referredlabs.kikbak.data;

public class ClientOfferType {
  public long id; // optional
  public String merchantName;
  public String name;
  public String giftDesc;
  public String giftDetailedDesc;
  public double giftValue; // optional
  public String giftDiscountType;
  public String kikbakDesc;
  public String kikbakDetailedDesc;
  public double kikbakValue; // optional
  public long merchantId;
  public String merchantUrl;
  public String offerImageUrl;
  public String giveImageUrl;
  public String tosUrl;
  public OfferLocationType[] locations;
  public long beginDate;
  public long endDate;

  // not from json
  transient public float mCurrentDistance = Float.MAX_VALUE;
}
