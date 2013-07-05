
package com.referredlabs.kikbak.utils;

import com.referredlabs.kikbak.data.ClientOfferType;

import java.util.Comparator;
import java.util.Map;

public class OffersLocationComparator implements Comparator<ClientOfferType> {

  Map<ClientOfferType, Nearest> mMap;

  public OffersLocationComparator(Map<ClientOfferType, Nearest> map) {
    mMap = map;
  }

  @Override
  public int compare(ClientOfferType lhs, ClientOfferType rhs) {
    Nearest ln = mMap.get(lhs);
    Nearest rn = mMap.get(rhs);
    float l = ln.getDistance();
    float r = rn.getDistance();
    return Float.compare(l, r);
  }
}
