package com.powerboot.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.powerboot.base.BaseResponse;
import com.powerboot.consts.DictConsts;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.StopWatch;

public class MobileUtil {
    private static Pattern indiaMobile =  Pattern.compile("^(\\+?91|0)?[6789]\\d{9}$");

    /**
     * 尼日利亚号码前缀
     */
    public static final String NIGERIA_MOBILE_PREFIX = "234";

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

    public static List<String> getIndiaMobileList(int count){
        List<String> list = new ArrayList<>();
        for(int i=0;i<count;i++){
            list.add(getIndiaMobile());
        }
        return list;
    }

    public static String getIndiaMobile(){
        String s1 = getRandom(1,9);
        //String s2 = getRandom(0,9);
        //String s3 = getRandom(0,9);
        String e1 = getRandom(0,9);
        String e2 = getRandom(0,9);
        String e3 = getRandom(0,9);
        String e4 = getRandom(0,9);
        return "254 7" +s1+ "****" + e1 + e2 + e3 ;
    }

    public static String getRandom(int min, int max){
        Random random = new Random();
        int s = random.nextInt(max) % (max - min + 1) + min;
        return String.valueOf(s);

    }

    public static void main(String[] args) {
        String phone = "0742872342";
        if (phone != null && phone.length() == 10 && "0".equals(phone.subSequence(0,1))){
            phone = phone.substring(1,phone.length());
        }
        int index = phone.length();
        String tel =  "234"+phone;
        System.out.println(tel);
        System.out.println(isNigeriaMobile(tel));
        System.out.println(isNigeriaMobile(phone));
    }

}
