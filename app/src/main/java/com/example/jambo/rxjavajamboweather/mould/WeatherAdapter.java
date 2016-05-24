package com.example.jambo.rxjavajamboweather.mould;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.jambo.rxjavajamboweather.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Jambo on 2016/5/20.
 */
public class WeatherAdapter extends ArrayAdapter<Weather.DailyForecastEntity>{

    private int resourceId;

    public WeatherAdapter(Context context, int resource, List<Weather.DailyForecastEntity> dailyForecastEntityList) {
        super(context, resource, dailyForecastEntityList);
        resourceId = resource;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(resourceId, null);
            viewHolder = new ViewHolder();
            viewHolder.day = (TextView) convertView.findViewById(R.id.day);
            viewHolder.description = (TextView)convertView.findViewById(R.id.description);
            viewHolder.temp1 = (TextView) convertView.findViewById(R.id.max_temp);
            viewHolder.temp2 = (TextView) convertView.findViewById(R.id.min_temp);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Weather.DailyForecastEntity mWeather = getItem(position);
    try {
        if (position == 0) {
            viewHolder.day.setText("Today");
        } else if (position == 1){
            viewHolder.day.setText("Tomorrow");
        }else {
            viewHolder.day.setText(dayForWeek(mWeather.date));
        }
            viewHolder.description.setText(mWeather.cond.txtD);
            viewHolder.temp1.setText(mWeather.tmp.max + "℃");
            viewHolder.temp2.setText(mWeather.tmp.min + "℃");

    }catch (Exception e){
        e.printStackTrace();
    }finally {
            return convertView;
    }
}


    public class ViewHolder{
        private TextView day;
        private TextView description;
        private TextView temp1;
        private TextView temp2;
    }


    public String dayForWeek(String Time) throws Exception{
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
