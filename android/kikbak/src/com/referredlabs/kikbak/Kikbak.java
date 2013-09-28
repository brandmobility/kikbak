
package com.referredlabs.kikbak;

import java.io.File;
import java.net.URL;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;

import com.referredlabs.kikbak.http.HttpClientHelper;
import com.squareup.okhttp.OkHttpClient;

public class Kikbak extends Application {

  private static Kikbak mInstance;

  public static final Executor PARALLEL_EXECUTOR;
  static {
    if (Build.VERSION.SDK_INT < 11) {
      PARALLEL_EXECUTOR = createExecutor();
    } else {
      PARALLEL_EXECUTOR = AsyncTask.THREAD_POOL_EXECUTOR;
    }
  }

  private static Executor createExecutor() {
    ThreadFactory threadFactory = new ThreadFactory() {
      private final AtomicInteger mCount = new AtomicInteger(1);

      public Thread newThread(Runnable r) {
        return new Thread(r, "GlobalTh#" + mCount.getAndIncrement());
      }
    };

    BlockingQueue<Runnable> poolWorkQueue = new LinkedBlockingQueue<Runnable>(10);
    return new ThreadPoolExecutor(4, 128, 0, TimeUnit.SECONDS, poolWorkQueue, threadFactory);
  }

  public static Kikbak getInstance() {
    return mInstance;
  }

  private static void setInstance(Kikbak instance) {
    mInstance = instance;
  }

  @Override
  public void onCreate() {
    super.onCreate();
    URL.setURLStreamHandlerFactory(new OkHttpClient()); // https://github.com/square/okhttp/issues/184
    HttpClientHelper.createInstance(this);
    setInstance(this);
  }

  public int getAppVersion() {
    try {
      PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
      return packageInfo.versionCode;
    } catch (NameNotFoundException e) {
      // should never happen
      throw new RuntimeException("Could not get package name: " + e);
    }
  }

  public void removeFileAsync(final Uri uri) {
    if (uri != null) {
      PARALLEL_EXECUTOR.execute(new Runnable() {
        @Override
        public void run() {
          String path = uri.getPath();
          new File(path).delete();
        }
      });
    }
  }
}
