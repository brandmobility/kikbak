
package com.referredlabs.kikbak.http;

import android.net.http.AndroidHttpClient;

import com.google.common.io.CharStreams;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.referredlabs.kikbak.C;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;

public class Http {

  static final String USER_AGENT = "kikbak";
  static final String SERVER = C.SERVER;
  static final short PORT = C.PORT;

  public static String getUri(String path) {
    return "http://" + SERVER + ":" + PORT + "/kikbak" + path;
  }

  public static <T, V> V execute(String uri, T request, Class<V> responseType) throws IOException {
    String data = writeRequest(request);

    AndroidHttpClient client = AndroidHttpClient.newInstance(USER_AGENT);
    HttpPost post = new HttpPost(uri);
    StringEntity reqEntity = new StringEntity(data);
    reqEntity.setContentType("application/json");
    post.setEntity(reqEntity);
    AndroidHttpClient.modifyRequestToAcceptGzipResponse(post);

    HttpResponse resp = client.execute(post);
    int code = resp.getStatusLine().getStatusCode();
    if (code == 200) {
      V response = getResponse(resp.getEntity(), responseType);
      client.close();
      return response;
    }

    String content = getContent(resp.getEntity());
    client.close();
    throw new HttpStatusException(code, content);
  }

  private static <V> V getResponse(HttpEntity entity, Class<V> responseType) throws IOException {
    if (entity != null) {
      try {
        InputStream in = AndroidHttpClient.getUngzippedContent(entity);
        V respone = readResponse(in, responseType);
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

  private static <T> String writeRequest(T request) throws IOException {
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

  private static <T> T readResponse(InputStream in, Class<T> responseType) throws IOException {
    Gson gson = new Gson();
    JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
    reader.beginObject();
    String typeName = getJsonName(responseType);
    String name = reader.nextName();
    if (!typeName.equals(name))
      throw new IOException("Wrong response type, expected:" + typeName
          + " received:" + name);
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

}
