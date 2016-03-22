package org.sensation.snapmemo.receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.sensation.snapmemo.R;
import org.sensation.snapmemo.activity.MainActivity;

/**
 * 接收定时提醒广播并处理广播的类
 */
public class AlarmReceiver extends BroadcastReceiver {
    private final static int NOTIFICATION_ID = 1;

    @Override
    public void onReceive(Context context, Intent intent) {
        String topic = intent.getStringExtra("topic");

        //显示通知，并震动提醒
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Bitmap logo = BitmapFactory.decodeResource(context.getResources(), R.mipmap.logo);
        Intent openIntent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, openIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        long[] vibrates = {0, 250, 250, 250};

        Notification notification = new Notification.Builder(context)
                .setSmallIcon(R.mipmap.logo)
                .setTicker("SnapMemo提醒")
                .setContentTitle("SnapMemo提醒")
                .setContentText(topic)
                .setWhen(System.currentTimeMillis())
                .setContentIntent(pendingIntent)
                .setVibrate(vibrates)
                .build();

        notificationManager.notify(NOTIFICATION_ID, notification);
    }

}
