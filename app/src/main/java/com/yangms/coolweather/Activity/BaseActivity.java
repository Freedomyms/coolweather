package com.yangms.coolweather.Activity;

import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

/**
 * Created by admin on 2018/9/3.
 */

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        hideTitleBar();
        super.onCreate(savedInstanceState);

        initTitleBarTextColor();
        initScreenOrientation();
        //调整软键盘，防止聊天遮挡
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN|WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        ActivityCollector.addActivity(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        ActivityCollector.setCurrentActivity(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }

    protected void initTitleBarTextColor() {
        //设置状态栏文字颜色及图标为深色,当状态栏为深色，状态栏文字就是白色；当状态栏为浅色，文字就是黑色
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Window window = getWindow();
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY );
        }
    }


    private void initScreenOrientation() {
        //设置竖屏  禁止旋转
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }
    /**
     * 不显示标题栏
     */
    protected void hideTitleBar() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉信息栏
    }
}