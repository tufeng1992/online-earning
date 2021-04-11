package com.powerboot.utils;

import java.util.Random;

/**
 * Create  2020 - 07 - 15 10:20 上午
 */
public class StringRandom {

    private final static String RANDOM_CHAR = "abcdefghjkmnpqrstuvwxyz23456789";
    /**
     * 默认4位
     *
     * @return
     */
    public static String getStringRandom() {
        return getStringRandom(4);
    }

    //生成随机数字
    public static String getStringRandom(Integer length) {
        if (length==null || length == 0) {
            length = 4;
        }
        String val = "";
        Random random = new Random();

        //参数length，表示生成几位随机数
        for (int i = 0; i < length; i++) {
            val += String.valueOf(random.nextInt(10));
        }
        return val;
    }

    public static String getNumberAndLetterRandom(Integer length) {
        if (length==null || length == 0) {
            length = 10;
        }
        Random random = new Random();
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int num = random.nextInt(RANDOM_CHAR.length()-1);
            buf.append(RANDOM_CHAR.charAt(num));
        }
        return buf.toString();
    }

    public static void main(String[] args) {
        StringRandom test = new StringRandom();
        //测试
        System.out.println(test.getNumberAndLetterRandom(8));
    }
}
