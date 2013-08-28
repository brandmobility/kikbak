
package com.referredlabs.kikbak.gcm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.referredlabs.kikbak.Kikbak;
import com.referredlabs.kikbak.R;
import com.referredlabs.kikbak.ui.MainActivity;

public class NotificationController {

  private static final int NOTIFICATION_ID = 1;

  private static final String TYPE_GIFT = "gift";
  private static final String TYPE_KIKBAK = "kikbak";

  private static NotificationController sInstance;

  private boolean mAllowNotifications = true;

  public static NotificationController getInstance() {
    if (sInstance == null) {
      sInstance = new NotificationController();
    }
    return sInstance;
  }

  private NotificationController() {
  }

  public void setAllowNotifications(boolean allow) {
    mAllowNotifications = allow;
  }

  public void clearRewardNotifications() {
    NotificationManager mgr = (NotificationManager) Kikbak.getInstance().getSystemService(
        Context.NOTIFICATION_SERVICE);
    mgr.cancel(TYPE_GIFT, NOTIFICATION_ID);
    mgr.cancel(TYPE_KIKBAK, NOTIFICATION_ID);
  }

  public void showNotification(String type, String msg) {
    if (!mAllowNotifications || type == null || msg == null)
      return;

    Context ctx = Kikbak.getInstance();

    Intent target = new Intent(ctx, MainActivity.class);
    target.putExtra(MainActivity.ARG_SHOW_REWARDS, true);
    PendingIntent contentIntent = PendingIntent.getActivity(ctx, 0, target,
        PendingIntent.FLAG_UPDATE_CURRENT);
    NotificationCompat.Builder mBuilder =
        new NotificationCompat.Builder(ctx)
            .setAutoCancel(true)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(getTitleForType(ctx, type))
            .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
            .setContentText(msg);
    mBuilder.setContentIntent(contentIntent);

    Notification notification = mBuilder.build();
    NotificationManager mgr = (NotificationManager) ctx
        .getSystemService(Context.NOTIFICATION_SERVICE);
    mgr.notify(type, NOTIFICATION_ID, notification);
  }

  private String getTitleForType(Context ctx, String type) {
    String title = null;
    if (TYPE_GIFT.equals(type)) {
      title = ctx.getString(R.string.notification_gift_title);
    }
    if (TYPE_KIKBAK.equals(type)) {
      title = ctx.getString(R.string.notification_kikbak_title);
    }
    return title;
  }

}
