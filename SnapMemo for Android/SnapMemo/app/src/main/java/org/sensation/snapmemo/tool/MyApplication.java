package org.sensation.snapmemo.tool;

import android.app.Application;
import android.content.Context;

import org.sensation.snapmemo.R;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by Alan on 2016/2/5.
 */
public class MyApplication extends Application {

    private static Context context;

    public static Context getContext() {
        return context;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        CalligraphyConfig.initDefault("fonts/msyhl.ttf", R.attr.fontPath);
    }
}
