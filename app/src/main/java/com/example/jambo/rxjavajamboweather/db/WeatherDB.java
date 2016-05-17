package com.example.jambo.rxjavajamboweather.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.jambo.rxjavajamboweather.mould.City;
import com.example.jambo.rxjavajamboweather.mould.Province;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jambo on 2016/5/12/012.
 */
public class WeatherDB {

    public Context context;

    public WeatherDB(Context context){
        this.context = context;
    }

    public List<Province> loadProvinces(SQLiteDatabase db){
        List<Province> provinceList = new ArrayList<>();
        Cursor cursor = db.query("T_Province",null,null,null,null,null,null);
        if (cursor.moveToFirst()){
            do {
                Province province = new Province();
                province.setProvinceName(cursor.getString(cursor.getColumnIndex("ProName")));
                province.setProSort(cursor.getInt(cursor.getColumnIndex("ProSort")));
                provinceList.add(province);
            }while (cursor.moveToNext());
        }

        return provinceList;
    }


    public List<City> loadCities(SQLiteDatabase db, int ProSort){
        List<City> cityList = new ArrayList<>();
        Cursor cursor = db.query("T_City",null,"ProId = ?",new String[]{String.valueOf(ProSort)},null,null,null);
        if (cursor.moveToFirst()){
            do {
                City city = new City();
                city.setCityName(cursor.getString(cursor.getColumnIndex("CityName")));
                city.setProId(ProSort);
                cityList.add(city);

            }while (cursor.moveToNext());
        }

        return cityList;
    }
}
