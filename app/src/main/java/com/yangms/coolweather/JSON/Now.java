package com.yangms.coolweather.JSON;

import com.google.gson.annotations.SerializedName;

/**
 * Created by admin on 2018/9/4.
 */

public class Now {
    @SerializedName("tmp")
    public String temperature;

    @SerializedName("cond")
    public More more;

    public class More{
        @SerializedName("txt")
        public String info;
    }
}
