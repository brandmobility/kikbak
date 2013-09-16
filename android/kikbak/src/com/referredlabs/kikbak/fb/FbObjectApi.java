
package com.referredlabs.kikbak.fb;

import java.io.IOException;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Bundle;

import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.RequestBatch;
import com.facebook.Response;
import com.facebook.Session;
import com.referredlabs.kikbak.C;
import com.referredlabs.kikbak.Kikbak;
import com.referredlabs.kikbak.R;
import com.referredlabs.kikbak.data.ClientOfferType;
import com.referredlabs.kikbak.data.MerchantLocationType;
import com.referredlabs.kikbak.utils.Nearest;

public class FbObjectApi {

  private final static String COUPON_PATH = "me/objects/referredlabs:coupon";
  private final static String SHARE_PATH = "me/referredlabs:share";

  public static void publishStory(Session session, ClientOfferType offer, String landingPage,
      String imageUrl, String userMessage, String code) throws IOException {

    // For some reasons publishing story failed with IOException from FB internals
    // Until resolved lets try 3 times

    IOException exception = null;
    for (int i = 0; i < 3; i++) {
      try {
        publishStoryImpl(session, offer, landingPage, imageUrl, userMessage, code);
        return;
      } catch (IOException e) {
        android.util.Log.w("MMM", "publish story failed ");
        exception = e;
      }
    }
    throw exception;
  }

  public static void publishStoryImpl(Session session, ClientOfferType offer, String landingPage,
      String imageUrl,
      String userMessage, String code)
      throws IOException {
    RequestBatch requestBatch = new RequestBatch();

    Request giftReq = createGiftRequest(session, offer, landingPage, imageUrl, code);
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

  private static Request createGiftRequest(Session session, ClientOfferType offer,
      String landingPage, String imageUrl,
      String code) {
    JSONObject gift = new JSONObject();
    try {
      Context ctx = Kikbak.getInstance();
      // common properties
      String title = ctx.getString(R.string.facebook_story_title_fmt, offer.giftDesc,
          offer.merchantName);
      String description = ctx.getString(R.string.facebook_story_first_line_fmt, offer.giftDesc,
          offer.giftDetailedDesc);

      gift.put("title", title);
      gift.put("description", description);
      gift.put("image", imageUrl);
      gift.put("url", landingPage);

      // kikbak specific properties
      JSONObject data = new JSONObject();
      data.put("merchant", offer.merchantName);
      data.put("gift_desc", offer.giftDesc);
      data.put("gift_detailed_desc", offer.giftDetailedDesc);

      Nearest nearest = new Nearest(offer.locations);
      if (nearest.getDistance() < C.CLOSE_TO_STORE_DISTANCE) {
        MerchantLocationType loc = nearest.get();
        String secondLine = ctx.getString(R.string.facebook_story_second_line_fmt,
            offer.merchantName, loc.address1, loc.city, loc.state);
        data.put("second_line", secondLine);
      }
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
      // result = "http://young-springs-3453.herokuapp.com/coupon.html?loc=3";
    }
    return result;
  }

}
