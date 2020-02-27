package com.finalwy.basecomponent.utils;

import android.annotation.SuppressLint;
import android.text.format.Time;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 日期转换
 *
 * @author wy
 * @Date 2020-02-18
 */
public class DateUtils {

    /**
     * 获取当前时间
     *
     * @return 年月日时分秒字符串
     */
    public static String getCurrentTime() {
        Time time = new Time("GMT+8");
        time.setToNow();
        String year = "" + time.year;
        int imonth = time.month + 1;
        String month = imonth > 9 ? "" + imonth : "0" + imonth;
        String day = time.monthDay > 9 ? "" + time.monthDay : "0"
                + time.monthDay;
        String hour = (time.hour + 8) > 9 ? "" + (time.hour + 8) : "0"
                + (time.hour + 8);
        String minute = time.minute > 9 ? "" + time.minute : "0" + time.minute;
        String sec = time.second > 9 ? "" + time.second : "0" + time.second;
        String currentTime = year + month + day + hour + minute + sec;
        return currentTime;
    }

    //获取 日期/时/分/秒
    @SuppressLint("SimpleDateFormat")
    public static String getDateTime(long time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = sdf.format(new Date(time));
        return date;
    }

    //获取 日期/时/分
    @SuppressLint("SimpleDateFormat")
    public static String getHourMinute(long time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日HH时mm分");
        String date = sdf.format(new Date(time));
        return date;
    }

    //获取 日期年月日
    @SuppressLint("SimpleDateFormat")
    public static String getYearMonthDay(long time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
        String date = sdf.format(new Date(time));
        return date;
    }

    //获取日期年月
    public static String getYearMonth(long time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月");
        String date = sdf.format(new Date(time));
        return date;
    }

    //获取日期年月
    public static String getYearMonth2(long time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
        String date = sdf.format(new Date(time));
        return date;
    }

    //获取日期年
    public static String getYear(long time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年");
        String date = sdf.format(new Date(time));
        return date;
    }

    //获取  时/分
    public static String getTime(long time) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        String date = sdf.format(new Date(time));
        return date;
    }
}
