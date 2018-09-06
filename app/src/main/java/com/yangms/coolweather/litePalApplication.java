package com.yangms.coolweather;

import android.app.Application;
import android.content.Context;

/**
 * Created by admin on 2018/9/3.
 */

public class litePalApplication extends Application {
    public static litePalApplication MyInstance;
    private static Context context;
    @Override
    public void onCreate() {
        super.onCreate();
        MyInstance = this;
        context = getApplicationContext();
    }

    public static Context getContextObject() {
        return context;
    }

    public static litePalApplication getMyInstance() {
        return MyInstance;
    }
}
