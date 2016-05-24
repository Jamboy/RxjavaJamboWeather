package com.example.jambo.rxjavajamboweather.mould;

/**
 * Created by Jambo on 2016/5/21.
 */
public class WeatherTest {
    private String date;
    private String max;
    private String min;
    private String descri;

    public WeatherTest(String date, String descri, String max, String min) {
        this.date = date;
        this.descri = descri;
        this.max = max;
        this.min = min;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDescri() {
        return descri;
    }

    public void setDescri(String descri) {
        this.descri = descri;
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
}
