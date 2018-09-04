package com.yangms.coolweather.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.yangms.coolweather.JSON.Forecast;
import com.yangms.coolweather.JSON.Weather;
import com.yangms.coolweather.R;
import com.yangms.coolweather.util.HttpUtil;
import com.yangms.coolweather.util.PreUtil;
import com.yangms.coolweather.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.yangms.coolweather.Activity.MainActivity.WEATHER_ID;

public class WeatherActivity extends AppCompatActivity {
    private ScrollView mSvWeatherLayout;
    private TextView mTvTitleCity, mTvTitleUpdateTime;
    private TextView mTvDegreeText, mTvWeatherInfoText;
    private LinearLayout mLlForecastLayout;
    private TextView mTvDateText, mTvInfoText, mTvMaxText, mTvMinText;
    private TextView mTvAqiText, mTvPM25Text;
    private TextView mTvComfortText, mTvCarWashText, mTvSportText;

    private static final String WEATHER="weather";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        initView();
        initData();
    }

    private void initView() {
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
    }

    private void initData() {
        String weatherString=PreUtil.getString(WeatherActivity.this,WEATHER,null);
        if(weatherString!=null){
            Weather weather = Utility.handleWeatherResponse(weatherString);
        }else {
            String weatherId = getIntent().getStringExtra(WEATHER_ID);
            mSvWeatherLayout.setVisibility(View.INVISIBLE);
            requestWeather(weatherId);
        }
    }
    private void requestWeather(final String weatherId){
        String weatherUrl = "http://guolin.tech/api/weather?cityid="+weatherId+"&key=bc0418b57b2d4918819d3974ac1285d9";
       // String weatherUrl1 = "http://guolin.tech/api/weather?cityid=CN101090201&key=bc0418b57b2d4918819d3974ac1285d9";
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this,"获取天气信息失败",Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText=response.body().string();
                final Weather weather = Utility.handleWeatherResponse(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(weather!=null&&"ok".equals(weather.status)){
                            PreUtil.setString(WeatherActivity.this,WEATHER,responseText);
                            showWeatherInfo(weather);
                        }
                    }
                });
            }
        });
    }
    private void showWeatherInfo(Weather weather){
        String cityName=weather.basic.cityName;
        String updateTime = weather.basic.update.updateTime.split(" ")[1];
        String degree = weather.now.temperature+"℃";
        String weatherInfo=weather.now.more.info;
        mTvTitleCity.setText(cityName);
        mTvTitleUpdateTime.setText(updateTime);
        mTvDegreeText.setText(degree);
        mTvWeatherInfoText.setText(weatherInfo);
        mLlForecastLayout.removeAllViews();
        for(Forecast forecast:weather.forecastList){
            View view= LayoutInflater.from(this).inflate(R.layout.weather_forcast_item,mLlForecastLayout,false);
            mTvDateText = findViewById(R.id.date_text);//预报日期
            mTvInfoText = findViewById(R.id.info_text);// 天气概况
            mTvMaxText = findViewById(R.id.max_text);//最高温度
            mTvMinText = findViewById(R.id.min_text);// 最低温度
            //mTvDateText.setText(forecast.date);
            mTvInfoText.setText(forecast.more.info);
            mTvMaxText.setText(forecast.temperature.max);
            mTvMinText.setText(forecast.temperature.min);
            mLlForecastLayout.addView(view);
        }
        if(weather.aqi!=null){
            mTvAqiText.setText(weather.aqi.city.aqi);
            mTvPM25Text.setText(weather.aqi.city.pm25);
        }
        String comfort = ""+weather.suggestion.comfort.info;
        String carWash="" +weather.suggestion.carWash.info;
        String sport =" "+weather.suggestion.sport.info;
        mTvComfortText.setText(comfort);
        mTvCarWashText.setText(carWash);
        mTvSportText.setText(sport);
        mSvWeatherLayout.setVisibility(View.VISIBLE);
    }
}

