package com.example.jambo.rxjavajamboweather.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.MenuItem;
import android.view.Window;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jambo.rxjavajamboweather.R;
import com.example.jambo.rxjavajamboweather.mould.Weather;
import com.example.jambo.rxjavajamboweather.mould.WeatherAdapter;
import com.example.jambo.rxjavajamboweather.util.HttpUtil;
import com.example.jambo.rxjavajamboweather.util.WeatherApi;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by Jambo on 2016/5/21.
 */
public class ShowWeatherActivity extends Activity {
    private ListView weather_list_view;
    private static final String KEY = "1f93bec9ad304eb2ae641280bd65b9df";
    private TextView city_text;
    private TextView temp_text;
    private TextView suggistion_text;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.drawer_weather);
        weather_list_view = (ListView) findViewById(R.id.weather_forecast_list);
        city_text = (TextView) findViewById(R.id.city_name);
        temp_text = (TextView) findViewById(R.id.current_temp);
        suggistion_text = (TextView) findViewById(R.id.suggestion_text);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                queryWeatherFromservice();
            }
        });
        queryWeatherFromservice();
        init();
    }

    public void queryWeatherFromservice(){
        mSwipeRefreshLayout.setRefreshing(true);
        HttpUtil.getApiService().getWeather("长沙", KEY)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Func1<WeatherApi, Weather>() {
                    @Override
                    public Weather call(WeatherApi weatherApi) {
                        return weatherApi.weatherList.get(0);
                    }
                })
                .subscribe(new Observer<Weather>() {
                    @Override
                    public void onCompleted() {
                        Toast.makeText(ShowWeatherActivity.this,"加载成功然后呢?",Toast.LENGTH_LONG).show();
                        mSwipeRefreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Weather weather) {
                        city_text.setText(weather.basic.city);
                        temp_text.setText(weather.now.tmp + "℃");
                        suggistion_text.setText(weather.suggestion.drsg.txt);
                        WeatherAdapter mAdapter = new WeatherAdapter(ShowWeatherActivity.this, R.layout.weather_forecast_list_item_text, weather.dailyForecast);
                        weather_list_view.setAdapter(mAdapter);
                    }
                });
    }


    public void init(){
        NavigationView mNavigationView = (NavigationView) findViewById(R.id.navigation_view);
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.chose_city:
                        Intent intent = new Intent(ShowWeatherActivity.this, ChoseCityActivity.class);
                        startActivity(intent);
                        break;

                    case R.id.setting:
                        Intent i = new Intent(ShowWeatherActivity.this, SettingActivity.class);
                        startActivity(i);
                        break;

                    case R.id.exit:
                        System.exit(1);
                        break;

                    case R.id.about:
                        Toast.makeText(ShowWeatherActivity.this,"About me ?", Toast.LENGTH_LONG).show();
                        break;
                }
                return true;
            }
        });
    }
}
