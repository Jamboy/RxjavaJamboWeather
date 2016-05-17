package com.example.jambo.rxjavajamboweather;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jambo.rxjavajamboweather.mould.Weather;
import com.example.jambo.rxjavajamboweather.util.HttpUtil;
import com.example.jambo.rxjavajamboweather.util.Utility;
import com.example.jambo.rxjavajamboweather.util.WeatherApi;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class WeatherActivity extends Activity {
    public static final String key = "1f93bec9ad304eb2ae641280bd65b9df";
    public static String CITY_NAME =null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_activity);
        CITY_NAME = getIntent().getStringExtra("city_name");
        if (savedInstanceState == null){
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new ListFragment())
                    .commit();
        }

    }

    public static class ListFragment extends android.app.Fragment {
        private TextView city_text;
        private TextView current_temp;
        private ListView weather_forecast_list;
        private SwipeRefreshLayout mSwipeRefreshLayout;


        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

            View view = inflater.inflate(R.layout.show_weather,container,false);

            city_text = (TextView) view.findViewById(R.id.city_name);
            current_temp = (TextView) view.findViewById(R.id.current_temp);
            weather_forecast_list = (ListView) view.findViewById(R.id.weather_forecast_list);
            mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefresh_layout);
            mSwipeRefreshLayout.setColorSchemeResources(R.color.brand_main,
                    android.R.color.black,
                    R.color.brand_main,
                    android.R.color.black);
            mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    queryWeatherFromService();
                }
            });
            WeatherAdapter mAdapter = new WeatherAdapter(getActivity(),R.layout.weather_forecast_list_item_text,new ArrayList<Weather.DailyForecastEntity>());
            weather_forecast_list.setAdapter(mAdapter);
            queryWeatherFromService();
            return view;
        }

        @Override
        public void onDestroyView() {
            super.onDestroyView();
        }

        public  void queryWeatherFromService() {
            mSwipeRefreshLayout.setRefreshing(true);
//            String city_name = "北京";
            HttpUtil.getApiService().getWeather(CITY_NAME, key)
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
                            Utility.saveWeatherInfo(weather, getActivity());
                        }
                    })
                    .subscribe(new Observer<Weather>() {
                        @Override
                        public void onCompleted() {
                            Toast.makeText(getActivity(), "loading onCompleted", Toast.LENGTH_SHORT).show();
                            mSwipeRefreshLayout.setRefreshing(false);
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.e("WeatherActivity", e.getMessage());
                        }

                        @Override
                        public void onNext(Weather weather) {
                            city_text.setText(weather.basic.city);
                            current_temp.setText(weather.now.tmp + "℃");
                            List<Weather.DailyForecastEntity> list = weather.dailyForecast;
//                            list = weather.dailyForecast;++++
                            WeatherAdapter adapter = (WeatherAdapter) weather_forecast_list.getAdapter();
                            adapter.clear();
                            adapter.addAll(list);
                        }
                    });

        }
    }

    public static class WeatherAdapter extends ArrayAdapter<Weather.DailyForecastEntity> {
        private int resoureId;

        public WeatherAdapter(Context context, int textViewResourceId, List<Weather.DailyForecastEntity> list) {
            super(context,textViewResourceId, list);
            Log.d("Weather","WeatherAdapter");
            resoureId = textViewResourceId;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            ViewHolder viewHolder;
            if (convertView == null){
                convertView = LayoutInflater.from(getContext()).inflate(resoureId,null);
                viewHolder = new ViewHolder();
                viewHolder.day = (TextView) convertView.findViewById(R.id.day);
                viewHolder.description = (TextView) convertView.findViewById(R.id.description);
                viewHolder.temp1 = (TextView) convertView.findViewById(R.id.min_temp);
                viewHolder.temp2 = (TextView) convertView.findViewById(R.id.max_temp);
                convertView.setTag(viewHolder);
            }else{
                viewHolder = (ViewHolder) convertView.getTag();
            }
//           List<Weather.DailyForecastEntity> dailyForecastEntity = getItem(position).dailyForecast;
//            for (int i=0; i< dailyForecastEntity.size(); i++){
//                Weather.DailyForecastEntity date = dailyForecastEntity.get(i);
//                try {
//                    viewHolder.day.setText(dayForWeek(date.date));
//                    viewHolder.temp1.setText(date.tmp.min);
//                    viewHolder.temp2.setText(date.tmp.max);
//                    viewHolder.description.setText(date.cond.txtD);
//                }catch (Exception e) {
//                    e.printStackTrace();
//                    Log.d("WeatherAdapter", "WeatherAdapter error");
//                }
//            }
//            Weather.DailyForecastEntity date = dailyForecastEntity.get(0);
//            Weather weather = getItem(position);
            Weather.DailyForecastEntity date = getItem(position);
            try {
                viewHolder.day.setText(dayForWeek(date.date));
                viewHolder.temp1.setText(date.tmp.min + "℃");
                viewHolder.temp2.setText(date.tmp.max + "℃");
                viewHolder.description.setText(date.cond.txtD);
            }catch (Exception e) {
                e.printStackTrace();
                Log.d("WeatherAdapter", "WeatherAdapter error");
            }finally {
                return convertView;
            }
//        return convertView;
        }


        private class ViewHolder{
            private TextView day;
            private TextView description;
            private TextView temp1;
            private TextView temp2;
        }


        public static String dayForWeek(String Time) throws Exception{
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(simpleDateFormat.parse(Time));
            int dayForWeek = 0;
            String week = "";
            dayForWeek = calendar.get(Calendar.DAY_OF_WEEK);
            switch (dayForWeek){
                case 1:
                    week = "Sunday";
                    break;
                case 2:
                    week = "Monday";
                    break;
                case 3:
                    week = "Thusday";
                    break;
                case 4:
                    week = "Wednesday";
                    break;
                case 5:
                    week = "Thursday";
                    break;
                case 6:
                    week = "Friday";
                    break;
                case 7:
                    week = "Saturday";
                    break;
            }
            return week;
        }
    }
//
//    public static List<WeatherTest> init(){
//        List<WeatherTest> list = new ArrayList<>();
//        WeatherTest test = new WeatherTest("2016-4-4","15","10","晴天");
//        WeatherTest test1 = new WeatherTest("2016-4-5","16","10","阴天");
//        list.add(test);
//        list.add(test1);
//        return list;
//    }


}
