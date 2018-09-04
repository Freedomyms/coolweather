package com.yangms.coolweather.JSON;

/**
 * Created by admin on 2018/9/4.
 */

public class AQI {
    public AQICity city;
    public class AQICity{
        public String aqi;
        public String pm25;
    }
}
