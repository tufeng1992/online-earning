package com.powerboot.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.powerboot.base.BaseResponse;
import com.powerboot.consts.DictConsts;
import com.powerboot.enums.ServerAreaEnum;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.StopWatch;

public class MobileUtil {
    private static Pattern indiaMobile =  Pattern.compile("^(\\+?91|0)?[6789]\\d{9}$");

    /**
     * 尼日利亚号码前缀
     */
    public static final String NIGERIA_MOBILE_PREFIX = "234";

    /**
     * 泰国号码前缀
     */
    public static final String THAILAND_MOBILE_PREFIX = "66";

    /**
     * 南非号码前缀
     */
    public static final String SOUTH_AFRICA_MOBILE_PREFIX = "27";

    /**
     * 判断尼日利亚手机号合法性
     * @param mobile
     * @return
     */
    public static boolean isNigeriaMobile(String mobile) {
        if (StringUtils.isBlank(mobile)) {
            return false;
        }
        int index = mobile.length();
        if (index == 10 || (index == 13 && mobile.substring(0, 3).equals(NIGERIA_MOBILE_PREFIX))) {
            return true;
        }
        return false;
    }

    /**
     * 判断南非手机号合法性
     * @param mobile
     * @return
     */
    public static boolean isSouthAfricaMobile(String mobile) {
        if (StringUtils.isBlank(mobile)) {
            return false;
        }
        int index = mobile.length();
        if ((index == 10 || index == 9)
                || ((index == 12 || index == 11) && mobile.substring(0, 2).equals(SOUTH_AFRICA_MOBILE_PREFIX))) {
            return true;
        }
        return false;
    }

    /**
     * 判断手机号合法性
     * @param mobile
     * @return
     */
    public static boolean isValidMobile(String mobile) {
        if (StringUtils.isBlank(mobile)) {
            return false;
        }
        int index = mobile.length();
        String area = RedisUtils.getValue(DictConsts.SERVER_AREA, String.class);
        if (com.powerboot.utils.StringUtils.isEmpty(area) || ServerAreaEnum.THAILAND.getCode().equalsIgnoreCase(area)) {
            if ((index == 10 || index == 9)
                    || ((index == 12 || index == 11) && mobile.substring(0, 2).equals(THAILAND_MOBILE_PREFIX))) {
                return true;
            }
        } else if (ServerAreaEnum.NIGERIA.getCode().equalsIgnoreCase(area)){
            return isNigeriaMobile(mobile);
        } else {
            return isSouthAfricaMobile(mobile);
        }
        return false;
    }

    /**
     * 替换手机号前缀
     * @param mobile
     * @return
     */
    public static String replaceValidMobile(String mobile) {
        if (com.powerboot.utils.StringUtils.isEmpty(mobile)) {
            return mobile;
        }
        int index = mobile.length();
        String area = RedisUtils.getValue(DictConsts.SERVER_AREA, String.class);
        if (com.powerboot.utils.StringUtils.isEmpty(area) || ServerAreaEnum.THAILAND.getCode().equalsIgnoreCase(area)) {
            if (index == 10 || index == 9) {
                return THAILAND_MOBILE_PREFIX + mobile;
            }
        } else if (ServerAreaEnum.NIGERIA.getCode().equalsIgnoreCase(area)){
            return MobileUtil.NIGERIA_MOBILE_PREFIX + mobile.substring(index-10);
        } else {
            if (index == 10 || index == 9) {
                return SOUTH_AFRICA_MOBILE_PREFIX + mobile;
            }
        }
        return mobile;
    }

    /**
     * 获取虚拟手机号集合
     * @param count
     * @return
     */
    public static List<String> getIndiaMobileList(int count){
        List<String> list = new ArrayList<>();
        String area = RedisUtils.getValue(DictConsts.SERVER_AREA, String.class);
        if (com.powerboot.utils.StringUtils.isEmpty(area) || ServerAreaEnum.THAILAND.getCode().equalsIgnoreCase(area)) {
            for(int i=0;i<count;i++){
                list.add(getThMobile());
            }
        } else if (ServerAreaEnum.NIGERIA.getCode().equalsIgnoreCase(area)) {
            for(int i=0;i<count;i++){
                list.add(getNigeriaMobile());
            }
        } else {
            for(int i=0;i<count;i++){
                list.add(getSouthAfricaMobile());
            }
        }
        return list;
    }

    private static final String[] prefixRandom = {"80", "81", "86", "89"};

    /**
     * 获取泰国随机号码
     * @return
     */
    public static String getThMobile(){
        String s1 = getRandom(0,4);
        //String s2 = getRandom(0,9);
        //String s3 = getRandom(0,9);
        String e1 = getRandom(0,9);
        String e2 = getRandom(0,9);
        String e3 = getRandom(0,9);
        String e4 = getRandom(0,9);
        String temp = prefixRandom[Integer.valueOf(s1)];
        return "66 " +temp+ "****" + e1 + e2 + e3 ;
    }

    /**
     * 获取尼日利亚随机号码
     * @return
     */
    public static String getNigeriaMobile() {
        String s1 = getRandom(1,9);
        //String s2 = getRandom(0,9);
        //String s3 = getRandom(0,9);
        String e1 = getRandom(0,9);
        String e2 = getRandom(0,9);
        String e3 = getRandom(0,9);
        String e4 = getRandom(0,9);
        return "234 7" +s1+ "****" + e1 + e2 + e3 ;
    }

    /**
     * 获取南非随机号码
     * @return
     */
    public static String getSouthAfricaMobile() {
        String s1 = getRandom(1,9);
        //String s2 = getRandom(0,9);
        //String s3 = getRandom(0,9);
        String e1 = getRandom(0,9);
        String e2 = getRandom(0,9);
        String e3 = getRandom(0,9);
        String e4 = getRandom(0,9);
        return "27 7" +s1+ "****" + e1 + e2 + e3 ;
    }

    public static String getRandom(int min, int max){
        Random random = new Random();
        int s = random.nextInt(max) % (max - min + 1) + min;
        return String.valueOf(s);

    }

    public static void main(String[] args) {
    }

}
