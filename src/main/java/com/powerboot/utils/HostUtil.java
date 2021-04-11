package com.powerboot.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class HostUtil {
    private static Logger logger = LoggerFactory.getLogger(HostUtil.class);
    public static  String getHostIP(){
        try {
            InetAddress addr = InetAddress.getLocalHost();
            logger.info("IP地址:" + addr.getHostAddress() + "，主机名:" + addr.getHostName());
            return addr.getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
       return "";
    }

}
