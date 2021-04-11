package com.powerboot.utils;

import java.util.UUID;

public class CodeUtil {

    public static String uuid(){
       return UUID.randomUUID().toString().replaceAll("-", "").toLowerCase();
    }

}
