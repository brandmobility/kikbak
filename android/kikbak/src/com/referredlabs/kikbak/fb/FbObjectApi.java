
package com.referredlabs.kikbak.fb;

import java.io.IOException;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;

import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.RequestBatch;
import com.facebook.Response;
import com.facebook.Session;
import com.referredlabs.kikbak.C;
import com.referredlabs.kikbak.data.ClientOfferType;
import com.referredlabs.kikbak.utils.Nearest;

public class FbObjectApi {

  private final static String COUPON_PATH = "me/objects/referredlabs:coupon";
  private final static String SHARE_PATH = "me/referredlabs:share";

  public static void publishStory(Session session, ClientOfferType offer, String imageUrl,
      String userMessage, String code)
      throws IOException {
    RequestBatch requestBatch = new RequestBatch();

    Request giftReq = createGiftRequest(session, offer, imageUrl, code);
    giftReq.setBatchEntryName("objectCreate");
    requestBatch.add(giftReq);

    String location = getLocationString(offer);
    Request shareReq = createShareActionRequest(session, "{result=objectCreate:$.id}", userMessage,
        location);
    requestBatch.add(shareReq);

    List<Response> responses = requestBatch.executeAndWait();

    for (Response r : responses) {
      if (r.getError() != null) {
        throw new IOException("FB error:" + r.getError().getErrorMessage());
      }
    }
  }

  private static Request createGiftRequest(Session session, ClientOfferType offer, String imageUrl,
      String code) {
    JSONObject gift = new JSONObject();
    try {
      // common properties
      String title = offer.merchantName + ":" + offer.giftDesc;
      String description = offer.giftDetailedDesc;
      String url = "http://" + C.SERVER_DOMAIN + "/att/kikbak/landing.html?code=" + code;
      gift.put("title", title);
      gift.put("description", description);
      gift.put("image", imageUrl);
      gift.put("url", url);

      // kikbak specific properties
      JSONObject data = new JSONObject();
      data.put("merchant_name", offer.merchantName);
      data.put("detailed_desc", offer.giftDetailedDesc);
      gift.put("data", data);
    } catch (JSONException e) {
      // ignore
    }

    Bundle objectParams = new Bundle();
    objectParams.putString("object", gift.toString());

    Request objectRequest = new Request(session, COUPON_PATH, objectParams, HttpMethod.POST, null);
    objectRequest.setBatchEntryName("objectCreate");

    return objectRequest;
  }

  private static Request createShareActionRequest(Session session, String objectId,
      String userMessage, String location) {
    Bundle actionParams = new Bundle();
    actionParams.putString("coupon", objectId);
    actionParams.putString("fb:explicitly_shared", "true");
    actionParams.putString("message", userMessage);
    if (location != null)
      actionParams.putString("place", location);
    Request actionRequest = new Request(session, SHARE_PATH, actionParams, HttpMethod.POST, null);
    return actionRequest;
  }

  private static String getLocationString(ClientOfferType offer) {
    String result = null;
    Nearest nearest = new Nearest(offer.locations);
    if (nearest.getDistance() < C.IN_STORE_DISTANCE) {
      // FIXME
      result = "http://young-springs-3453.herokuapp.com/coupon.html?loc=3";
    }
    return result;
  }

}
