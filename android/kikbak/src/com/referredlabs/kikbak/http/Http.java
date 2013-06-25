package com.referredlabs.kikbak.http;

import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import com.google.common.io.CharStreams;
import com.google.gson.Gson;
import com.referredlabs.kikbak.data.RegisterUserRequest;
import com.referredlabs.kikbak.data.RegisterUserResponseWrapper;
import com.referredlabs.kikbak.data.User;

public class Http {

  static final String SERVER = "192.168.0.103";
  static final short PORT = 8080;
  static final String REGISTER_PATH = "/kikbak/user/register/fb/";

  public static long sendRegisterUserReqest(User user) {
    try {
      RegisterUserRequest req = new RegisterUserRequest();
      req.setUser(user);
      String data = jsonWithType(req);

      HttpClient client = new DefaultHttpClient();
      HttpPost post = new HttpPost("http://" + SERVER + ":" + PORT + REGISTER_PATH);
      StringEntity entity = new StringEntity(data);
      entity.setContentType("application/json");
      post.setEntity(entity);
      HttpResponse resp = client.execute(post);
      int code = resp.getStatusLine().getStatusCode();
      HttpEntity result = resp.getEntity();
      String output = CharStreams.toString(new InputStreamReader(result.getContent(), "UTF-8"));
      resp.getEntity().consumeContent();

      Gson gson = new Gson();
      RegisterUserResponseWrapper reponse = gson.fromJson(output, RegisterUserResponseWrapper.class);
      android.util.Log.d("MMM", "reponse=" + reponse);
      if (reponse != null && reponse.registerUserResponse.userId != null)
        return reponse.registerUserResponse.userId.userId;

    } catch (Exception e) {
      android.util.Log.d("MMM", "exception:" + e);
    }
    return -1;
  }

  private static String jsonWithType(Object o) {
    StringBuilder out = new StringBuilder();
    out.append("{\"").append(o.getClass().getSimpleName()).append("\":");
    Gson gson = new Gson();
    gson.toJson(o, out);
    out.append('}');
    return out.toString();
  }
}
