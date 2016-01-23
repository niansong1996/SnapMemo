package com.alan.sphare.model.Tool;

import java.io.Serializable;

/**
 * Created by Alan on 2016/1/19.
 */
public class Time implements Serializable {
    public int hour, minute;

    public Time(int hour, int minute) {
        this.hour = hour;
        this.minute = minute;
    }

    /**
     * 补充1位数字，在前面添加0，并以String类型返回
     *
     * @param num
     * @return
     */
    static public String amplify(int num) {
        if (num >= 0 && num < 10) {
            return "0" + String.valueOf(num);
        } else {
            return String.valueOf(num);
        }
    }
}
