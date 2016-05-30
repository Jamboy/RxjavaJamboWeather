package com.example.jambo.rxjavajamboweather.service;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;

import com.example.jambo.rxjavajamboweather.R;
import com.example.jambo.rxjavajamboweather.activity.ShowWeatherActivity;
import com.example.jambo.rxjavajamboweather.util.ACache;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Jambo on 2016/5/24.
 */
public class AutoUpdateService extends Service {
    private ACache mACache;
    @Override
    public void onCreate() {
        super.onCreate();
        mACache = ACache.get(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
            showNotification(intent);
        new Thread(new Runnable() {
            @Override
            public void run() {
/**
 *          这里执行更新天气操作？
 */
//                updateWeather();
            }
        }).start();

        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        int anHour = 4 * 60 * 60 * 1000;
        long triggerAtTime = SystemClock.elapsedRealtime() + anHour;
        Intent i = new Intent(this,AutoUpdateService.class);
        PendingIntent pi = PendingIntent.getBroadcast(this,0,i,0);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
        return super.onStartCommand(intent, flags, startId);
    }


    private void showNotification(Intent intent){
        String weather = intent.getStringExtra("weather");
        Notification.Builder builder = new Notification.Builder(this);
        builder.setContentTitle(weather + "  " + intent.getStringExtra("temp") + "℃");
        Pattern pattern = Pattern.compile(".*雨");
        Matcher matcher = pattern.matcher(weather);
        if (matcher.matches()) {
            builder.setContentText( weather +"啊带伞啊不然等下又淋雨啊傻X");
        }else {
            builder.setContentText("好丑怎么自定义通知如何显示详细天气?");
        }
        builder.setSmallIcon(R.drawable.weather);
        Intent notificationIntent = new Intent(this, ShowWeatherActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        startForeground(1,builder.build());
    }


}
