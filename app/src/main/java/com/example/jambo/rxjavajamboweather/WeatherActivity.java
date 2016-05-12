package com.example.jambo.rxjavajamboweather;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jambo.rxjavajamboweather.mould.Weather;
import com.example.jambo.rxjavajamboweather.util.HttpUtil;
import com.example.jambo.rxjavajamboweather.util.Utility;
import com.example.jambo.rxjavajamboweather.util.WeatherApi;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class WeatherActivity extends AppCompatActivity {
     private TextView textView;
    public static final String key = "1f93bec9ad304eb2ae641280bd65b9df";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = (TextView) findViewById(R.id.city_name);
        String city_name = getIntent().getStringExtra("city_name");
        if (city_name != null){
            queryWeatherFromService(city_name);
        }else {
            showWeather();
        }


    }


    public void queryWeatherFromService(String city_name){
        HttpUtil.getApiService().getWeather(city_name,key)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .filter(new Func1<WeatherApi, Boolean>() {
                    @Override
                    public Boolean call(WeatherApi weatherApi) {
                        return weatherApi.weatherList.get(0).status.equals("ok");
                    }
                })
                .map(new Func1<WeatherApi, Weather>() {
                    @Override
                    public Weather call(WeatherApi weatherApi) {
                        return weatherApi.weatherList.get(0);
                    }
                })
                .doOnNext(new Action1<Weather>() {
                    @Override
                    public void call(Weather weather) {
                        Utility.saveWeatherInfo(weather,WeatherActivity.this);
                    }
                })
                .subscribe(new Observer<Weather>() {
                    @Override
                    public void onCompleted() {
                        Toast.makeText(WeatherActivity.this,"WeatherActivity onCompleted", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("WeatherActivity", e.getMessage());
                    }

                    @Override
                    public void onNext(Weather weather) {
                        showWeather();
                    }
                });

    }


    public void showWeather(){

    }
}
