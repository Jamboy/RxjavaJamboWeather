package com.example.jambo.rxjavajamboweather.util;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by Jambo on 2016/5/12/012.
 */
public interface ApiInterface {
    String BASE_URL = "https://api.heweather.com/x3/";
    @GET("weather")
    Observable<WeatherApi> getWeather(@Query("city") String city, @Query("key") String key);
}
