package com.yangms.coolweather.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by sunhy on 2018/4/15.
 * MDC_Public
 * SharePreference的封装
 */

public class PreUtil {
    public static boolean getBoolean(Context ctx, String key, boolean defValue) {
        @SuppressLint("WrongConstant") SharedPreferences sp = ctx.getSharedPreferences("config", Context.MODE_APPEND);
        return sp.getBoolean(key, defValue);
    }

    public static void setBoolean(Context ctx, String key, boolean value) {
        @SuppressLint("WrongConstant") SharedPreferences sp = ctx.getSharedPreferences("config", Context.MODE_APPEND);
        sp.edit().putBoolean(key, value).apply();
    }

    public static void setString(Context ctx, String key, String value) {
        @SuppressLint("WrongConstant") SharedPreferences sp = ctx.getSharedPreferences("config", Context.MODE_APPEND);
        sp.edit().putString(key, value).apply();
    }

    public static String getString(Context ctx, String key, String deFvalue) {
        @SuppressLint("WrongConstant") SharedPreferences sp = ctx.getSharedPreferences("config", Context.MODE_APPEND);
        return sp.getString(key, deFvalue);
    }

    public static void setInt(Context ctx, String key, int value) {
        @SuppressLint("WrongConstant") SharedPreferences sp = ctx.getSharedPreferences("config", Context.MODE_APPEND);
        sp.edit().putInt(key, value).apply();
    }

    public static int getInt(Context ctx, String key, int deFvalue) {
        @SuppressLint("WrongConstant") SharedPreferences sp = ctx.getSharedPreferences("config", Context.MODE_APPEND);
        return sp.getInt(key, deFvalue);
    }
}
