
package com.referredlabs.kikbak.gcm;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.referredlabs.kikbak.R;
import com.referredlabs.kikbak.ui.MainActivity;

public class GcmBroadcastReceiver extends BroadcastReceiver {
  public static final int NOTIFICATION_ID = 1;

  @Override
  public void onReceive(Context context, Intent intent) {
    GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(context);
    String type = gcm.getMessageType(intent);
    if (type != null) {
      String data = intent.getExtras().toString();
      sendNotification(context, type, data);
    }
    setResultCode(Activity.RESULT_OK);
  }

  private void sendNotification(Context ctx, String type, String data) {
    Intent target = new Intent(ctx, MainActivity.class);
    PendingIntent contentIntent = PendingIntent.getActivity(ctx, 0, target, 0);
    String msg = type + ":" + data;
    NotificationCompat.Builder mBuilder =
        new NotificationCompat.Builder(ctx)
            .setAutoCancel(true)
            .setSmallIcon(R.drawable.ic_launcher)
            .setContentTitle("Kikbak notification")
            .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
            .setContentText(msg);

    mBuilder.setContentIntent(contentIntent);
    Notification notification = mBuilder.build();

    NotificationManager mgr = (NotificationManager) ctx
        .getSystemService(Context.NOTIFICATION_SERVICE);
    mgr.notify(NOTIFICATION_ID, notification);
  }
}
