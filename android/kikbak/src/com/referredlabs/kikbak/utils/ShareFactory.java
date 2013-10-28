
package com.referredlabs.kikbak.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.support.v4.app.DialogFragment;

public class ShareFactory {

  private static ShareFactory sInstance = new ShareFactory();

  public static ShareFactory getInstance() {
    return sInstance;
  }

  ArrayList<ShareMethodInfo> mShareMethods = new ArrayList<ShareMethodInfo>();

  public void register(ShareMethodInfo shareMethod) {
    mShareMethods.add(shareMethod);
  }
  
  public List<ShareMethodInfo> getShareMethods() {
    return Collections.unmodifiableList(mShareMethods);
  }

  public static class ShareMethodInfo {
    public final Class<DialogFragment> clazz;
    public final String name;
    public final int nameResId;
    public final int iconResId;

    ShareMethodInfo(Class<DialogFragment> clazz, String name, int nameResId, int iconResId) {
      this.clazz = clazz;
      this.name = name;
      this.nameResId = nameResId;
      this.iconResId = iconResId;
    }
  }
}
