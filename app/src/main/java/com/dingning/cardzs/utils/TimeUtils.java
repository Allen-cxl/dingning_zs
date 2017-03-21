package com.dingning.cardzs.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Formatter;
import java.util.Locale;

/**
 * Created by Allen on 2016/12/6.
 */

public class TimeUtils {

    /**
     *
     * @return 返回年月日
     */
    public static String getDate(){
        SimpleDateFormat formatter  =  new  SimpleDateFormat  ("yyyy年MM月dd日");
        Date curDate = new Date(System.currentTimeMillis());

        String  str  =  formatter.format(curDate);
        return str;
    }

    /**
     *
     * @return 返回时分
     */
    public static String getTime(){
        SimpleDateFormat formatter  =  new  SimpleDateFormat  ("HH:mm");
        Date curDate = new Date(System.currentTimeMillis());

        String  str  =  formatter.format(curDate);
        return str;
    }

    /**
     *
     * @return 返回星期数
     */
    public static String getDayInWeek(){

        SimpleDateFormat formatter  =  new SimpleDateFormat("EEEE");
        Date curDate = new Date(System.currentTimeMillis());

        String  str  =  formatter.format(curDate);
        return str;
    }

    /**
     *
     * @return 返回年数
     */
    public static String getYear(){

        SimpleDateFormat formatter  =  new SimpleDateFormat("yyyy");
        Date curDate = new Date(System.currentTimeMillis());

        String  str  =  formatter.format(curDate);
        return str;
    }

    /**
     *
     * @return 返回月数
     */
    public static String getMonth(){

        SimpleDateFormat formatter  =  new SimpleDateFormat("MM");
        Date curDate = new Date(System.currentTimeMillis());

        String  str  =  formatter.format(curDate);
        return str;
    }

    /**
     *
     * @return 返回日数
     */
    public static String getDayInMonth(){

        SimpleDateFormat formatter  =  new SimpleDateFormat("dd");
        Date curDate = new Date(System.currentTimeMillis());

        String  str  =  formatter.format(curDate);
        return str;
    }

    public static String stringForTime(int timeMs) {
        if (timeMs <= 0 || timeMs >= 24 * 60 * 60 * 1000) {
            return "00:00";
        }
        int totalSeconds = timeMs / 1000;
        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;
        StringBuilder stringBuilder = new StringBuilder();
        Formatter mFormatter = new Formatter(stringBuilder, Locale.getDefault());
        if (hours > 0) {
            return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            return mFormatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }
}
