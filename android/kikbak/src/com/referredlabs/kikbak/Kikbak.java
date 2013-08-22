
package com.referredlabs.kikbak;

import java.io.File;
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

import com.referredlabs.kikbak.http.HttpClientHelper;

public class Kikbak extends Application {

  private static Kikbak mInstance;

  private static final ThreadFactory sThreadFactory = new ThreadFactory() {
    private final AtomicInteger mCount = new AtomicInteger(1);

    public Thread newThread(Runnable r) {
      return new Thread(r, "GlobalTh#" + mCount.getAndIncrement());
    }
  };

  private static final BlockingQueue<Runnable> sPoolWorkQueue =
      new LinkedBlockingQueue<Runnable>(10);

  public static final Executor PARALLEL_EXECUTOR =
      new ThreadPoolExecutor(4, 128, 0, TimeUnit.SECONDS, sPoolWorkQueue, sThreadFactory);

  public static Kikbak getInstance() {
    return mInstance;
  }

  private static void setInstance(Kikbak instance) {
    mInstance = instance;
  }

  @Override
  public void onCreate() {
    super.onCreate();
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
