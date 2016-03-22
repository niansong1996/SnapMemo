package org.sensation.snapmemo.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.widget.Toast;

import org.sensation.snapmemo.R;
import org.sensation.snapmemo.receiver.AlarmReceiver;

/**
 * 后台定时提醒服务，在每次设置memo后根据memo的时间使用
 */
public class AlarmService extends Service {

    /**
     * 自定义启动方式
     *
     * @param context 来自MainActivity的上下文
     * @param memoID  memoID的后8为作为int类型作为标识
     * @param topic   标题
     * @param time    需要提醒的时间距离开机时间的时长(ns)
     */
    public static void actionStart(Context context, int memoID, String topic, long time) {
        Intent intent = new Intent(context, AlarmService.class);
        intent.putExtra("memoID", memoID);
        intent.putExtra("topic", topic);
        intent.putExtra("time", time);
        context.startService(intent);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int memoID = intent.getIntExtra("memoID", 0);
        String topic = intent.getStringExtra("topic");
        long time = intent.getLongExtra("time", 0);

        //如果有分配memoID就设定闹钟提醒
        if (memoID != 0) {
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            long triggerAtTime = SystemClock.elapsedRealtime() + time;
            Intent remindingIntent = new Intent(this, AlarmReceiver.class);
            remindingIntent.putExtra("topic", topic);
            PendingIntent pendingRemindingIntent = PendingIntent.getBroadcast(this, memoID, remindingIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pendingRemindingIntent);
        } else {
            //不设置闹钟
            Toast.makeText(AlarmService.this, getString(R.string.reminder_fail), Toast.LENGTH_SHORT).show();
        }
        return super.onStartCommand(intent, flags, startId);
    }
}
