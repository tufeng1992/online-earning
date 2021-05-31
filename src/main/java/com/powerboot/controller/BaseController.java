package com.powerboot.controller;

import com.powerboot.config.BaseException;
import com.powerboot.consts.I18nEnum;
import com.powerboot.consts.TipConsts;
import com.powerboot.request.BaseRequest;
import com.powerboot.utils.CryptoUtils;
import java.lang.reflect.Field;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 基础
 */

public class BaseController {

    public static Logger logger = LoggerFactory.getLogger(BaseController.class);

    /**
     * 获取客户端IP
     * @param request
     * @return
     */
    public String getIp(HttpServletRequest request){

        try {
            //this.logger.info("x-forwarded-for = {}", request.getHeader("x-forwarded-for"));
            String ip = request.getHeader("X-Forwarded-For");
            if (!StringUtils.isEmpty(ip) && !"unKnown".equalsIgnoreCase(ip)) {
                //多次反向代理后会有多个ip值，第一个ip才是真实ip
                int index = ip.indexOf(",");
                if (index != -1) {
                    return ip.substring(0, index);
                } else {
                    return ip;
                }
            }
            ip = request.getHeader("X-Real-IP");
            if (!StringUtils.isEmpty(ip) && !"unKnown".equalsIgnoreCase(ip)) {
                return ip;
            }
            return request.getRemoteAddr();
        }catch (Exception e){
            //logger.error("获取唯一ip异常,异常::",e);
        }
        //logger.info("尝试获取ip");
        String ip = request.getHeader("X-real-ip");
        //logger.debug("x-forwarded-for = {}", ip);
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
            //logger.debug("Proxy-Client-IP = {}", ip);
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
            //logger.debug("WL-Proxy-Client-IP = {}", ip);
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
            //logger.debug("RemoteAddr-IP = {}", ip);
        }
        if (ip != null && ip.length() > 0 && ip.trim().length() > 0) {
            ip = ip.split(",")[0];
        }
        //System.out.println("ip=========" + ip);
        //logger.debug("Proxy-Client-IP = {}", ip);
        return ip;
    }

    public Integer getUserIdInt(Object obj){
        return getUserId(obj).intValue();
    }

    public Long getUserId(Object obj){
        Field[] fields;
        Class clazz = obj.getClass().getSuperclass();
        if(clazz!=null && clazz.getName().equals("com.powerboot.request.BaseRequest")){
            fields = obj.getClass().getSuperclass().getDeclaredFields();
        }else{
            fields = obj.getClass().getDeclaredFields();
        }
        try {
            for (Field field : fields) {
                field.setAccessible(true);
                if(field.getName().equals("sign")){
                    String value = (String) field.get(obj);
                    if (value == null){
                        throw new BaseException(I18nEnum.LOGIN_TIMEOUT_FAIL.getMsg());
                    }
                    return Long.parseLong(CryptoUtils.decode(value));
                }
            }
        } catch (Exception e) {
            logger.error("获取用户id失败", e);
            throw new BaseException(I18nEnum.LOGIN_TIMEOUT_FAIL.getMsg());
        }
        throw new BaseException(I18nEnum.LOGIN_TIMEOUT_FAIL.getMsg());
    }

    public static void main(String[] args) {
        System.out.println(CryptoUtils.encode("1"));
    }

}
