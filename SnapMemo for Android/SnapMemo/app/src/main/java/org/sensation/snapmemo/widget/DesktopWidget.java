package org.sensation.snapmemo.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import org.sensation.snapmemo.R;
import org.sensation.snapmemo.activity.MainActivity;

/**
 * Created by Alan on 2016/2/24.
 */
public class DesktopWidget extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        //TODO 显示当前时间以后的Memo
        RemoteViews remoteView = new RemoteViews(context.getPackageName(), R.layout.desktop_widget);
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent openMainIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteView.setOnClickPendingIntent(R.id.textView3, openMainIntent);
    }
}
