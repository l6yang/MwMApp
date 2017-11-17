package com.mwm.loyal.utils;

import android.text.TextUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.mwm.loyal.impl.Contact;

public class TimeUtil implements Contact {
    public static String getWeek() {
        SimpleDateFormat format = new SimpleDateFormat(Str.TIME_WEEK, Locale.CHINA);
        return replaceTime(format.format(new Date()));
    }

    private static SimpleDateFormat setFormat(String format) {
        return new SimpleDateFormat(format, Locale.CHINA);
    }

    public static String getDate() {
        return replaceTime(setFormat(Str.TIME_YEAR_MONTH_DAY)
                .format(new Date()));
    }

    public static String getDateTime() {
        return replaceTime(setFormat(Str.TIME_ALL).format(new Date()));
    }

    public static String getDateTime(String format) {
        return replaceTime(setFormat(format).format(new Date()));
    }

    public static String getDateTime(Date date, String format) {
        return replaceTime(setFormat(format).format(date));
    }

    public static String getDate(Date date, String format) {
        return replaceTime(setFormat(format).format(date));
    }

    public static String getDate(String time) {
        SimpleDateFormat format = new SimpleDateFormat(time, Locale.CHINA);
        return replaceTime(format.format(new Date()));
    }

    public static String getTime() {
        return replaceTime(setFormat(Str.HOURS_MIN).format(new Date()));
    }

    public static boolean afterDate(String startTime, String endTime, String format) {
        try {
            SimpleDateFormat sdf = setFormat(format);
            if (TextUtils.isEmpty(startTime) || TextUtils.isEmpty(endTime))
                return true;
            if (TextUtils.equals(startTime, endTime))
                return true;
            try {
                Date start = sdf.parse(startTime);
                Date end = sdf.parse(endTime);
                return end.after(start);
            } catch (ParseException e) {
                e.printStackTrace();
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

    public static int dateSpan(String startTime, String endTime) {
        try {
            SimpleDateFormat sdf = setFormat(Str.TIME_YEAR_MONTH_DAY);
            if (TextUtils.isEmpty(startTime) || TextUtils.isEmpty(endTime))
                return -1;
            if (TextUtils.equals(startTime, endTime))
                return 1;
            try {
                Date start = sdf.parse(replaceTime(startTime));
                Date end = sdf.parse(replaceTime(endTime));
                long span = (end.getTime() - start.getTime()) / 1000;
                int day = (int) span / (24 * 3600);
                return day >= 0 ? day + 1 : -1;
            } catch (ParseException e) {
                e.printStackTrace();
                return -1;
            }
        } catch (Exception e) {
            return -1;
        }
    }

    public static float dateTime(String startTime, String endTime) {
        try {
            SimpleDateFormat sdf = setFormat(Str.TIME_ALL);
            if (TextUtils.isEmpty(startTime) || TextUtils.isEmpty(endTime))
                return -1;
            if (TextUtils.equals(startTime, endTime))
                return 0;
            try {
                Date start = sdf.parse(replaceTime(startTime));
                Date end = sdf.parse(replaceTime(endTime));
                long span = (end.getTime() - start.getTime()) / 1000;
                float hour = (float) span / (3600);
                hour = (float) Math.round(hour * 100) / 100;
                return hour >= 0 ? hour : -1;
            } catch (ParseException e) {
                e.printStackTrace();
                return -1;
            }
        } catch (Exception e) {
            return -1;
        }
    }

    private static String replaceTime(String str) {
        return str.toLowerCase().replace("上午", "").replace("下午", "").replace("am", "").replace("pm", "");
    }

    public static String subEndTime(String time) {
        String subTime = StringUtil.replaceNull(time).trim();
        return TextUtils.isEmpty(subTime) ? "" : subTime.
                replace("00:00:00", "").replace(".0", "").trim();
    }
}
