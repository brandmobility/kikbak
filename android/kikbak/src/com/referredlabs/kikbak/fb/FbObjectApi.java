
package com.referredlabs.kikbak.fb;

import android.os.Bundle;

import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.RequestBatch;
import com.facebook.Response;
import com.facebook.Session;
import com.referredlabs.kikbak.http.Http;
import com.referredlabs.kikbak.utils.Register;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

public class FbObjectApi {

  private final static String COUPON_PATH = "me/objects/referredlabs:coupon";
  private final static String SHARE_PATH = "me/referredlabs:share";

  public static void publishStory(Session session, String imageUrl, String userMessage)
      throws IOException {

    // TODO: location needs to be either facebook locaiton id
    // or uri to page with 'place' meta-data
    // do not know how to pass only lgn,lat
    final String location = "http://young-springs-3453.herokuapp.com/coupon.html";

    RequestBatch requestBatch = new RequestBatch();

    Request giftReq = createGiftRequest(session, imageUrl);
    giftReq.setBatchEntryName("objectCreate");
    requestBatch.add(giftReq);

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

  private static Request createGiftRequest(Session session, String imageUrl) {
    // FIXME
    final String url = "http://young-springs-3453.herokuapp.com/xxxx/"; //
    final String description = "This is gift description";

    JSONObject gift = new JSONObject();
    try {
      // common properties
      gift.put("image", imageUrl);
      gift.put("title", "$50 gift for my friend");
      gift.put("url", url);
      gift.put("description", description);

      // kikbak specific properties
      JSONObject data = new JSONObject();
      data.put("gift_value", "50");
      data.put("merchant_name", "Vistula");
      data.put("offer_id", "http://young-springs-3453.herokuapp.com/coupon.html");
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
    actionParams.putString("place", location);
    Request actionRequest = new Request(session, SHARE_PATH, actionParams, HttpMethod.POST, null);
    return actionRequest;
  }

}
