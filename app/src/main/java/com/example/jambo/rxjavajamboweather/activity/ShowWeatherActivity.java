package com.example.jambo.rxjavajamboweather.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
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
public class  ShowWeatherActivity extends AppCompatActivity implements AMapLocationListener, SwipeRefreshLayout.OnRefreshListener {

    public RecyclerView weather_recycler_view;
    public static final String KEY = "1f93bec9ad304eb2ae641280bd65b9df";
    //    private TextView city_text;
//    private TextView temp_text;
//    private TextView suggistion_text;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ACache mCache;
    private Observer<Weather> observer;
    private SharedPreferences preferences;
    private Toolbar toolbar;
    private CollapsingToolbarLayout collapsingToolbarLayout;

    private AMapLocationClient mLocationClient = null;
    private AMapLocationClientOption mLocationOption = null;
    private Boolean isLocation = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer_weather);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
        mCache = ACache.get(this);
        init();
        queryDate();
        location();
        if(isLocation){
            onRefresh();
//            queryWeatherFromservice(observer,preferences.getString("location_city","北京"));
//            Log.d("isLocation",preferences.getString("location_city","北京"));
        }
    }

    @Override
    public void onRefresh() {
        queryWeatherFromservice(observer,preferences.getString("location_city","衡阳"));
    }

    public void queryDate() {
        mSwipeRefreshLayout.setRefreshing(true);
        observer = new Observer<Weather>() {
            @Override
            public void onCompleted() {
                mSwipeRefreshLayout.setRefreshing(false);
                Toast.makeText(ShowWeatherActivity.this, "加载成功然后呢？", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(Throwable e) {
                Log.e("onError", e.getMessage());
            }

            @Override
            public void onNext(Weather weather) {
//                添加adapter
//                showNotification(weather);
                weather_recycler_view.setLayoutManager(new LinearLayoutManager(ShowWeatherActivity.this));
                WeatherAdapter mAdapter = new WeatherAdapter(ShowWeatherActivity.this, weather);
                weather_recycler_view.setAdapter(mAdapter);
                Intent intent = new Intent(ShowWeatherActivity.this, AutoUpdateService.class);
                intent.putExtra("weather", weather.dailyForecast.get(0).cond.txtD);
                intent.putExtra("temp", weather.now.tmp);
                startService(intent);
            }
        };

        queryDataFromACache(observer);
    }


    public void queryDataFromACache(Observer<Weather> observer) {
        Weather weather = null;
        try {
            weather = (Weather) mCache.getAsObject("WeatherData");
        } catch (Exception e) {
            Log.e("Exception", e.getMessage());
        }

        if (weather != null)
            Observable.just(weather).distinct().subscribe(observer);
        else
            queryWeatherFromservice(observer,preferences.getString("city_name","北京"));
    }


    public void queryWeatherFromservice(Observer<Weather> observer, String city_name) {
        Log.d("Show", city_name + "service");
        HttpUtil.getApiService().getWeather(city_name, KEY)
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
        if (requestCode == 1 && resultCode == 2) {
            Log.d("Show", data.getStringExtra("city_name") + "data");
            preferences.edit().putString("city_name", data.getStringExtra("city_name")).apply();
            queryWeatherFromservice(observer,preferences.getString("city_name","北京"));
        }

    }

    public void init() {
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        weather_recycler_view = (RecyclerView) findViewById(R.id.recycler_view);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        collapsingToolbarLayout.setTitle(" ");

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView mNavigationView = (NavigationView) findViewById(R.id.navigation_view);

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.chose_city:
                        Intent intent = new Intent(ShowWeatherActivity.this, ChoseCityActivity.class);
                        startActivityForResult(intent, 1);
                        break;

                    case R.id.setting:
                        startActivity(new Intent(ShowWeatherActivity.this, SettingActivity.class));
                        break;

                    case R.id.exit:
                        System.exit(1);
                        break;

                    case R.id.about:
                        Toast.makeText(ShowWeatherActivity.this, "About me ?", Toast.LENGTH_LONG).show();
                        break;
                }
                drawerLayout.closeDrawer(GravityCompat.START);
                return false;
            }
        });
    }


    /**
     * 高德定位
     */
    private void location() {
        //初始化定位
        mLocationClient = new AMapLocationClient(getApplicationContext());
        //设置定位回调监听
        mLocationClient.setLocationListener(this);
        mLocationOption = new AMapLocationClientOption();
        //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Battery_Saving);
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
        //设置是否只定位一次,默认为false
        mLocationOption.setOnceLocation(false);
        //设置是否强制刷新WIFI，默认为强制刷新
        mLocationOption.setWifiActiveScan(true);
        //设置是否允许模拟位置,默认为false，不允许模拟位置
        mLocationOption.setMockEnable(false);
        //设置定位间隔 单位毫秒
        mLocationOption.setInterval(24 * 3600 * 1000);
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        //启动定位
        mLocationClient.startLocation();
    }


    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null) {
            if (aMapLocation.getErrorCode() == 0) {
                //定位成功回调信息，设置相关消息
                aMapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见定位类型表
                //aMapLocation.getLatitude();//获取纬度
                //aMapLocation.getLongitude();//获取经度
                //aMapLocation.getAccuracy();//获取精度信息
                //SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                //Date date = new Date(aMapLocation.getTime());
                //df.format(date);//定位时间
                //aMapLocation.getAddress();//地址，如果option中设置isNeedAddress为false，则没有此结果，网络定位结果中会有地址信息，GPS定位不返回地址信息。
                //aMapLocation.getCountry();//国家信息
                //aMapLocation.getProvince();//省信息
                //aMapLocation.getCity();//城市信息
                //aMapLocation.getDistrict();//城区信息
                //aMapLocation.getStreet();//街道信息
                //aMapLocation.getStreetNum();//街道门牌号信息
                //aMapLocation.getCityCode();//城市编码
                //aMapLocation.getAdCode();//地区编码
                String location_city = aMapLocation.getCity().replace("市","")
                        .replace("省","")
                        .replace("土家族苗族自治州","")
                        .replace("自治区","")
                        .replace("特别行政区","")
                        .replace("地区","")
                        .replace("盟","");
                preferences.edit().putString("location_city",location_city).apply();
                isLocation = true;
                Log.d(location_city,"当前城市");
            }
            else {
                //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                Log.e("AmapError", "location Error, ErrCode:" + aMapLocation.getErrorCode() + ", errInfo:" +
                        aMapLocation.getErrorInfo());
            }
        }
    }
}
