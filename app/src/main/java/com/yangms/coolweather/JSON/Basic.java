package com.yangms.coolweather.JSON;

import com.google.gson.annotations.SerializedName;

/**
 * Created by admin on 2018/9/4.
 */

public class Basic {
    @SerializedName("city")
    public String cityName;

    @SerializedName("id")
    public String weatherId;

    public Update update;
    public class Update{
        @SerializedName("loc")
        public String updateTime;
    }
}
