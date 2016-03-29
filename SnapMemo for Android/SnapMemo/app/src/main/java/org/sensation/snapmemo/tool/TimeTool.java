package org.sensation.snapmemo.tool;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * 设置日期、时间相关的工具类
 * Created by Alan on 2016/2/26.
 */
public class TimeTool {

    final static public int DAY_SECONDS = 24 * 60 * 60;

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

    /**
     * 获得相隔天数
     *
     * @param d1
     * @param d2
     * @return
     */
    public static int getDaysBetween(Calendar d1, Calendar d2) {
        if (d1.after(d2)) { // swap dates so that d1 is start and d2 is end
            Calendar swap = d1;
            d1 = d2;
            d2 = swap;
        }
        int days = d2.get(java.util.Calendar.DAY_OF_YEAR) - d1.get(java.util.Calendar.DAY_OF_YEAR);
        int y2 = d2.get(java.util.Calendar.YEAR);
        if (d1.get(java.util.Calendar.YEAR) != y2) {
            d1 = (java.util.Calendar) d1.clone();
            do {
                days += d1.getActualMaximum(java.util.Calendar.DAY_OF_YEAR);
                d1.add(java.util.Calendar.YEAR, 1);
            } while (d1.get(java.util.Calendar.YEAR) != y2);
        }
        return days;
    }

    public static int getSecondsBetween(String startTime, String endTime) {
        String[] startTimeArray = startTime.split(":"),
                endTimeArray = endTime.split(":");
        int startHour = deplify(startTimeArray[0]), startMin = deplify(startTimeArray[1]),
                endHour = deplify(endTimeArray[0]), endMin = deplify(endTimeArray[1]);
        if (startHour <= endHour) {
            if (startMin <= endMin) {
                return (endHour - startHour) * 3600 + (endMin - startMin) * 60;
            } else {
                return (endHour - startHour) * 3600 + (60 - endMin + startMin) * 60;
            }
        } else {
            return 0;
        }
    }

    public static int deplify(String time) {
        if (time.charAt(0) == '0') {
            time = time.substring(1);
            return Integer.parseInt(time);
        } else {
            return Integer.parseInt(time);
        }
    }

}
