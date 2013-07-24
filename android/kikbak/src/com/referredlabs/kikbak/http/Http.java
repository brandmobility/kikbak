
package com.referredlabs.kikbak.http;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.http.AndroidHttpClient;
import android.util.Pair;

import com.google.common.io.CharStreams;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.referredlabs.kikbak.C;
import com.referredlabs.kikbak.Kikbak;
import com.referredlabs.kikbak.data.UploadImageResponse;
import com.referredlabs.kikbak.utils.BitmapBody;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;

public class Http {

  public static final String USER_AGENT = "kikbak";
  static final String SERVER = C.SERVER;
  static final short PORT = C.PORT;

  public static String getUri(String path) {
    return "http://" + SERVER + ":" + PORT + "/kikbak" + path;
  }

  public static <T, V> V execute(String uri, T request, Class<V> responseType) throws IOException {
    String data = writeRequest(request);

    AndroidHttpClient client = AndroidHttpClient.newInstance(USER_AGENT);
    try {
      HttpPost post = new HttpPost(uri);
      StringEntity reqEntity = new StringEntity(data);
      reqEntity.setContentType("application/json");
      post.setEntity(reqEntity);
      AndroidHttpClient.modifyRequestToAcceptGzipResponse(post);

      HttpResponse resp = client.execute(post);
      int code = resp.getStatusLine().getStatusCode();
      if (code == 200) {
        V response = getResponse(resp.getEntity(), responseType, true);
        return response;
      }

      String content = getContent(resp.getEntity());
      throw new HttpStatusException(code, content);
    } finally {
      client.close();
    }
  }

  public static <T, V> V execute(String uri, Class<V> responseType) throws IOException {
    AndroidHttpClient client = AndroidHttpClient.newInstance(USER_AGENT);
    try {
      HttpGet get = new HttpGet(uri);
      AndroidHttpClient.modifyRequestToAcceptGzipResponse(get);
      HttpResponse resp = client.execute(get);
      int code = resp.getStatusLine().getStatusCode();
      if (code == 200) {
        V response = getResponse(resp.getEntity(), responseType, false);
        return response;
      }

      String content = getContent(resp.getEntity());
      throw new HttpStatusException(code, content);
    } finally {
      client.close();
    }
  }

  private static <V> V getResponse(HttpEntity entity, Class<V> responseType, boolean extraTyped)
      throws IOException {
    if (entity != null) {
      try {
        InputStream in = AndroidHttpClient.getUngzippedContent(entity);
        V respone = readResponse(in, responseType, extraTyped);
        return respone;
      } finally {
        closeHttpEntity(entity);
      }
    }
    throw new IOException("Empty response from server.");
  }

  private static String getContent(HttpEntity entity)
  {
    String data = null;
    if (entity != null) {
      try {
        InputStream in = AndroidHttpClient.getUngzippedContent(entity);
        data = CharStreams.toString(new InputStreamReader(in, "UTF-8"));
      } catch (Exception e) {
        // ignore
      } finally {
        closeHttpEntity(entity);
      }
    }
    return data;
  }

  public static <T> String writeRequest(T request) throws IOException {
    Gson gson = new Gson();
    StringWriter out = new StringWriter();
    JsonWriter writer = new JsonWriter(out);
    writer.beginObject();
    writer.name(request.getClass().getSimpleName());
    gson.toJson(request, request.getClass(), writer);
    writer.endObject();
    writer.close();
    return out.toString();
  }

  private static <T> T readResponse(InputStream in, Class<T> responseType, boolean extraTyped)
      throws IOException {
    Gson gson = new Gson();
    JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
    if (extraTyped) {
      reader.beginObject();
      String typeName = getJsonName(responseType);
      String name = reader.nextName();
      if (!typeName.equals(name)) {
        reader.close();
        throw new IOException("Wrong response type, expected:" + typeName + " received:" + name);
      }
    }
    T response = gson.fromJson(reader, responseType);
    reader.close();
    return response;
  }

  private static <T> String getJsonName(Class<T> type) {
    String name = type.getSimpleName();
    char first = Character.toLowerCase(name.charAt(0));
    return first + name.substring(1);
  }

  private static void closeHttpEntity(HttpEntity entity) {
    if (entity != null) {
      try {
        entity.consumeContent();
      } catch (Exception e) {
        // ignore
      }
    }
  }

  public static Pair<String, Bitmap> fetchBarcode(long userId, long giftId) throws IOException {
    AndroidHttpClient client = AndroidHttpClient.newInstance(USER_AGENT);
    try {
      String uri = getUri("/rewards/generateBarcode/" + userId + "/" + giftId + "/300/300/");
      HttpGet get = new HttpGet(uri);
      AndroidHttpClient.modifyRequestToAcceptGzipResponse(get);

      HttpResponse resp = client.execute(get);
      int code = resp.getStatusLine().getStatusCode();
      if (code == 200) {
        String barcode = resp.getFirstHeader("barcode").getValue();
        Bitmap bitmap = getContentAsBitmap(resp.getEntity());
        return new Pair<String, Bitmap>(barcode, bitmap);
      }

      String content = getContent(resp.getEntity());
      throw new HttpStatusException(code, content);
    } finally {
      client.close();
    }
  }

  private static Bitmap getContentAsBitmap(HttpEntity entity)
  {
    Bitmap bitmap = null;
    if (entity != null) {
      try {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inDensity = 300;
        opts.inTargetDensity = (int) (Kikbak.getInstance().getResources().getDisplayMetrics().density * 160);
        opts.inScaled = true;
        InputStream in = AndroidHttpClient.getUngzippedContent(entity);
        bitmap = BitmapFactory.decodeStream(in, null, opts);
      } catch (Exception e) {
        // ignore
      } finally {
        closeHttpEntity(entity);
      }
    }
    return bitmap;
  }

  public static String uploadImage(long userId, String filePath) throws IOException {
    AndroidHttpClient httpClient = AndroidHttpClient.newInstance(USER_AGENT);
    try {
      String uri = "http://54.244.124.116/s/upload.php";
      HttpPost postRequest = new HttpPost(uri);
      MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
      reqEntity.addPart("userId", new StringBody(Long.toString(userId)));
      reqEntity.addPart("file", new FileBody(new File(filePath), "image/png"));
      postRequest.setEntity(reqEntity);
      HttpResponse resp = httpClient.execute(postRequest);
      int code = resp.getStatusLine().getStatusCode();
      if (code == 200) {
        UploadImageResponse r = getResponse(resp.getEntity(), UploadImageResponse.class, false);
        return r.url;
      }

      String content = getContent(resp.getEntity());
      throw new HttpStatusException(code, content);

    } finally {
      httpClient.close();
    }
  }

  public static String uploadImage(long userId, Bitmap image) throws IOException {
    AndroidHttpClient httpClient = AndroidHttpClient.newInstance(USER_AGENT);
    try {
      String uri = "http://54.244.124.116/s/upload.php";
      HttpPost postRequest = new HttpPost(uri);
      MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
      reqEntity.addPart("userId", new StringBody(Long.toString(userId)));
      reqEntity.addPart("file", new BitmapBody(image));
      postRequest.setEntity(reqEntity);
      HttpResponse resp = httpClient.execute(postRequest);
      int code = resp.getStatusLine().getStatusCode();
      if (code == 200) {
        UploadImageResponse r = getResponse(resp.getEntity(), UploadImageResponse.class, false);
        return r.url;
      }

      String content = getContent(resp.getEntity());
      throw new HttpStatusException(code, content);

    } finally {
      httpClient.close();
    }
  }

}
