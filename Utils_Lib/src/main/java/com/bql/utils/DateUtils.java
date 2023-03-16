package com.bql.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * 日期处理工具类
 * Created by Cyarie on 2016/1/29.
 */
public class DateUtils {

    public static final String DATE_HOUR_FORMAT = "yyyy-MM-dd HH";
    public static final String TIME_FORMAT = "H:mm:ss";
    public static final String DATE_FORMAT_CH = "yyyy年M月d日";
    public static final String DATE_FORMAT_STD = "yyyy-MM-dd";
    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";
    public static final String AM_PM_TIME_FORMAT = "HH:mm";
    public static final String DATE_FORMAT_HM = "yyyy-MM-dd HH:mm";

    public static final String DATE_TIME_FORMAT_M = "yyyy-MM-dd HH:mm";
    public static final String DATE_TIME_FORMAT_S = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_TIME_FORMAT_D = "yyyy-MM-dd";

    public static final String TIME_FORMAT_S = "HH:mm:ss";

    /**
     * 格式化时间
     *
     * @param date         日期
     * @param formatString 格式化形式
     * @return
     */
    public static String format(Date date, String formatString) {
        String formatDate = "";
        try {
            if (date != null) {
                SimpleDateFormat format = new SimpleDateFormat(formatString);
                formatDate = format.format(date);
            }
        } catch (Exception e) {

        }
        return formatDate;
    }

    /**
     * 格式化时间
     *
     * @param time         时间毫秒值
     * @param formatString 格式化形式
     * @return
     */
    public static String format(long time, String formatString) {
        String formatDate = "";
        try {
            SimpleDateFormat format = new SimpleDateFormat(formatString);
            formatDate = format.format(new Date(time));
        } catch (Exception e) {

        }

        return formatDate;
    }

    /**
     * 解析日期
     *
     * @param dateStr      日期字符串
     * @param formatString 格式化形式
     * @return
     */
    public static Date parseDate(String dateStr, String formatString) {
        Date date = null;


        try {
            if (dateStr != null && !dateStr.equals("")) {
                SimpleDateFormat format = new SimpleDateFormat(formatString);
                date = format.parse(dateStr);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return date;
    }

    /**
     * 获得今天的日期
     *
     * @param format 格式化标准
     * @return
     */
    public static String getToday(String format) {
        Calendar now = Calendar.getInstance();
        if (format != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat(format);
            return dateFormat.format(now.getTime());
        } else {
            return now.getTime().getTime() + "";
        }
    }

    /**
     * 获得当前时间（格式：去掉时分秒后的毫秒数）
     *
     * @return
     */
    public static long getCurentYMDTimeMillis() {
        Calendar cd = Calendar.getInstance();
        int year = cd.get(Calendar.YEAR);
        int month = cd.get(Calendar.MONTH);
        int day = cd.get(Calendar.DAY_OF_MONTH);
        cd.clear();
        cd.set(Calendar.YEAR, year);
        cd.set(Calendar.MONTH, month);
        cd.set(Calendar.DAY_OF_MONTH, day);
        long nowTime = cd.getTimeInMillis();
        return nowTime;
    }


    /**
     * 将毫秒值 转化为东八区 的12小时制  H:mm:ss
     *
     * @param milliseconds 毫秒值
     * @return
     */
    public static String formatTime(int milliseconds) {
        if (milliseconds < 0) {
            return "";
        }
        SimpleDateFormat format = new SimpleDateFormat(TIME_FORMAT);
        format.setTimeZone(TimeZone.getTimeZone("GMT +08:00, GMT +0800"));
        return format.format(new Date(milliseconds));
    }

    /**
     * 将从0开始的毫秒数转换为hh:mm:ss格式 如果没有hh那么转换为 mm:ss
     *
     * @param milliseconds
     * @return
     */
    public static String formatMilliseconds(int milliseconds) {
        int hh = milliseconds / 1000 / 60 / 60;
        int mm = (milliseconds - (hh * 60 * 60 * 1000)) / 1000 / 60;
        int ss = (milliseconds - (mm * 1000 * 60) - (hh * 60 * 60 * 1000)) / 1000;
        if (hh != 0) {
            return String.format("%02d:%02d:%02d", hh, mm, ss);
        }
        return String.format("%02d:%02d", mm, ss);
    }

    /**
     * 将从0开始的毫秒数转换为指定格式
     *
     * @param milliseconds
     * @return
     */
    public static String formatMilliseconds(String format, long milliseconds) {
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        formatter.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
        String result = formatter.format(milliseconds);
        return result;
    }

    /**
     * 获取系统三个月之前的时间
     *
     * @return
     */
    public static String getThreeMonthBeforeSysTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -3);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        return year + "-" + String.format("%02d", month) + "-"
                + String.format("%02d", day);
    }

    /**
     * 功能描述：返回年份
     *
     * @param date Date 日期
     * @return 返回年份
     */
    public static int getYear(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.YEAR);
    }

    /**
     * 功能描述：返回月份
     *
     * @param date Date 日期
     * @return 返回月份
     */
    public static int getMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.MONTH) + 1;
    }

    /**
     * 功能描述：返回日份
     *
     * @param date Date 日期
     * @return 返回日份
     */
    public static int getDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * 功能描述：返回小时
     *
     * @param date 日期
     * @return 返回小时
     */
    public static int getHour(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.HOUR_OF_DAY);
    }

    /**
     * 功能描述：返回分钟
     *
     * @param date 日期
     * @return 返回分钟
     */
    public static int getMinute(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.MINUTE);
    }

    /**
     * 返回秒钟
     *
     * @param date Date 日期
     * @return 返回秒钟
     */
    public static int getSecond(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.SECOND);
    }

    /**
     * 功能描述：返回毫秒
     *
     * @param date 日期
     * @return 返回毫秒
     */
    public static long getMillis(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.getTimeInMillis();
    }

    /**
     * 功能描述：日期相加
     *
     * @param date Date 日期
     * @param day  int 天数
     * @return 返回相加后的日期
     */
    public static Date addDate(Date date, int day) {
        Calendar calendar = Calendar.getInstance();
        long millis = getMillis(date) + ((long) day) * 24 * 3600 * 1000;
        calendar.setTimeInMillis(millis);
        return calendar.getTime();
    }

    /**
     * 功能描述：日期相减
     *
     * @param date  Date 日期
     * @param date1 Date 日期
     * @return 返回相减后的日期
     */
    public static int subDate(Date date, Date date1) {
        return (int) ((getMillis(date) - getMillis(date1)) / (24 * 3600 * 1000));
    }

    /**
     * 获取某天是星期几
     *
     * @param date
     * @return
     */
    public static String getMonthDayWeek(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int year = c.get(Calendar.YEAR);    //获取年
        int month = c.get(Calendar.MONTH) + 1;   //获取月份，0表示1月份
        int day = c.get(Calendar.DAY_OF_MONTH);    //获取当前天数
        int week = c.get(Calendar.DAY_OF_WEEK);

        String weekStr = null;

        switch (week) {

            case Calendar.SUNDAY:
                weekStr = "周日";
                break;

            case Calendar.MONDAY:
                weekStr = "周一";
                break;

            case Calendar.TUESDAY:
                weekStr = "周二";
                break;

            case Calendar.WEDNESDAY:
                weekStr = "周三";
                break;

            case Calendar.THURSDAY:
                weekStr = "周四";
                break;

            case Calendar.FRIDAY:
                weekStr = "周五";
                break;

            case Calendar.SATURDAY:
                weekStr = "周六";
                break;
        }

        return year + "年" + month + "月" + day + "日" + "(" + weekStr + ")";
    }


    /**
     * 获得口头时间字符串，如今天，昨天等
     *
     * @param d 时间格式为yyyy-MM-dd HH:mm:ss
     * @return 口头时间字符串
     */
    public static String getTimeInterval(String d) {
        Date date = parseDate(d, "yyyy-MM-dd HH:mm:ss");
        Calendar now = Calendar.getInstance();
        now.setTime(new Date());
        int nowYear = now.get(Calendar.YEAR);
        int nowMonth = now.get(Calendar.MONTH);
        int nowWeek = now.get(Calendar.WEEK_OF_MONTH);
        int nowDay = now.get(Calendar.DAY_OF_WEEK);
        int nowHour = now.get(Calendar.HOUR_OF_DAY);
        int nowMinute = now.get(Calendar.MINUTE);

        Calendar ca = Calendar.getInstance();
        if (date != null)
            ca.setTime(date);
        else
            ca.setTime(new Date());
        int year = ca.get(Calendar.YEAR);
        int month = ca.get(Calendar.MONTH);
        int week = ca.get(Calendar.WEEK_OF_MONTH);
        int day = ca.get(Calendar.DAY_OF_WEEK);
        int hour = ca.get(Calendar.HOUR_OF_DAY);
        int minute = ca.get(Calendar.MINUTE);
        if (year != nowYear) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            //不同年份
            return sdf.format(date);
        } else {
            if (month != nowMonth) {
                //不同月份
                SimpleDateFormat sdf = new SimpleDateFormat("M月dd日");
                return sdf.format(date);
            } else {
                if (week != nowWeek) {
                    //不同周
                    SimpleDateFormat sdf = new SimpleDateFormat("M月dd日");
                    return sdf.format(date);
                } else if (day != nowDay) {
                    if (day + 1 == nowDay) {
                        return "昨天" + format(date, "HH:mm");
                    }
                    if (day + 2 == nowDay) {
                        return "前天" + format(date, "HH:mm");
                    }
                    //不同天
                    SimpleDateFormat sdf = new SimpleDateFormat("M月dd日");
                    return sdf.format(date);
                } else {
                    //同一天
                    //                    int hourGap = nowHour - hour;
                    //                    if (hourGap == 0)//1小时内
                    //                    {
                    //                        if (nowMinute - minute < 1) {
                    //                            return "刚刚";
                    //                        } else {
                    //                            return (nowMinute - minute) + "分钟前";
                    //                        }
                    //                    } else if (hourGap >= 1 && hourGap <= 12) {
                    //                        return hourGap + "小时前";
                    //                    } else {
                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                    return sdf.format(date);
                    //                    }
                }
            }
        }
    }


}
