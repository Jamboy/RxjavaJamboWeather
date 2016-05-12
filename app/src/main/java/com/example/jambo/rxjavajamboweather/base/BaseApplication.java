package com.example.jambo.rxjavajamboweather.base;

import android.app.Application;

/**
 * Created by Jambo on 2016/5/12/012.
 */
public class BaseApplication extends Application {
    public static BaseApplication instance;


    public static BaseApplication getInstance(){
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }
}

