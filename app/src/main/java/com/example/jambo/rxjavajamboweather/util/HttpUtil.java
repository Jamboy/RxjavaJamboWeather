package com.example.jambo.rxjavajamboweather.util;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Jambo on 2016/5/12/012.
 */
public class HttpUtil {

    public static Retrofit retrofit = null;
    public static ApiInterface apiService = null;

    public static void init(){
        retrofit = new Retrofit.Builder()
                .baseUrl(ApiInterface.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();

        apiService = retrofit.create(ApiInterface.class);
    }

    public static ApiInterface getApiService(){
        if (apiService != null) return apiService;
        init();
        return getApiService();
    }



}
