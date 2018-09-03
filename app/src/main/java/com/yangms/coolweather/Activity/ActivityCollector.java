package com.yangms.coolweather.Activity;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 2018/9/3.
 */

public class ActivityCollector{

    private static List<Activity> activities = new ArrayList<Activity>();
    private static Activity currentActivity = null;

    /**
     * 添加当前界面，在BaseActivity的onCreate()里面使用
     *
     * @param activity
     */
    public static void addActivity(Activity activity) {
        activities.add(activity);
    }

    /**
     * 设置当前界面，方便get使用，在在BaseActivity的onResume()里面使用
     *
     * @param currentActivity
     */
    public static void setCurrentActivity(Activity currentActivity) {
        ActivityCollector.currentActivity = currentActivity;
    }

    /**
     * 获取当前界面
     *
     * @return
     */
    public static Activity getCurrentActivity() {
        return currentActivity;
    }

    /**
     * 移除当前界面，在BaseActivity的onDestroy()里面使用
     *
     * @param activity
     */
    public static void removeActivity(Activity activity) {
        activities.remove(activity);
    }

    /**
     * 结束所有界面
     */
    public static void finishAll() {
        for (Activity activity : activities) {
            if (!activity.isFinishing()) {
                activity.finish();
            }
        }
    }


}