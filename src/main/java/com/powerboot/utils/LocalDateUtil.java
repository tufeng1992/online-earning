package com.powerboot.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;

/**
 * Create  2020 - 10 - 30 4:57 下午
 */
public class LocalDateUtil {
    public static final String DATE_HMS_FORMAT_START = "00:00:00";
    public static final String DATE_HMS_FORMAT_END = "23:59:59";
    public static final String SIMPLE_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String SIMPLE_DATE_FORMAT_HM = "yyyy-MM-dd HH:mm";
    public static final String SIMPLE_DATE_FORMAT_HH = "yyyy-MM-dd HH:00:00";
    public static final String SIMPLE_DATE_FORMAT_YYYYMMDDHH = "yyyy-MM-dd HH:00";
    public static final String SIMPLE_DATE_FORMAT_YMDHMSS = "yyyyMMddHHmmssSSS";
    public static final String SIMPLE_DATE_FORMAT_YMDHMS = "yyyyMMddHHmmss";
    public static final String SIMPLE_DATE_FORMAT_YMD = "yyyy-MM-dd";
    public static final String SIMPLE_DATE_FORMAT_YM = "yyyy-MM";
    public static final String SIMPLE_DATE_FORMAT_YMD2 = "yyyyMMdd";
    public static final String SIMPLE_DATE_FORMAT_YMDH = "yyyyMMddHH";
    public static final String SIMPLE_DATE_FORMAT_SPECAIL = "MM月dd日HH:mm";
    public static final String SIMPLE_DATE_FORMAT_YMD_CHINESE = "yyyy年MM月dd日";
    public static final String SIMPLE_DATE_FORMAT_YMD3 = "yyyy/MM/dd";
    private static Logger log = LoggerFactory.getLogger(LocalDateUtil.class);

    /**
     * 获取当前时间
     *
     * @return LocalDateTime
     */
    public static LocalDateTime now() {
        return LocalDateTime.now();
    }

    /**
     * 获取当前时间
     *
     * @return String
     */
    public static String nowYMDHMS() {
        return now(SIMPLE_DATE_FORMAT);
    }

    /**
     * 获取当前时间
     *
     * @param format 格式
     * @return String
     */
    public static String now(String format) {
        return formatLocalDateTimeToString(now(), format);
    }

    /**
     * Date 转 LocalDateTime
     *
     * @param date Date 对象
     * @return LocalDateTime
     */
    public static LocalDateTime dateToLocalDateTime(Date date) {
        long nanoOfSecond = (date.getTime() % 1000) * 1000000;
        return LocalDateTime.ofEpochSecond(date.getTime() / 1000, (int) nanoOfSecond, ZoneOffset.of("+8"));
    }

    /**
     * 把日期后的时间归0 变成(yyyy-MM-dd 00:00:00:000)
     *
     * @param date Date 对象
     * @return LocalDateTime
     */
    public static LocalDateTime zerolizedTime(Date date) {
        LocalDateTime localDateTime = dateToLocalDateTime(date);
        return LocalDateTime.of(localDateTime.getYear(), localDateTime.getMonth(), localDateTime.getDayOfMonth(), 0, 0, 0, 0);

    }

    /**
     * Timestamp 转 LocalDateTime
     *
     * @param date Date 对象
     * @return LocalDateTime
     */
    public static LocalDateTime timestampToLocalDateTime(Timestamp date) {
        return LocalDateTime.ofEpochSecond(date.getTime() / 1000, date.getNanos(), ZoneOffset.of("+8"));
    }

    /**
     * 把时间变成(yyyy-MM-dd 00:00:00:000)
     *
     * @param date Date 对象
     * @return LocalDateTime
     */
    public static LocalDateTime zerolizedTime(Timestamp date) {
        LocalDateTime localDateTime = timestampToLocalDateTime(date);
        return LocalDateTime.of(localDateTime.getYear(), localDateTime.getMonth(), localDateTime.getDayOfMonth(), 0, 0, 0, 0);
    }

    /**
     * 把日期的时间变成(yyyy-MM-dd 23:59:59:999)
     *
     * @param localDateTime 对象
     * @return LocalDateTime
     */
    public static LocalDateTime getEndTime(LocalDateTime localDateTime) {
        return LocalDateTime.of(localDateTime.getYear(), localDateTime.getMonth(), localDateTime.getDayOfMonth(), 23, 59, 59, 999 * 1000000);
    }

    /**
     * 把日期的时间变成(yyyy-MM-dd 23:59:59:999)
     *
     * @param date Date 对象
     * @return LocalDateTime
     */
    public static LocalDateTime getEndTime(Date date) {
        LocalDateTime localDateTime = dateToLocalDateTime(date);
        return LocalDateTime.of(localDateTime.getYear(), localDateTime.getMonth(), localDateTime.getDayOfMonth(), 23, 59, 59, 999 * 1000000);
    }

    /**
     * 把时间变成(yyyy-MM-dd 23:59:59:999)
     *
     * @param date Date 对象
     * @return LocalDateTime
     */
    public static LocalDateTime getEndTime(Timestamp date) {
        LocalDateTime localDateTime = timestampToLocalDateTime(date);
        return LocalDateTime.of(localDateTime.getYear(), localDateTime.getMonth(), localDateTime.getDayOfMonth(), 23, 59, 59, 999 * 1000000);
    }

    /**
     * 计算特定时间到 当天 23.59.59.999 的秒数
     *
     * @return 秒数
     */
    public static int calculateToEndTime(Date date) {
        LocalDateTime localDateTime = dateToLocalDateTime(date);
        LocalDateTime end = getEndTime(date);
        return (int) (end.toEpochSecond(ZoneOffset.UTC) - localDateTime.toEpochSecond(ZoneOffset.UTC));
    }

    /**
     * 增加或减少年/月/周/天/小时/分/秒数
     *
     * @param localDateTime 例：ChronoUnit.DAYS
     * @param chronoUnit    单位
     * @param num           数量
     * @return LocalDateTime
     */
    public static LocalDateTime addTime(LocalDateTime localDateTime, ChronoUnit chronoUnit, int num) {
        return localDateTime.plus(num, chronoUnit);
    }

    /**
     * 增加或减少年/月/周/天/小时/分/秒数
     *
     * @param localDate 例：ChronoUnit.DAYS
     * @param chronoUnit    单位
     * @param num           数量
     * @return LocalDateTime
     */
    public static LocalDate addTime(LocalDate localDate, ChronoUnit chronoUnit, int num) {
        return localDate.plus(num, chronoUnit);
    }

    /**
     * 增加或减少年/月/周/天/小时/分/秒数
     *
     * @param chronoUnit 例：ChronoUnit.DAYS
     * @param num        数量
     * @return LocalDateTime
     */
    public static LocalDateTime addTime(Date date, ChronoUnit chronoUnit, int num) {
        long nanoOfSecond = (date.getTime() % 1000) * 1000000;
        LocalDateTime localDateTime = LocalDateTime.ofEpochSecond(date.getTime() / 1000, (int) nanoOfSecond, ZoneOffset.of("+8"));
        return localDateTime.plus(num, chronoUnit);
    }

    /**
     * 增加或减少年/月/周/天/小时/分/秒数
     *
     * @param chronoUnit 例：ChronoUnit.DAYS
     * @param num        数量
     * @return LocalDateTime
     */
    public static LocalDateTime addTime(Timestamp date, ChronoUnit chronoUnit, int num) {
        long nanoOfSecond = (date.getTime() % 1000) * 1000000;
        LocalDateTime localDateTime = LocalDateTime.ofEpochSecond(date.getTime() / 1000, (int) nanoOfSecond, ZoneOffset.of("+8"));
        return localDateTime.plus(num, chronoUnit);
    }


    /**
     * 比较两个LocalDateTime是否同一天
     *
     * @param begin 开始时间
     * @param end   结束时间
     * @return 是否一致
     */
    public static boolean isTheSameDay(LocalDateTime begin, LocalDateTime end) {
        return begin.toLocalDate().equals(end.toLocalDate());
    }

    /**
     * 比较两个时间LocalDateTime大小
     *
     * @param time1 时间1
     * @param time2 时间2
     * @return 1:第一个比第二个大；0：第一个与第二个相同；-1：第一个比第二个小
     */
    public static int compareTwoTime(LocalDateTime time1, LocalDateTime time2) {
        if (time1.isAfter(time2)) {
            return 1;
        } else if (time1.isBefore(time2)) {
            return -1;
        } else {
            return 0;
        }
    }

    /**
     * localDateTime 转 自定义格式string
     *
     * @param localDateTime 时间对象
     * @param format        例：yyyy-MM-dd hh:mm:ss
     * @return 格式化后的结果
     */
    public static String formatLocalDateTimeToString(LocalDateTime localDateTime, String format) {
        try {
            if (localDateTime == null) {
                return "";
            }
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
            return localDateTime.format(formatter);

        } catch (DateTimeParseException e) {
            log.error("date time parse error is :", e);
        }
        return "";
    }

    /**
     * string 转 LocalDateTime
     *
     * @param dateStr 例："2017-08-11 01:00:00"
     * @param format  例："yyyy-MM-dd HH:mm:ss"
     * @return 时间对象
     */
    public static LocalDateTime stringToLocalDateTime(String dateStr, String format) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
            return LocalDateTime.parse(dateStr, formatter);
        } catch (DateTimeParseException e) {
            log.error("date time parse error is :", e);
        }
        return null;
    }

    /**
     * string 转 LocalDate
     *
     * @param dateStr 例："2017-08-11
     * @param format  例："yyyy-MM-dd
     * @return 时间对象
     */
    public static LocalDate stringToLocalDate(String dateStr, String format) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
            return LocalDate.parse(dateStr, formatter);
        } catch (DateTimeParseException e) {
            log.error("date time parse error is :", e);
        }
        return null;
    }

    /**
     * 年月日数据转 LocalDateTime
     * yyyy-MM-dd to yyyy-MM-dd hh:mm:ss
     * string 转 LocalDateTime
     *
     * @param dateStr 例："2017-08-11 01:00:00"
     * @param format  例："yyyy-MM-dd HH:mm:ss"
     * @return 时间对象
     */
    public static LocalDateTime dateStringToLocalDateTime(String dateStr, String format) {
        try {
            dateStr += " 00:00:00";
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
            return LocalDateTime.parse(dateStr, formatter);
        } catch (DateTimeParseException e) {
            log.error("date time parse error is :", e);
        }
        return null;
    }

    /**
     * 计算两个时间LocalDateTime相差的天数，不考虑日期前后
     *
     * @param before time1
     * @param after  time2
     * @return
     */
    @Deprecated
    public static int getAbsTimeDiffDay(LocalDateTime before, LocalDateTime after) {
        return Math.abs(Period.between(before.toLocalDate(), after.toLocalDate()).getDays());
    }

    /**
     * 时间相减取天数
     * @param before 开始时间
     * @param after 结束时间
     * @return 相差天数
     */
    public static long getTimeSubtractDays( LocalDateTime before,  LocalDateTime after) {
        return getTimeSubtractDays(before.toLocalDate(), after.toLocalDate());
    }


    /**
     * 日期相减取天数
     * @param before 开始日期
     * @param after 结束日期
     * @return 相差天数
     */
    public static long getTimeSubtractDays( LocalDate before,  LocalDate after) {
        return after.toEpochDay() - before.toEpochDay();
    }

    /**
     * 计算两个时间LocalDateTime相差的月数，不考虑日期前后，返回结果>=0
     *
     * @param before time1
     * @param after  time2
     * @return 相差月数
     */
    public static int getAbsTimeDiffMonth(LocalDateTime before, LocalDateTime after) {
        return Math.abs(Period.between(before.toLocalDate(), after.toLocalDate()).getMonths());
    }

    /**
     * 计算两个时间LocalDateTime相差的年数，不考虑日期前后，返回结果>=0
     *
     * @param before time1
     * @param after  time2
     * @return 相差年数
     */
    public static int getAbsTimeDiffYear(LocalDateTime before, LocalDateTime after) {
        return Math.abs(Period.between(before.toLocalDate(), after.toLocalDate()).getYears());
    }

    /**
     * 获取月头
     *
     * @return
     */
    public static LocalDateTime getMonthFirstDay() {
        return getMonthFirstDay(now());
    }

    /**
     * 获取指定月头
     *
     * @param time
     * @return
     */
    public static LocalDateTime getMonthFirstDay(LocalDateTime time) {
        return time.with(TemporalAdjusters.firstDayOfMonth());
    }


    /**
     * 获取月末
     *
     * @return
     */
    public static LocalDateTime getMonthLastDay() {
        return getMonthLastDay(now());
    }

    /**
     * 获取指定月末
     *
     * @param time
     * @return
     */
    public static LocalDateTime getMonthLastDay(LocalDateTime time) {
        return time.with(TemporalAdjusters.lastDayOfMonth());
    }


    /**
     * 获取固定格式的时间字符串
     *
     * @param time
     * @param format
     * @return
     */
    public static String getTimeStr(LocalDateTime time, String format) {
        DateTimeFormatter formatters = DateTimeFormatter.ofPattern(format);
        if (null == time) {
            return "";
        }
        return time.format(formatters);
    }

    /**
     * 获取固定格式的时间字符串
     *
     * @param time
     * @param format
     * @return
     */
    public static String getTimeStr(LocalDate time, String format) {
        DateTimeFormatter formatters = DateTimeFormatter.ofPattern(format);
        if (null == time) {
            return "";
        }
        return time.format(formatters);
    }


}
