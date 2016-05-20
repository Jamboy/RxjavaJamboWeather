package com.example.jambo.rxjavajamboweather.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.example.jambo.rxjavajamboweather.WeatherActivity;

/**
 * Created by Jambo on 2016/5/19.
 */
public class AutoUpdateWeather extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        new WeatherActivity.ListFragment().queryWeatherFromService();
        return super.onStartCommand(intent, flags, startId);

    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
