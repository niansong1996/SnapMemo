package com.alan.sphare.model.Tool;

import java.io.Serializable;

/**
 * Created by Alan on 2016/1/19.
 */
public class Time implements Serializable{
    public int hour,minute;

    public Time(int hour, int minute) {
        this.hour = hour;
        this.minute = minute;
    }
}
