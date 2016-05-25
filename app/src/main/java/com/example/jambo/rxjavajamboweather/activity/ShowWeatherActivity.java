package com.example.jambo.rxjavajamboweather.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.MenuItem;
import android.view.Window;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jambo.rxjavajamboweather.R;
import com.example.jambo.rxjavajamboweather.mould.Weather;
import com.example.jambo.rxjavajamboweather.mould.WeatherAdapter;
import com.example.jambo.rxjavajamboweather.service.AutoUpdateService;
import com.example.jambo.rxjavajamboweather.util.ACache;
import com.example.jambo.rxjavajamboweather.util.HttpUtil;
import com.example.jambo.rxjavajamboweather.util.WeatherApi;

import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by Jambo on 2016/5/21.
 */
public class ShowWeatherActivity extends Activity {
    private ListView weather_list_view;
    public static final String KEY = "1f93bec9ad304eb2ae641280bd65b9df";
    private TextView city_text;
    private TextView temp_text;
    private TextView suggistion_text;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ACache mCache;
    private Observer<Weather> observer;
    private SharedPreferences preferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.drawer_weather);
        mCache = ACache.get(this);

        init();
        queryDate();
    }


    public void queryDate(){
        mSwipeRefreshLayout.setRefreshing(true);
        observer = new Observer<Weather>() {
            @Override
            public void onCompleted() {
                mSwipeRefreshLayout.setRefreshing(false);
                Toast.makeText(ShowWeatherActivity.this,"加载成功然后呢？", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(Throwable e) {
                Log.e("onError", e.getMessage());
            }

            @Override
            public void onNext(Weather weather) {
                temp_text.setText(weather.now.tmp+"℃");
                city_text.setText(weather.basic.city);
                suggistion_text.setText(weather.suggestion.drsg.txt);
                WeatherAdapter mAdapter = new WeatherAdapter(ShowWeatherActivity.this,R.layout.weather_forecast_list_item_text,weather.dailyForecast);
                weather_list_view.setAdapter(mAdapter);
//                showNotification(weather);
                Intent intent = new Intent(ShowWeatherActivity.this, AutoUpdateService.class);
                intent.putExtra("weather",weather.dailyForecast.get(0).cond.txtD);
                intent.putExtra("temp",weather.now.tmp);
                startService(intent);
            }
        };

        queryDataFromACache(observer);
    }


    public void queryDataFromACache(Observer<Weather> observer){
        Weather weather = null;
        try{
            weather = (Weather) mCache.getAsObject("WeatherData");
        }catch (Exception e){
            Log.e("Exception",e.getMessage());
        }

        if (weather != null)
            Observable.just(weather).distinct().subscribe(observer);
        else
            queryWeatherFromservice(observer);
    }


    public void queryWeatherFromservice(Observer<Weather> observer){
        String city_name = preferences.getString("city_name", "长沙");
        Log.d("Show", city_name + "service");
        HttpUtil.getApiService().getWeather(preferences.getString("city_name","长沙"), KEY)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Func1<WeatherApi, Weather>() {
                    @Override
                    public Weather call(WeatherApi weatherApi) {
                        return weatherApi.weatherList.get(0);
                    }
                })
                .doOnNext(new Action1<Weather>() {
                    @Override
                    public void call(Weather weather) {
                        mCache.put("WeatherData", weather, 3600);
                    }
                })
                .subscribe(observer);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//       这里是判断从哪个Acitivity返回的数据
        if (requestCode == 1 && resultCode == 2){
            Log.d("Show", data.getStringExtra("city_name") + "data");
            preferences.edit().putString("city_name",data.getStringExtra("city_name")).apply();
            queryWeatherFromservice(observer);
        }

    }

    public void init(){
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        weather_list_view = (ListView) findViewById(R.id.weather_forecast_list);
        city_text = (TextView) findViewById(R.id.city_name);
        temp_text = (TextView) findViewById(R.id.current_temp);
        suggistion_text = (TextView) findViewById(R.id.suggestion_text);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                    queryDate();
            }
        });
        final DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView mNavigationView = (NavigationView) findViewById(R.id.navigation_view);
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.chose_city:
                        Intent intent = new Intent(ShowWeatherActivity.this, ChoseCityActivity.class);
                        startActivityForResult(intent,1);
                        break;

                    case R.id.setting:
                        startActivity(new Intent(ShowWeatherActivity.this,SettingActivity.class));
                        break;

                    case R.id.exit:
                        System.exit(1);
                        break;

                    case R.id.about:
                        Toast.makeText(ShowWeatherActivity.this,"About me ?", Toast.LENGTH_LONG).show();
                        break;
                }
                drawerLayout.closeDrawer(GravityCompat.START);
                return false;
            }
        });
    }


//    public void showNotification(Weather weather){
//        Notification.Builder builder = new Notification.Builder(this);
//        builder.setContentText(weather.basic.city + "contentText");
//        builder.setSmallIcon(R.mipmap.weather);
//        builder.setContentTitle(weather.dailyForecast.get(0).cond.txtD);
//
//        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        manager.notify(1,builder.build());
//    }

}
