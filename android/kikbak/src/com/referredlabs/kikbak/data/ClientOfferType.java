
package com.referredlabs.kikbak.data;

public class ClientOfferType {
  public long id; // optional
  public String merchantName;
  public String name;
  public String giftDesc;
  public String giftDescOptional;
  public double giftValue; // optional
  public String giftType;
  public String kikbakDesc;
  public String kikbakDescOptional;
  public double kikbakValue; // optional
  public long merchantId;
  public String merchantImageUrl;
  public String merchantUrl;
  public String imageUrl;
  public String termsOfService;
  public OfferLocationType[] locations;
  public long beginDate;
  public long endDate;

  // not from json
  transient public float mCurrentDistance = Float.MAX_VALUE;
}
