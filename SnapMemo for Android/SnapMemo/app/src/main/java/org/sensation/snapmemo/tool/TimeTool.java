package org.sensation.snapmemo.tool;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * 设置日期、时间相关的工具类
 * Created by Alan on 2016/2/26.
 */
public class TimeTool {
    /**
     * 获得根据yyyy-MM-dd格式传入的日期所对应的星期几信息
     *
     * @param date
     * @return
     */
    public static String getDay(String date) {

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        int dayOfWeekNum = 0;
        String dayOfWeek = "未获得";

        try {
            c.setTime(format.parse(date));

            dayOfWeekNum = c.get(Calendar.DAY_OF_WEEK);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        switch (dayOfWeekNum) {
            case 1:
                dayOfWeek = "周日";
                break;
            case 2:
                dayOfWeek = "周一";
                break;
            case 3:
                dayOfWeek = "周二";
                break;
            case 4:
                dayOfWeek = "周三";
                break;
            case 5:
                dayOfWeek = "周四";
                break;
            case 6:
                dayOfWeek = "周五";
                break;
            case 7:
                dayOfWeek = "周六";
                break;
        }

        return dayOfWeek;
    }

    /**
     * 添加单字符的int整数为双字符的string
     *
     * @param time 传入的int型time
     * @return 转换为双字符的time
     */
    public static String amplify(int time) {
        if (time < 10) {
            return "0" + time;
        } else {
            return time + "";
        }
    }
}
