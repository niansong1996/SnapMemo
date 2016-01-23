package com.alan.sphare.model.Tool;

import java.io.Serializable;

/**
 * Created by Alan on 2016/1/21.
 */
public class Date implements Serializable{
    int year, month, day;

    public Date(int year, int month, int day) {
        this.year = year;
        this.month = month;
        this.day = day;
    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public int getDay() {
        return day;
    }
}
