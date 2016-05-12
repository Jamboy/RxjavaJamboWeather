package com.example.jambo.rxjavajamboweather.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.example.jambo.rxjavajamboweather.mould.Weather;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Jambo on 2016/5/12/012.
 */
public class Utility {
    public static void saveWeatherInfo(Weather weather, Context context){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年M月d日", Locale.CHINA);
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        String city_name = weather.basic.city;
        String temp1 = weather.dailyForecast.get(0).tmp.min;
        String temp2 = weather.dailyForecast.get(0).tmp.max;
        String weather_desp = weather.now.cond.txt;
        String current_time = weather.basic.update.loc;
        String [] loc = current_time.split(" ");
        String publish_time = loc[1];

        editor.putBoolean("city_selected", true);
        editor.putString("city_name",city_name);
        editor.putString("temp1",temp1);
        editor.putString("temp2",temp2);
        editor.putString("weather_desp",weather_desp);
        editor.putString("publish_time",publish_time);
        editor.putString("current_date",simpleDateFormat.format(new Date()));
        editor.commit();
    }
}
