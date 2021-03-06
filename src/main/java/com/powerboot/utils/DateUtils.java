package com.powerboot.utils;

import com.powerboot.base.BaseResponse;
import com.powerboot.config.BaseException;
import com.powerboot.consts.DictConsts;
import org.apache.commons.lang.StringUtils;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 */
public class DateUtils extends org.apache.commons.lang.time.DateUtils{
    /**
     * the milli second of a day
     */
    public static final long DAYMILLI = 24 * 60 * 60 * 1000;
    /**
     * the milli seconds of an hour
     */
    public static final long HOURMILLI = 60 * 60 * 1000;
    /**
     * the milli seconds of a minute
     */
    public static final long MINUTEMILLI = 60 * 1000;
    /**
     * the milli seconds of a second
     */
    public static final long SECONDMILLI = 1000;

    /**
     * flag before
     */
    public static final transient int BEFORE = 1;
    /**
     * flag after
     */
    public static final transient int AFTER = 2;
    /**
     * flag equal
     */
    public static final transient int EQUAL = 3;

    public static final String DATEHMSFORMAT = "23:59:59";
    public final static String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    public static final String SIMPLE_DATEFORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String SIMPLE_DATEFORMAT2 = "yyyy/MM/dd HH:mm:ss";

    public static final String SIMPLE_DATEFORMAT_HM = "yyyy-MM-dd HH:mm";

    public static final String SIMPLE_DATEFORMATHH = "yyyy-MM-dd HH:00:00";
    public static final String SIMPLE_DATEFORMAYYYYMMDDHH = "yyyy-MM-dd HH:00";

    public static final String SIMPLE_DATEFORMAT_YMDHMSS = "yyyyMMddHHmmssSSS";
    public static final String SIMPLE_DATEFORMAT_YMDHMS = "yyyyMMddHHmmss";

    public static final String SIMPLE_DATEFORMAT_MD = "MM.dd";

    public static final String SIMPLE_DATEFORMAT_MD2 = "MMdd";

    public static final String SIMPLE_DATEFORMAT_HHMM = "HH:mm";
    public static final String SIMPLE_DATEFORMAT_HHMM2 = "HH:00";
    public static final String SIMPLE_DATEFORMAT_YMD = "yyyy-MM-dd";
    public static final String SIMPLE_DATEFORMAT_YM = "yyyy-MM";
    public static final String SIMPLE_DATEFORMAT_YMD2 = "yyyyMMdd";
    public static final String SIMPLE_DATEFORMAT_SPECAIL = "MM???dd???HH:mm";

    public static final String SIMPLE_DATEFORMAT_YMD_CHINESE = "yyyy???MM???dd???";

    public static final String SIMPLE_DATEFORMAT_YMD3 = "yyyy/MM/dd";
    public static final String SIMPLE_DATEFORMAT_YMD4 = "MM/dd/yyyy";
    public static final String SIMPLE_DATEFORMAT_Y = "yyyy";

    public static final Date DEFAULT_DATE = DateUtils.parseDateYMDHMS("2000-01-01 00:00:00");

    /**
     * ?????????????????????????????????
     * @return
     */
    public static Boolean checkWeekend(){
        Calendar cal=Calendar.getInstance();
        cal.setTime(new Date());
        String days = RedisUtils.getString(DictConsts.UN_WITHDRAW_DAYS);
        if (StringUtils.isNotBlank(days)) {
            for (String day : days.split(",")) {
                Integer dayOfWeek = Integer.valueOf(day);
                if (dayOfWeek == 7) {
                    dayOfWeek = 1;
                } else {
                    dayOfWeek++;
                }
                if(cal.get(Calendar.DAY_OF_WEEK) == dayOfWeek){
                    return true;
                }
            }
        }
        return false;
    }
    /**
     * ??????????????????
     * @return?????????????????????yyyyMMddHHmmss
     */
    public static String getStringDate() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String dateString = formatter.format(currentTime);
        System.out.println("TIME:::"+dateString);
        return dateString;
    }
    /**
     * ?????????????????????+3????????????
     * ???????????????
     * @return
     */
    public static String generateNumber(){
        String t = getStringDate();
        int x=(int)(Math.random()*900)+100;
        return t + x;
    }


    public static String formatLocalDate2StringYMD(LocalDate localDate){
        DateTimeFormatter formatters = DateTimeFormatter.ofPattern(SIMPLE_DATEFORMAT_YMD);
        return localDate.format(formatters);
    }

    public static LocalDate convertDate2LocalDate(Date date){
        Instant instant = date.toInstant();
        ZoneId zone = ZoneId.systemDefault();
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, zone);
        return localDateTime.toLocalDate();
    }

    public static String formatDateYMDChinese(Date date){
        if(date==null){
            return null;
        }
        return format(date,SIMPLE_DATEFORMAT_YMD_CHINESE);
    }

    public static String formatDateYMD(Date date){
        if(date==null){
            return null;
        }
        return format(date,SIMPLE_DATEFORMAT_YMD);
    }

    public static String formatDateY(Date date){
        if(date==null){
            return null;
        }
        return format(date,SIMPLE_DATEFORMAT_Y);
    }

    public static String formatDateYMD2(Date date){
        if(date==null){
            return null;
        }
        return format(date,SIMPLE_DATEFORMAT_YMD2);
    }

    public static Date formatDateYMDHMS(String date){
        if(date==null){
            return null;
        }
        try{
            return parseDate(date,SIMPLE_DATEFORMAT);
        }catch (Exception e){
            return parseDate(parseDateYMD(date),SIMPLE_DATEFORMAT);
        }
    }

    public static String formatDateYMDHMS(Date date){
        if(date==null){
            return null;
        }
        return format(date,SIMPLE_DATEFORMAT);
    }

    public static String getNowYMD(){
        return DateUtils.formatDateYMD(new Date());
    }

    public static String getNowYear(){
        Calendar calendar = Calendar.getInstance();
        return String.valueOf(calendar.get(Calendar.YEAR));
    }

    public static String getNowMonth(){
        Calendar calendar = Calendar.getInstance();
        return String.valueOf(calendar.get(Calendar.MONTH) + 1);
    }

    public static Integer getNowQarter(){
        int month = Integer.parseInt(getNowMonth());
        if(month>=1 && month<=3){
            return 1;
        }else if(month>=4 && month<=6){
            return 2;
        } else if(month>=7 && month<=9){
            return 3;
        } else if(month>=10 && month<=12){
            return 4;
        }
        return null;
    }

    public static String getNowDay(){
        Calendar calendar = Calendar.getInstance();
        return String.valueOf(Calendar.DAY_OF_MONTH);
    }

    public static String getYearFormat(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date.getTime());
        return String.valueOf(calendar.get(Calendar.YEAR));
    }

    public static String getMonthFormat(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date.getTime());
        return String.valueOf(calendar.get(Calendar.MONTH) + 1);
    }

    public static String getDayFormat(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date.getTime());
        return String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
    }


    /**
     * ??????????????????????????? ??????1
     *
     * @param date
     * @param pattern
     * @return
     */
    public static int getMonth(String date, String pattern) {
        Date d = parseDate(date, pattern);
        if (d == null) {
            return 0;
        }
        return getMonth(d);
    }

    public static int getMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date.getTime());
        return calendar.get(Calendar.MONTH) + 1;
    }

    public static Date getMonthFirstDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date.getTime());
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        return getDateStart(calendar.getTime());
    }

    public static Date getMonthLastDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date.getTime());
        calendar.add(Calendar.MONTH, 1);
        calendar.set(Calendar.DAY_OF_MONTH, 0);
        return new Date(getDateStart(calendar.getTime()).getTime() + 24 * 60 * 60 * 1000 - 1);
    }

    public static Date getMonthLastDay(Integer year,Integer month) {
        String dateStr = year + "-" + month + "-1";
        Date date = DateUtils.parseDateYMD(dateStr);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date.getTime());
        calendar.add(Calendar.MONTH, 1);
        calendar.set(Calendar.DAY_OF_MONTH, 0);
        return new Date(getDateStart(calendar.getTime()).getTime() + 24 * 60 * 60 * 1000 - 1);
    }

    public static Date getDateStart(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date.getTime());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    public static Date addWeekDays(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date.getTime());
        calendar.add(Calendar.WEEK_OF_YEAR, -1);
        return calendar.getTime();
    }

    public static Date addMonths(Date date, int num) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date.getTime());
        calendar.add(Calendar.MONTH, num);
        return calendar.getTime();
    }

    public static Date addMonthsMinus(Date date, int num) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date.getTime());
        calendar.add(Calendar.MONTH, num);

        calendar.set(Calendar.DATE, calendar.get(Calendar.DATE)-1);
        return calendar.getTime();

    }

    public static Date addMonthsMinusContract(Date date, int num) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date.getTime());
        calendar.add(Calendar.MONTH, num);

        calendar.set(Calendar.DATE, calendar.get(Calendar.DATE)-1);
        return calendar.getTime();

    }

    public static Date addYears(Date date, int num) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date.getTime());
        calendar.add(Calendar.YEAR, num);
        return calendar.getTime();
    }

    public static Date addYearsMinusContract(Date date, int num) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date.getTime());
        calendar.add(Calendar.YEAR, num);
        calendar.set(Calendar.DATE, calendar.get(Calendar.DATE)-1);
        return calendar.getTime();
    }

    public static Date getBeforeAfterDate(Date date, int day) {
        Calendar cal = new GregorianCalendar();
        cal.setTime(date);
        int Year = cal.get(Calendar.YEAR);
        int Month = cal.get(Calendar.MONTH);
        int Day = cal.get(Calendar.DAY_OF_MONTH);
        int NewDay = Day + day;
        cal.set(Calendar.YEAR, Year);
        cal.set(Calendar.MONTH, Month);
        cal.set(Calendar.DAY_OF_MONTH, NewDay);
        return new Date(cal.getTimeInMillis());
    }

    public static String addDate(Date date, int minute) {
        long curren = System.currentTimeMillis();
        curren += minute * 60 * 1000;
        Date da = new Date(curren);
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(da);
        //System.out.println(dateFormat.format(da));
    }

    public static Long getMorningTime() {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        Long today = c.getTimeInMillis() / 1000;
        return today;
    }

    /**
     * ????????????
     * @param dateList
     */
    public static void orderSort(List<Date> dateList){
        Collections.sort(dateList, new Comparator<Date>() {
            @Override
            public int compare(Date o1, Date o2) {
                return o1.after(o2) ? 1 : -1;
            }
        });
    }

    /**
     * ????????????
     * @param dateList
     */
    public static void descSort(List<Date> dateList){
        Collections.sort(dateList, new Comparator<Date>() {
            @Override
            public int compare(Date o1, Date o2) {
                return o1.before(o2) ? 1 : -1;
            }
        });
    }

    /**
     * ????????????????????????
     * @param date
     * @return
     */
    public static Date getFirstDayOfWeek(Date date) {
        Calendar cal = Calendar.getInstance();
        try {
            cal.setTime(date);
            cal.set(Calendar.DAY_OF_WEEK, 1);
            cal.set(Calendar.DATE, cal.get(Calendar.DATE) + 1);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return cal.getTime();
    }

    /**
     * ???????????????????????????
     * @param date
     * @return
     */
    public static Date getLastDayOfWeek(Date date) {
        Calendar cal = Calendar.getInstance();
        try {
            cal.setTime(date);
            cal.set(Calendar.DAY_OF_WEEK, 1);
            cal.set(Calendar.DATE, cal.get(Calendar.DATE) + 7);
        } catch(Exception e) {
            e.printStackTrace();
        }

        return cal.getTime();
    }
    /**
     * ?????????????????????
     * @param date
     * @return
     */
    public static Date getYearFiestDay(Date date){
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.DAY_OF_YEAR, 1);
        Date dt = null; // ?????????????????????
        try {
            dt = c.getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dt;
    }

    /**
     * ????????????????????????
     * @param date
     * @return
     */
    public static Date getYearEndDay(Date date){
        Calendar cd = Calendar.getInstance();
        cd.setTime(date);
        Calendar c = Calendar.getInstance();
        c.clear();
        c.set(Calendar.YEAR, cd.get(Calendar.YEAR));
        c.roll(Calendar.DAY_OF_YEAR,-1);
        Date dt = null; // ????????????????????????
        try {
            dt = c.getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dt;
    }


    /**
     * ????????????????????????
     * @param date
     * @return
     */
    public static String getYearEndDate(Date date){
        return format(getYearEndDay(date),SIMPLE_DATEFORMAT);
    }

    /**
     * ?????????????????????
     * @param date
     * @return
     */
    public static String getYearFiestDate(Date date){
        return format(getYearFiestDay(date),SIMPLE_DATEFORMAT);
    }

    /**
     * ?????????????????????  00:00:00?????????
     * @param date
     * @return
     */
    public static String getMonthFirstDate(Date date) {

        SimpleDateFormat tempDate = new SimpleDateFormat(DateUtils.SIMPLE_DATEFORMAT);
        Date dt = null; // ?????????????????????
        try {
            dt = new SimpleDateFormat(DateUtils.SIMPLE_DATEFORMAT_YM).parse(tempDate.format(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return DateUtils.format(dt, DateUtils.SIMPLE_DATEFORMAT);
    }

    /**
     * ????????????????????????  23:59:59?????????
     * @param date
     * @return
     */
    public static String getMonthLastDate(Date date) {
        return format(getMonthLastDay(new Date()),SIMPLE_DATEFORMAT);
    }


    public static String addDays(String date, String pattern, int num) {
        Date d = parseDate(date, pattern);
        d = addDays(d, num);
        return format(d, pattern);
    }

    public static Date parseDate(String date, String pattern) {
        if (StringUtils.isEmpty(date)) {
            return null;
        }
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        Date d = null;
        try {
            d = format.parse(date);
        } catch (ParseException e) {
            throw new BaseException("date error",date,e);
        }

        return d;
    }

    public static Date parseDateYMD(String date) {
        return parseDate(date,SIMPLE_DATEFORMAT_YMD);
    }

    public static Date parseDateYMD(Date date) {
        return parseDate(date,SIMPLE_DATEFORMAT_YMD);
    }

    public static Date parseDate(Date date, String pattern) {
        return parseDate(format(date,pattern),pattern);
    }

    public static Date parseDateYMDHMS(String date) {
        return parseDate(date,SIMPLE_DATEFORMAT);
    }

    public static Date parseDateYMDHMS(Date date) {
        return parseDate(date,SIMPLE_DATEFORMAT);
    }

    public static String format(Date date, String pattern) {
        if (date == null) {
            return null;
        }
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        return format.format(date);
    }

    public static long getDaySub(String beginDateStr) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar calendar = Calendar.getInstance();
        long nowDate = calendar.getTime().getTime(); // Date.getTime() ?????????????????????
        long betweenDate = 0;
        try {
            long specialDate = sdf.parse(beginDateStr).getTime();
            betweenDate = (specialDate - nowDate) / (1000 * 60 * 60 * 24); // ????????????????????????????????????????????????????????????
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return betweenDate;
    }

    /**
     *
     * @param change
     * @throws Exception
     *
     *             yyyy-MM-dd?????????yyyyMMdd
     */

    public static String parseStringToString(String change) {

        SimpleDateFormat from = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat to = new SimpleDateFormat("yyyyMMdd");
        Date temp = null;

        try {
            temp = from.parse(change);
            // changed=to.format(temp);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return to.format(temp);
    }

    // ???????????????????????? 2017-11-16 00:00:00
    public static Date getStartOfDay(){
        return getStartOfDay(new Date());
    }

    // ???????????????????????? 2017-11-16 00:00:00
    public static Date getStartOfDay(Date date) {
        LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(date.getTime()), ZoneId.systemDefault());
        LocalDateTime startOfDay = localDateTime.with(LocalTime.MIN);
        return Date.from(startOfDay.atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * ???????????????23:59:59???????????????
     *
     * @throws Exception
     */
    public static Date getEndOfDay(Date date) {
        LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(date.getTime()), ZoneId.systemDefault());;
        LocalDateTime endOfDay = localDateTime.with(LocalTime.MAX);
        return DateUtils.parseDateYMDHMS(Date.from(endOfDay.atZone(ZoneId.systemDefault()).toInstant()));
    }


    /**
     * ????????????23:59:59???????????????
     *
     * @throws Exception
     */
    public static Date getEndOfDay() {
        return getEndOfDay(new Date());
    }

    public static boolean compareTwoDate(Date first, Date second) throws ParseException {

        boolean flag = Boolean.TRUE;
        if (first.getTime() - second.getTime() > 0) {
            flag = Boolean.TRUE;
        } else {
            flag = Boolean.FALSE;
        }

        return flag;
    }

    public static int compareDate(Date first, Date second){
        return first.compareTo(second);
    }

    public static int compareDate(String first, String second){
        return compareDate(parseDateYMDHMS(first),parseDateYMDHMS(second));
    }

    /**
     * ?????? 24??????   ?????? :  00:00 01:00 02:00 03:00 04:00 05:00 06:00 07:00
     * @return
     */
    public static List<Date> getTimeSegment() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);//????????????
        int month=cal.get(Calendar.MONTH);//????????????
        int day=cal.get(Calendar.DATE);//?????????
        cal.set(year, month - 1, day, 0, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        long startTime = cal.getTimeInMillis();
        cal.set(year, month - 1, day, 23, 59, 59);
        long endTime = cal.getTimeInMillis();
        final int seg = 60 * 60 * 1000;// ?????????????????????
        ArrayList<Date> result = new ArrayList<Date>((int) ((endTime - startTime) / seg + 1));
        for (long time = startTime; time <= endTime; time += seg) {
            result.add(new Date(time));
        }
        return result;
    }

    public static int getDaysBetweenDates(String beginDate, String endDate){
        long time = Math.abs(parseDate(endDate,SIMPLE_DATEFORMAT_YMD).getTime() - parseDate(beginDate,SIMPLE_DATEFORMAT_YMD).getTime());
        long count = time/(24*3600*1000);
        return (int)count;
    }

    /**
     * ?????????????????? ?????? ???: 2017-01-01 2017-01-02 ?????? 2
     * @param beginDate yyyy-MM-dd
     * @param endDate yyyy-MM-dd
     * @return
     */
    public static int getDaysBetweenDate(String beginDate, String endDate){
        return getDaysBetweenDate(parseDate(beginDate,SIMPLE_DATEFORMAT_YMD),parseDate(endDate,SIMPLE_DATEFORMAT_YMD));
    }

    /**
     * ?????????????????? ???????????? ???: 2017-01-01 2017-01-02 ?????? 2017-01-01 2017-01-02
     * @param beginDate yyyy-MM-dd
     * @param endDate yyyy-MM-dd
     * @return
     */
    public static List<Date> getDatesBetweenDate(String beginDate, String endDate){
        return getDatesBetweenDate(parseDate(beginDate,SIMPLE_DATEFORMAT_YMD),parseDate(endDate,SIMPLE_DATEFORMAT_YMD));
    }

    /**
     * ?????????????????? ??????????????????????????????
     * @param beginDate
     * @param endDate
     * @return
     */
    public static int getDaysBetweenDate(Date beginDate, Date endDate){
        if(endDate.before(beginDate)){
            return getDatesBetweenDate(endDate,beginDate).size();
        }
        return getDatesBetweenDate(beginDate,endDate).size();
    }


    public static Date preDate(Date currDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currDate);
        calendar.set(Calendar.DATE, calendar.get(Calendar.DATE)-1);
        return calendar.getTime();
    }

    /**
     * ?????????????????? ????????????
     * @param beginDate
     * @param endDate
     * @return
     */
    public static List<Date> getDatesBetweenDate(Date beginDate, Date endDate) {
        beginDate = parseDate(beginDate,SIMPLE_DATEFORMAT_YMD);
        endDate = parseDate(endDate,SIMPLE_DATEFORMAT_YMD);
        List<Date> lDate = new ArrayList<Date>();
        lDate.add(beginDate);// ???????????????????????????
        Calendar cal = Calendar.getInstance(); // ??????????????? Date ????????? Calendar ?????????
        cal.setTime(beginDate);
        boolean bContinue = true;
        while (bContinue) { // ?????????????????????????????????????????????????????????????????????????????????
            cal.add(Calendar.DAY_OF_MONTH, 1); // ??????????????????????????????????????????
            if (endDate.after(cal.getTime())) {
                lDate.add(cal.getTime());
            } else if(beginDate.compareTo(endDate) == 0){
                break;
            } else {
                lDate.add(endDate);// ???????????????????????????
                break;
            }
        }

        return lDate;
    }


    /**
     * ??????7?????????????????????
     * @return
     */
    public static List<Date> sevenHourDates(){
        List<Date> lDate = new ArrayList<Date>();
        for(int i=1;i<8;i++) {
            lDate.add(DateUtils.addHours(new Date(), -(i)));
        }
        return lDate;
    }

    public static Date now(){
        return new Date();
    }

    /**
     * ?????????????????? yyyyMMddHHmmssSSS
     * @return
     */
    public static String getFormatNow(){
        return getFormatNow(SIMPLE_DATEFORMAT_YMDHMSS);
    }

    /**
     * ?????????????????? yyyyMMdd
     * @return
     */
    public static String getFormatYYYYMMDDNow(){
        return getFormatNow(SIMPLE_DATEFORMAT_YMD2);
    }
    /**
     * ??????????????????????????????
     * @param pattern
     * @return
     */
    public static String getFormatNow(String pattern){
        return format(new Date(),pattern);
    }

    /**
     * 	??????????????????????????????????????????????????????????????????
     *
     * @param startDate ?????????????????????
     * @param endDate ???????????????????????????????????????
     * @param newStartDate ?????????????????????
     * @return ?????????????????????
     */
    public static Date getMoveDate(Date startDate,Date endDate,Date newStartDate) {
        LocalDate start = startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate end = endDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate newStart = newStartDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        Period period = Period.between(start, newStart);
        LocalDate newEnd = end.plusYears(period.getYears());
        newEnd = newEnd.plusMonths(period.getMonths());
        newEnd = newEnd.plusDays(period.getDays());
        return localDateToUdate(newEnd);
    }

    /**
     * 	??????????????????????????????????????????????????????????????????
     *
     * @param start ?????????????????????
     * @param end ???????????????????????????????????????
     * @param newStart ?????????????????????
     * @return ?????????????????????
     */
    public static LocalDate getMoveDate(LocalDate start,LocalDate end,LocalDate newStart) {
        Period period = Period.between(start, newStart);
        // long period = ChronoUnit.DAYS.between(start, newStart);
        // LocalDate newEnd = end.plusDays(period);
        LocalDate newEnd = end.plusYears(period.getYears());
        newEnd = newEnd.plusMonths(period.getMonths());
        newEnd = newEnd.plusDays(period.getDays());
        // System.out.println("start date:"+start+" ||| "+"end date:"+end+" ||| "+"new start date:"+newStart+"----"+period.getYears()+"||"+period.getMonths()+"||"+period.getDays()+"------"+"new end date"+"---"+newEnd);
        return newEnd;
    }


    public static LocalDate getMoveDate1(LocalDate start,LocalDate end,LocalDate newStart) {
        Period period = Period.between(start, newStart);
        // long period = ChronoUnit.DAYS.between(start, newStart);
        // LocalDate newEnd = end.plusDays(period);
        LocalDate newEnd = end.plusYears(period.getYears());
        newEnd = newEnd.plusMonths(period.getMonths());
        newEnd = newEnd.plusDays(period.getDays());
        // System.out.println("start date:"+start+" ||| "+"end date:"+end+" ||| "+"new start date:"+newStart+"----"+period.getYears()+"||"+period.getMonths()+"||"+period.getDays()+"------"+"new end date"+"---"+newEnd);
        return newEnd;
    }

    public static Date localDateToUdate(LocalDate currDate) {
        ZoneId zone = ZoneId.systemDefault();
        Instant instant = currDate.atStartOfDay().atZone(zone).toInstant();
        return Date.from(instant);

    }

    public static LocalDate dateToLocalDate(Date currDate) {
        Instant instant = currDate.toInstant();
        ZoneId zoneId = ZoneId.systemDefault();
        LocalDate localDate = instant.atZone(zoneId).toLocalDate();
        return localDate;
    }

    /**
     * ????????????????????????
     * ???????????? 2017-01-02 , 1
     * ?????? 2017-02-01 23:59:59
     * @param startDate
     * @param month
     * @return
     */
    public static Date getAddMonthEndDate(Date startDate,int month){
        return getEndOfDay(parseDateYMD(getMonthFirstDate(addMonths(startDate,month))));
    }

    /**
     * ????????????????????????
     * ???????????? 2017-01-02 , 1
     * ?????? 2017-02-01 00:00:00
     * @param startDate
     * @param month
     * @return
     */
    public static Date getAddMonthStartDate(Date startDate,int month){
        return getStartOfDay((parseDateYMD(getMonthFirstDate(addMonths(startDate,month)))));
    }

    /**
     * ???????????????????????????
     * ?????? ?????? 2017-01-02,1
     * ?????? 2017-01-31 23:59:59
     * ?????????  ????????????????????????????????????
     * @param startDate ????????????
     * @param month ???
     * @return
     */
    public static Date getEndDate(Date startDate,int month){
        if (month == 0)
            return parseDate(getMonthLastDay(startDate),SIMPLE_DATEFORMAT);
        return parseDate(getMonthLastDay(addDays(addMonths(getMonthFirstDay(startDate),month),-1)),SIMPLE_DATEFORMAT);
    }

    /**
     * ?????????????????????
     * ?????? ?????? 2017-01-02,1
     * ?????? 2017-02-01 23:59:59
     * ?????????  ????????????????????????????????????
     * @param startDate ????????????
     * @param month ???
     * @return
     */
    public static Date getActualEndDate(Date startDate,int month){
        if (month == 0)
            return parseDate(getMonthLastDay(startDate),SIMPLE_DATEFORMAT);
        return parseDate(getEndOfDay(addDays(addMonths(startDate,month),-1)),SIMPLE_DATEFORMAT);
    }

    /**
     * ???????????????????????????????????????
     * ?????? ?????? 2017-01-02,1
     * ?????? 2017-01-02 23:59:59
     * @param date ????????????
     * @param day ???
     * @return
     */
    public static Date addDaysActualEndDate(Date date,int day){
        if (day == 0)
            return null;
        if (day == 1)
            return parseDate(getEndOfDay(date),SIMPLE_DATEFORMAT);
        return parseDate(getEndOfDay(addDays(addDays(date,day),-1)),SIMPLE_DATEFORMAT);
    }

    //	????????????????????????????????????2018-11-07--->2018-12-01???
    public static Date getPerFirstDayOfMonth(Date currDate) {
        SimpleDateFormat dft = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currDate);
        calendar.add(Calendar.MONTH, 1);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
        return calendar.getTime();
    }

    // ???????????????????????????????????????(2018-11-07--->2018-10-31)
    public static Date getLastMaxMonthDate(Date currDate) {
        SimpleDateFormat dft = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currDate);
        calendar.add(Calendar.MONTH, -1);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        return calendar.getTime();
    }

    //	?????????????????????????????????
    public static Date getContractMonthLastDay(Date startDate,Date endDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        c.setTime(startDate);

        int startDay=c.get(Calendar.DATE)-1;
        c.setTime(endDate);
        int endDay=c.get(Calendar.DATE);
        if(startDay>endDay) {
            c.setTime(endDate);
            c.set(Calendar.MONTH,c.get(c.MONTH)-1);
            c.set(Calendar.DATE,startDay);
        }else {
            c.setTime(endDate);
            c.set(Calendar.DATE,startDay);
        }
        return c.getTime();
    }

    //	?????????????????????????????????2018-11-07--->2018-11-01???
    public static Date getMinMonthDate(Date repeatDate){
        SimpleDateFormat dft = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        try {
            if(repeatDate!=null){
                calendar.setTime(repeatDate);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
        return calendar.getTime();
    }

    //???????????????????????????????????????2020-02-21--->29??????2018-02-21--->28??????
    public static int countDaysInMonth(Date date,int month){
        // ??????????????????
        Instant instant = date.toInstant();
        ZoneId zoneId = ZoneId.systemDefault();
        // atZone()?????????????????????????????????Instant?????????ZonedDateTime???
        LocalDate localDate = instant.atZone(zoneId).toLocalDate();

        // ????????????????????????????????????????????????
        LocalDate thisMonthDate = localDate.withMonth(month);
        return thisMonthDate.lengthOfMonth();
    }


    /**
     *LocalDateTime ??? Date
     *
     * @param time
     * @return
     */
    public static Date dateToDate(LocalDateTime time){
        return Date.from(time.atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * Date ??? LocalDateTime
     *
     * @param date
     * @return LocalDate
     */
//    public static LocalDate dateToLocalDate(Date date) {
//
//        return dateToLocalDateTime(date).toLocalDate();
//    }

    /**
     * Date ??? LocalDateTime
     *
     * @param date
     * @return LocalDateTime
     */
    public static LocalDateTime dateToLocalDateTime(Date date) {
        long nanoOfSecond = (date.getTime() % 1000) * 1000000;
        LocalDateTime localDateTime = LocalDateTime.ofEpochSecond(date.getTime() / 1000, (int) nanoOfSecond, ZoneOffset.of("+8"));

        return localDateTime;
    }

    /**
     * timestamp ??? LocalDateTime
     *
     * @param date
     * @return LocalDate
     */
    public static LocalDate timestampToLocalDate(Timestamp date) {

        return timestampToLocalDateTime(date).toLocalDate();
    }

    /**
     * Timestamp ??? LocalDateTime
     *
     * @param date
     * @return LocalDateTime
     */
    public static LocalDateTime timestampToLocalDateTime(Timestamp date) {
        LocalDateTime localDateTime = LocalDateTime.ofEpochSecond(date.getTime() / 1000, date.getNanos(), ZoneOffset.of("+8"));
        return localDateTime;
    }

    /**
     * localDateTime ??? ???????????????string
     *
     * @param localDateTime
     * @param format        ??????yyyy-MM-dd hh:mm:ss
     * @return
     */
    public static String formatLocalDateTimeToString(LocalDateTime localDateTime, String format) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
            return localDateTime.format(formatter);

        } catch (DateTimeParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * string ??? LocalDateTime
     *
     * @param dateStr ??????"2017-08-11 01:00:00"
     * @param format  ??????"yyyy-MM-dd HH:mm:ss"
     * @return
     */
    public static LocalDateTime stringToLocalDateTime(String dateStr, String format) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
            return LocalDateTime.parse(dateStr, formatter);
        } catch (DateTimeParseException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * ???????????????????????????????????????
     *
     * @param date
     * @return
     */
    public static int getActualMaximum(Date date) {

        return dateToLocalDateTime(date).getMonth().length(dateToLocalDate(date).isLeapYear());
    }

    /**
     * ????????????????????????
     *
     * @param date
     * @return 1:????????????2:????????????3:????????????4:????????????5:????????????6:????????????7:????????????
     */
    public static int getWeekOfDate(Date date) {
        return dateToLocalDateTime(date).getDayOfWeek().getValue();
    }


    /**
     * ??????????????????LocalDate??????????????????????????????????????????????????????>=0
     *
     * @param before
     * @param after
     * @return
     */
    public static int getAbsDateDiffDay(LocalDate before, LocalDate after) {

        return Math.abs(Period.between(before, after).getDays());
    }

    /**
     * ??????????????????LocalDateTime??????????????????????????????????????????????????????>=0
     *
     * @param before
     * @param after
     * @return
     */
    public static int getAbsTimeDiffDay(LocalDateTime before, LocalDateTime after) {

        return Math.abs(Period.between(before.toLocalDate(), after.toLocalDate()).getDays());
    }

    /**
     * ??????????????????LocalDateTime??????????????????????????????????????????????????????>=0
     *
     * @param before
     * @param after
     * @return
     */
    public static int getAbsTimeDiffMonth(LocalDateTime before, LocalDateTime after) {

        return Math.abs(Period.between(before.toLocalDate(), after.toLocalDate()).getMonths());
    }

    /**
     * ??????????????????LocalDateTime??????????????????????????????????????????????????????>=0
     *
     * @param before
     * @param after
     * @return
     */
    public static int getAbsTimeDiffYear(LocalDateTime before, LocalDateTime after) {

        return Math.abs(Period.between(before.toLocalDate(), after.toLocalDate()).getYears());
    }


    /**
     * ?????????????????????????????????
     *
     * @param date ??????
     * @return 1-7 1????????????,2:?????????,3:?????????,4:?????????,5:?????????,6:?????????,7:?????????
     */
    public static int getDayOfWeek(Date date) {
        Calendar cal = new GregorianCalendar();
        cal.setTime(date);
        return cal.get(Calendar.DAY_OF_WEEK);
    }

    /**
     * ???????????????????????????????????????
     *
     * @param date
     * @return
     */
    public static int getLastMonth(Date date) {
        return dateToLocalDateTime(date).getMonth().getValue();

    }

    /**
     * ??????????????????????????????
     *
     * @param date
     * @return
     */
    public static LocalDate newThisMonth(Date date) {
        LocalDate localDate = dateToLocalDate(date);
        return LocalDate.of(localDate.getYear(), localDate.getMonth(), 1);
    }

    /**
     * ?????????????????????????????????
     *
     * @param date
     * @return
     */
    public static LocalDate lastThisMonth(Date date) {
        int lastDay = getActualMaximum(date);
        LocalDate localDate = dateToLocalDate(date);
        return LocalDate.of(localDate.getYear(), localDate.getMonth(), lastDay);
    }

    /**
     * ??????????????????????????????
     *
     * @param date
     * @return
     */
    public static LocalDate newThisYear(Date date) {
        LocalDate localDate = dateToLocalDate(date);
        return LocalDate.of(localDate.getYear(), 1, 1);
    }

    public static Timestamp getCurrentDateTime() {
        return new Timestamp(Instant.now().toEpochMilli());
    }

    /**
     * ??????????????????
     *
     * @return LocalDateTime
     */
    public static LocalDateTime getCurrentLocalDateTime() {
        return LocalDateTime.now(Clock.system(ZoneId.of("Asia/Shanghai")));

    }


    /**
     * ?????????????????????????????????
     *
     * @param date
     * @param customTime ?????????"hh:mm:ss"????????????
     */
    public static LocalDateTime reserveDateCustomTime(Date date, String customTime) {
        String dateStr = dateToLocalDate(date).toString() + " " + customTime;
        return stringToLocalDateTime(dateStr, "yyyy-MM-dd HH:mm:ss");
    }

    /**
     * ?????????????????????????????????
     *
     * @param date
     * @param customTime ?????????"hh:mm:ss"????????????
     */
    public static LocalDateTime reserveDateCustomTime(Timestamp date, String customTime) {
        String dateStr = timestampToLocalDate(date).toString() + " " + customTime;
        return stringToLocalDateTime(dateStr, "yyyy-MM-dd HH:mm:ss");
    }

    /**
     * ????????????????????????0 ??????(yyyy-MM-dd 00:00:00:000)
     *
     * @param date
     * @return LocalDateTime
     */
    public static final LocalDateTime zerolizedTime(Date date) {
        LocalDateTime localDateTime = dateToLocalDateTime(date);
        return LocalDateTime.of(localDateTime.getYear(), localDateTime.getMonth(), localDateTime.getDayOfMonth(), 0, 0, 0, 0);

    }

    /**
     * ???????????????(yyyy-MM-dd 00:00:00:000)
     *
     * @param date
     * @return LocalDateTime
     */
    public static LocalDateTime zerolizedTime(Timestamp date) {
        LocalDateTime localDateTime = timestampToLocalDateTime(date);
        return LocalDateTime.of(localDateTime.getYear(), localDateTime.getMonth(), localDateTime.getDayOfMonth(), 0, 0, 0, 0);
    }

    /**
     * ????????????????????????(yyyy-MM-dd 23:59:59:999)
     *
     * @param date
     * @return LocalDateTime
     */
    public static LocalDateTime getEndTime(Date date) {
        LocalDateTime localDateTime = dateToLocalDateTime(date);
        return LocalDateTime.of(localDateTime.getYear(), localDateTime.getMonth(), localDateTime.getDayOfMonth(), 23, 59, 59, 999 * 1000000);
    }

    /**
     * ???????????????(yyyy-MM-dd 23:59:59:999)
     *
     * @param date
     * @return LocalDateTime
     */
    public static LocalDateTime getEndTime(Timestamp date) {
        LocalDateTime localDateTime = timestampToLocalDateTime(date);
        return LocalDateTime.of(localDateTime.getYear(), localDateTime.getMonth(), localDateTime.getDayOfMonth(), 23, 59, 59, 999 * 1000000);
    }

    /**
     * ????????????????????? ?????? 23.59.59.999 ?????????
     *
     * @return
     */
    public static int calculateToEndTime(Date date) {
        LocalDateTime localDateTime = dateToLocalDateTime(date);
        LocalDateTime end = getEndTime(date);
        return (int) (end.toEpochSecond(ZoneOffset.UTC) - localDateTime.toEpochSecond(ZoneOffset.UTC));
    }


    /**
     * ??????????????????/???/???/???/??????/???/??????
     *
     * @param localDateTime ??????ChronoUnit.DAYS
     * @param chronoUnit
     * @param num
     * @return LocalDateTime
     */
    public static LocalDateTime addTime(LocalDateTime localDateTime, ChronoUnit chronoUnit, int num) {
        return localDateTime.plus(num, chronoUnit);
    }

    /**
     * ??????????????????/???/???/???/??????/???/??????
     *
     * @param chronoUnit ??????ChronoUnit.DAYS
     * @param num
     * @return LocalDateTime
     */
    public static LocalDateTime addTime(Date date, ChronoUnit chronoUnit, int num) {
        long nanoOfSecond = (date.getTime() % 1000) * 1000000;
        LocalDateTime localDateTime = LocalDateTime.ofEpochSecond(date.getTime() / 1000, (int) nanoOfSecond, ZoneOffset.of("+8"));
        return localDateTime.plus(num, chronoUnit);
    }

    /**
     * ??????????????????/???/???/???/??????/???/??????
     *
     * @param chronoUnit ??????ChronoUnit.DAYS
     * @param num
     * @return LocalDateTime
     */
    public static LocalDateTime addTime(Timestamp date, ChronoUnit chronoUnit, int num) {
        long nanoOfSecond = (date.getTime() % 1000) * 1000000;
        LocalDateTime localDateTime = LocalDateTime.ofEpochSecond(date.getTime() / 1000, (int) nanoOfSecond, ZoneOffset.of("+8"));
        return localDateTime.plus(num, chronoUnit);
    }



    /**
     * ????????????LocalDateTime???????????????
     *
     * @param begin
     * @param end
     * @return
     */
    public static boolean isTheSameDay(LocalDateTime begin, LocalDateTime end) {
        return begin.toLocalDate().equals(end.toLocalDate());
    }


    /**
     * ??????????????????LocalDateTime??????
     *
     * @param time1
     * @param time2
     * @return 1:???????????????????????????0?????????????????????????????????-1???????????????????????????
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
     * ??????time1,time2????????????????????????????????????time1<=time2,??????0
     *
     * @param time1
     * @param time2
     */
    public static long getTwoTimeDiffSecond(Timestamp time1, Timestamp time2) {
        long diff = timestampToLocalDateTime(time1).toEpochSecond(ZoneOffset.UTC) - timestampToLocalDateTime(time2).toEpochSecond(ZoneOffset.UTC);
        if (diff > 0) {
            return diff;
        } else {
            return 0;
        }
    }

    /**
     * ??????time1,time2???????????????????????????????????????time1<=time2,??????0
     *
     * @param time1
     * @param time2
     */
    public static long getTwoTimeDiffMin(Timestamp time1, Timestamp time2) {
        long diff = getTwoTimeDiffSecond(time1, time2) / 60;
        if (diff > 0) {
            return diff;
        } else {
            return 0;
        }
    }

    /**
     * ??????time1,time2???????????????????????????????????????time1<=time2,??????0
     *
     * @param time1
     * @param time2
     */
    public static long getTwoTimeDiffHour(Timestamp time1, Timestamp time2) {
        long diff = getTwoTimeDiffSecond(time1, time2) / 3600;
        if (diff > 0) {
            return diff;
        } else {
            return 0;
        }
    }

    /**
     * ??????????????????????????????????????????
     *
     * @param startTime
     * @param endTime
     * @return
     */
    public static boolean isTimeInRange(Date startTime, Date endTime){
        return isTimeInRange(new Date(),startTime,endTime);
    }

    /**
     * ????????????????????????????????????
     *
     * @param time
     * @param startTime
     * @param endTime
     * @return
     */
    public static boolean isTimeInRange(Date time,Date startTime, Date endTime) {
        LocalDateTime now = dateToLocalDateTime(time);
        LocalDateTime start = dateToLocalDateTime(startTime);
        LocalDateTime end = dateToLocalDateTime(endTime);
        return (start.isBefore(now) && end.isAfter(now)) || start.isEqual(now) || end.isEqual(now);
    }

    public static boolean isLastDayOfMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.DAY_OF_MONTH) == calendar
                .getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    public static boolean isFirstDayOfMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.DAY_OF_MONTH) == 1;
    }

    public static int monthNum(Date statDate,Date endDate) {
        Calendar bef = Calendar.getInstance();
        Calendar aft = Calendar.getInstance();
        bef.setTime(statDate);
        aft.setTime(endDate);
        int result = aft.get(Calendar.MONTH) - bef.get(Calendar.MONTH);
        int month = (aft.get(Calendar.YEAR) - bef.get(Calendar.YEAR)) * 12;
        return Math.abs(month + result);
    }

    public static int getInterval(Date begin_date, Date end_date){
        int daysBetween = (int) ((end_date.getTime() - begin_date.getTime()) / (1000*3600*24));
        return daysBetween;
    }

    public static Date addNumDays(Date currDate,int num) {
        Calendar c = Calendar.getInstance();
        c.setTime(currDate);
        c.add(Calendar.DAY_OF_MONTH, num);
        return c.getTime();
    }

    /**
     * 	??????local??????
     * @param localStart
     * @param summaryNum
     * @return
     */
    public static Date getEndDate(LocalDate localStart,long summaryNum) {
        LocalDate currentEnd = localStart.plusMonths(summaryNum);
        currentEnd = currentEnd.minusDays(1);
        return localDateToUdate(currentEnd);
    }

    public static Date getLocalEndDate(LocalDate startDate,Integer summaryNum) {
//    	LocalDate start = DateUtils.dateToLocalDate(startDate);
        LocalDate currentEnd = startDate.plusMonths(summaryNum);
        currentEnd = currentEnd.minusDays(1);
        Date realEndDate = DateUtils.localDateToUdate(currentEnd);
        return realEndDate;
    }

    public static Date getLocalStartDate(LocalDate startDate,Integer summaryNum) {
//    	LocalDate start = DateUtils.dateToLocalDate(startDate);
        LocalDate currentEnd = startDate.plusMonths(summaryNum);
//		currentEnd = currentEnd.minusDays(1);
        Date realEndDate = DateUtils.localDateToUdate(currentEnd);
        return realEndDate;
    }

    public static Date setDateHMS(Date date,int hour,int minute,int second){
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalTime localTime = LocalTime.of(hour,minute,second);
        LocalDateTime localDateTime = LocalDateTime.of(localDate,localTime);
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    public static Date getYesterdayLastDate(Date date){
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        localDate = localDate.minusDays(1);
        LocalTime localTime = LocalTime.of(23,59,59);
        LocalDateTime localDateTime = LocalDateTime.of(localDate,localTime);
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * ??????????????????
     * @param num [+1????????????????????????(??????)???-1????????????????????????(??????)]
     * @return
     */
    public static Date getAppointDay(int num){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, num);
        return calendar.getTime();
    }

    /**
     * ?????????????????????????????? minute ??????
     * @param date
     * @param minute
     * @return
     */
    public static Date addMinutes(Date date, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date.getTime());
        calendar.add(Calendar.MINUTE, minute);
        return calendar.getTime();
    }

    public static Date getDateByYearMonthDay(Integer year,Integer month,Integer day){
        Calendar cd = Calendar.getInstance();
        if (year != null && month != null && day != null){
            cd.set(Calendar.YEAR,year);
            cd.set(Calendar.MONTH,month-1);
            cd.set(Calendar.DAY_OF_MONTH,day);
        }
        return cd.getTime();
    }

    public static Integer getYear(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date.getTime());
        return calendar.get(Calendar.YEAR);
    }

    public static Date getQarterFirstDay(Date date){
        int year = DateUtils.getYear(date);
        int month = DateUtils.getMonth(date);
        if(month>=1 && month<=3){
            return DateUtils.getDateByYearMonthDay(year,1,1);
        }else if(month>=4 && month<=6){
            return DateUtils.getDateByYearMonthDay(year,4,1);
        }else if(month>=7 && month<=9){
            return DateUtils.getDateByYearMonthDay(year,7,1);
        }else{
            return DateUtils.getDateByYearMonthDay(year,10,1);
        }
    }

    public static int getQarter(Date date){
        int month = DateUtils.getMonth(date);
        if(month>=1 && month<=3){
            return 1;
        }else if(month>=4 && month<=6){
            return 2;
        }else if(month>=7 && month<=9){
            return 3;
        }else{
            return 4;
        }
    }

    public static Date getQarterFirstDay(int year,int qarter){
        int month = 1;
        if(qarter==2){
            month = 4;
        }else if(qarter==3){
            month = 7;
        }else if(qarter==4){
            month = 10;
        }
        return DateUtils.getDateByYearMonthDay(year,month,1);
    }

    public static Date getQarterLastDay(int year,int qarter){
        int month = 3;
        int day = 31;
        if(qarter==2){
            month = 6;
            day = 30;
        }else if(qarter==3){
            month = 9;
            day = 30;
        }else if(qarter==4){
            month = 12;
            day = 31;
        }
        return DateUtils.getDateByYearMonthDay(year,month,day);
    }

    public static void main(String[] args) {
        System.out.println(checkWeekend());

//        System.out.println(DateUtils.parseDate("2021-01-11 00:00:01",DateUtils.DATE_TIME_PATTERN).compareTo(DateUtils.now()) > 0);
    }
}
