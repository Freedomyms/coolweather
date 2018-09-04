package com.yangms.coolweather.JSON;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by admin on 2018/9/4.
 */

public class Weather {

    public String status;

    public Basic basic;

    public AQI aqi;

    public Now now;

    public Suggestion suggestion;

    @SerializedName("daily_forecast")
    public List<Forecast> forecastList;
}
