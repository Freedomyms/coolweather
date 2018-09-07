package com.yangms.coolweather.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.yangms.coolweather.GlideApp;
import com.yangms.coolweather.JSON.Forecast;
import com.yangms.coolweather.JSON.Weather;
import com.yangms.coolweather.R;
import com.yangms.coolweather.service.AutoUpdateService;
import com.yangms.coolweather.util.HttpUtil;
import com.yangms.coolweather.util.PreUtil;
import com.yangms.coolweather.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.yangms.coolweather.Activity.MainActivity.WEATHER_ID;

public class WeatherActivity extends BaseActivity implements View.OnClickListener {
    public DrawerLayout drawerLayout;
    private Button mBtnNav;
    public SwipeRefreshLayout swipeRefresh;
    private ImageView mIvBingPicImg;
    private ScrollView mSvWeatherLayout;
    private TextView mTvTitleCity, mTvTitleUpdateTime;
    private TextView mTvDegreeText, mTvWeatherInfoText;
    private LinearLayout mLlForecastLayout;
    private TextView mTvDateText, mTvInfoText, mTvMaxText, mTvMinText;
    private TextView mTvAqiText, mTvPM25Text;
    private TextView mTvComfortText, mTvCarWashText, mTvSportText;

    public static final String WEATHER = "weather";
    private ProgressDialog progressDialog;
    public static final String BING_PIC = "bing_pic";
    private boolean isRefresh = false;//下拉刷新的时候Dialog不显示

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        initView();
        initData();

        transparentStatusBar();
    }

    private void initView() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mBtnNav = findViewById(R.id.nav_button);
        swipeRefresh = findViewById(R.id.swipe_refresh);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        mIvBingPicImg = findViewById(R.id.bing_pic_img);
        mSvWeatherLayout = findViewById(R.id.weather_layout);

        mTvTitleCity = findViewById(R.id.title_city);//城市名
        mTvTitleUpdateTime = findViewById(R.id.title_update_time);//天气更新时间

        mTvDegreeText = findViewById(R.id.degree_text);//显示当前气温
        mTvWeatherInfoText = findViewById(R.id.weather_info_text);// 显示天气概况

        mLlForecastLayout = findViewById(R.id.forecast_layout);//显示未来几天天气信息的布局

        mTvAqiText = findViewById(R.id.aqi_text);//显示AQI指数
        mTvPM25Text = findViewById(R.id.pm25_text);// 显示pm2.5指数

        mTvComfortText = findViewById(R.id.comfort_text);//舒适度
        mTvCarWashText = findViewById(R.id.car_wash_text);//洗车指数
        mTvSportText = findViewById(R.id.sport_text);//运动建议

        mBtnNav.setOnClickListener(this);
    }

    private void initData() {
        String weatherString = PreUtil.getString(WeatherActivity.this, WEATHER, null);
        final String weatherId;
        if (weatherString != null) {//有缓存直接解析天气数据
            Weather weather = Utility.handleWeatherResponse(weatherString);
            weatherId = weather.basic.weatherId;
            showWeatherInfo(weather);
        } else {//无缓存时去服务器查询天气
            weatherId = getIntent().getStringExtra(WEATHER_ID);
            mSvWeatherLayout.setVisibility(View.INVISIBLE);
            requestWeather(weatherId);
        }
        setOnSwipeRefreshListener(weatherId);
        String bingPic = PreUtil.getString(WeatherActivity.this, BING_PIC, null);
        if (bingPic != null) {
            GlideApp.with(this).load(bingPic).into(mIvBingPicImg);
        } else {
            loadBingPic();
        }

    }

    /**
     * 下拉刷新
     */
    private void setOnSwipeRefreshListener(final String weatherId) {
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isRefresh = true;
                requestWeather(weatherId);
            }
        });
    }

    /**
     * 透明状态栏
     */
    protected void transparentStatusBar() {
        //透明状态栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    private void requestWeather(final String weatherId) {
        if (!isRefresh) {
            showProgressDialog();
        }
        String weatherUrl = "http://guolin.tech/api/weather?cityid=" + weatherId + "&key=bc0418b57b2d4918819d3974ac1285d9";
        // String weatherUrl1 = "http://guolin.tech/api/weather?cityid=CN101090201&key=bc0418b57b2d4918819d3974ac1285d9";
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
                        if (!isRefresh) {
                            closeProgressDialog();
                        }
                        isRefresh = false;
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                final Weather weather = Utility.handleWeatherResponse(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (weather != null && "ok".equals(weather.status)) {
                            PreUtil.setString(WeatherActivity.this, WEATHER, responseText);
                            showWeatherInfo(weather);
                            if (!isRefresh) {
                                closeProgressDialog();
                            } else {
                                Toast.makeText(WeatherActivity.this, "天气更新成功", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(WeatherActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
                        }
                        isRefresh = false;
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }
        });
    }

    private void showWeatherInfo(Weather weather) {
        String cityName = weather.basic.cityName;
        String updateTime = weather.basic.update.updateTime.split(" ")[1];
        String degree = weather.now.temperature + "℃";
        String weatherInfo = weather.now.more.info;
        mTvTitleCity.setText(cityName);
        mTvTitleUpdateTime.setText(updateTime);
        mTvDegreeText.setText(degree);
        mTvWeatherInfoText.setText(weatherInfo);
        mLlForecastLayout.removeAllViews();
        for (Forecast forecast : weather.forecastList) {
            View view = LayoutInflater.from(this).inflate(R.layout.weather_forcast_item, mLlForecastLayout, false);
            mTvDateText = view.findViewById(R.id.date_text);//预报日期
            mTvInfoText = view.findViewById(R.id.info_text);// 天气概况
            mTvMaxText = view.findViewById(R.id.max_text);//最高温度
            mTvMinText = view.findViewById(R.id.min_text);// 最低温度
            mTvDateText.setText(forecast.date);
            mTvInfoText.setText(forecast.more.info);
            mTvMaxText.setText(forecast.temperature.max);
            mTvMinText.setText(forecast.temperature.min);
            mLlForecastLayout.addView(view);
        }
        if (weather.aqi != null) {
            mTvAqiText.setText(weather.aqi.city.aqi);
            mTvPM25Text.setText(weather.aqi.city.pm25);
        }
        String comfort = "" + weather.suggestion.comfort.info;
        String carWash = "" + weather.suggestion.carWash.info;
        String sport = " " + weather.suggestion.sport.info;
        mTvComfortText.setText(comfort);
        mTvCarWashText.setText(carWash);
        mTvSportText.setText(sport);
        mSvWeatherLayout.setVisibility(View.VISIBLE);

        Intent intent = new Intent(this, AutoUpdateService.class);
        startService(intent);
    }

    private void loadBingPic() {
        String requestBingPic = "http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String bingPic = response.body().string();
                PreUtil.setString(WeatherActivity.this, BING_PIC, bingPic);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        GlideApp.with(WeatherActivity.this).load(bingPic).into(mIvBingPicImg);
                    }
                });
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.nav_button:

                final RotateAnimation rotateAnim = new RotateAnimation(0, 90, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                rotateAnim.setDuration(500);
                rotateAnim.setFillAfter(true);
                mBtnNav.startAnimation(rotateAnim);
                rotateAnim.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        drawerLayout.openDrawer(GravityCompat.START);
                        rotateAnim.cancel();
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });

                break;
        }
    }

    /**
     * 显示进度对话框
     */
    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("正在加载...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    /**
     * 关闭进度对话框
     */
    private void closeProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void onBackPressed() {
        PreUtil.setString(WeatherActivity.this, WEATHER, null);
        PreUtil.setString(WeatherActivity.this, BING_PIC, null);
        super.onBackPressed();
    }
}

