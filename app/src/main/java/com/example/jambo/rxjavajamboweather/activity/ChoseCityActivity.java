package com.example.jambo.rxjavajamboweather.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.jambo.rxjavajamboweather.R;
import com.example.jambo.rxjavajamboweather.WeatherActivity;
import com.example.jambo.rxjavajamboweather.db.DBManager;
import com.example.jambo.rxjavajamboweather.db.WeatherDB;
import com.example.jambo.rxjavajamboweather.mould.City;
import com.example.jambo.rxjavajamboweather.mould.Province;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func0;
import rx.schedulers.Schedulers;

/**
 * Created by Jambo on 2016/5/12/012.
 */
public class ChoseCityActivity extends Activity{

    private TextView title_text;
    private ListView listView;
    private List<String> dataList = new ArrayList<>();
    private List<Province> provinceList;
    private List<City> cityList;
    private ArrayAdapter<String> adapter;
    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    private int currentLevel;
    public DBManager mDBManager;
    public WeatherDB mWeatherDB;
    private Province selectedProvince;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.chose_city);
        init();
        setListView();

    }


    public void setListView(){
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (currentLevel == LEVEL_PROVINCE){
                    selectedProvince = provinceList.get(position);
                    queryCities();
                }else if (currentLevel == LEVEL_CITY){
                    String city_name = cityList.get(position).getCityName();
                        if (city_name != null){
                            city_name = city_name.replace("市","")
                                    .replace("省","")
                                    .replace("土家族苗族自治州","")
                                    .replace("自治区","")
                                    .replace("特别行政区","")
                                    .replace("地区","")
                                    .replace("盟","");
                    }
                    Intent intent = new Intent(ChoseCityActivity.this, WeatherActivity.class);
                    intent.putExtra("city_name",city_name);
                    startActivity(intent);
                }
            }
        });
        queryPronvinces();
    }

    public void init(){
        listView = (ListView) findViewById(R.id.chose_list_view);
        title_text = (TextView) findViewById(R.id.title_text);
        mDBManager = new DBManager(this);
        mDBManager.openDatabase();
        mWeatherDB = new WeatherDB(this);
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,dataList);
        listView.setAdapter(adapter);
    }


    public void queryPronvinces(){

        title_text.setText("选择省份");
        dataList.clear();
        Observer<Province> observer = new Observer<Province>() {
            @Override
            public void onCompleted() {
                adapter.notifyDataSetChanged();
                currentLevel = LEVEL_PROVINCE;
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Province province) {
                dataList.add(province.getProvinceName());
            }
        };

        Observable.defer(new Func0<Observable<Province>>() {
            @Override
            public Observable<Province> call() {
                provinceList = mWeatherDB.loadProvinces(mDBManager.getDatabase());
                return Observable.from(provinceList);
            }
        }).subscribeOn(Schedulers.io())
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe(observer);
    }


    public void queryCities(){
        title_text.setText("选择城市");
        dataList.clear();

        Observer<City> observer = new Observer<City>() {
            @Override
            public void onCompleted() {
                adapter.notifyDataSetChanged();
                currentLevel = LEVEL_CITY;
            }

            @Override
            public void onError(Throwable e) {
                Log.e("ChoseCity",e.getMessage());
            }

            @Override
            public void onNext(City city) {
                dataList.add(city.getCityName());
            }
        };

        Observable.defer(new Func0<Observable<City>>() {
            @Override
            public Observable<City> call() {
                cityList = mWeatherDB.loadCities(mDBManager.getDatabase(),selectedProvince.getProSort());
                return Observable.from(cityList);
            }
        })
         .subscribeOn(Schedulers.io())
         .observeOn(AndroidSchedulers.mainThread())
         .subscribe(observer);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
