package com.example.jambo.rxjavajamboweather.util;

import com.example.jambo.rxjavajamboweather.mould.Weather;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jambo on 2016/5/12/012.
 */
public class WeatherApi {
    @SerializedName("HeWeather data service 3.0") @Expose
    public List<Weather> weatherList = new ArrayList<>();
}
