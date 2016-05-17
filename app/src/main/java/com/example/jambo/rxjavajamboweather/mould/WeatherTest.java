package com.example.jambo.rxjavajamboweather.mould;

/**
 * Created by Jambo on 2016/5/17.
 */
public class WeatherTest {
    private String date;
    private String max;
    private String min;
    private String descri;

    public WeatherTest(String date, String max, String min, String descri) {
        this.date = date;
        this.max = max;
        this.min = min;
        this.descri = descri;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMax() {
        return max;
    }

    public void setMax(String max) {
        this.max = max;
    }

    public String getMin() {
        return min;
    }

    public void setMin(String min) {
        this.min = min;
    }

    public String getDescri() {
        return descri;
    }

    public void setDescri(String descri) {
        this.descri = descri;
    }
}
