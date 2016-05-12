package com.example.jambo.rxjavajamboweather.mould;

/**
 * Created by Jambo on 2016/5/12/012.
 */
public class Province {
    private int id;
    private String provinceName;
    private int ProSort;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getProSort() {
        return ProSort;
    }

    public void setProSort(int proSort) {
        ProSort = proSort;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }
}
