package com.powerboot.utils;

import org.apache.commons.codec.digest.DigestUtils;

public class MD5Util {

    public static String stringToMD5(String plainText) {
        return DigestUtils.md5Hex(plainText);
    }

    public static String sha512Hex(String plainText) {
        return DigestUtils.sha512Hex(plainText);
    }


}
