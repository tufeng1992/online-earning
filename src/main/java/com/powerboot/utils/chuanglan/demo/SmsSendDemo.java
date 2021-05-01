package com.powerboot.utils.chuanglan.demo;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.alibaba.fastjson.JSON;
import com.powerboot.dao.SmsSendResponse;
import com.powerboot.utils.chuanglan.model.request.SmsSendGJRequest;
import com.powerboot.utils.chuanglan.model.request.SmsSendRequest;
import com.powerboot.utils.chuanglan.util.ChuangLanSmsUtil;

/**
 *
 * @author tianyh
 * @Description:普通短信发送
 */
public class SmsSendDemo {
    public static final String charset = "utf-8";
    // 用户平台API账号(非登录账号,示例:N1234567)
    public static String account = "I3202461";
    // 用户平台API密码(非登录密码)
    public static String password = "Fq5a0ZbAy";

    public static void main(String[] args) throws UnsupportedEncodingException {
        //请求地址请登录253云通讯自助通平台查看或者询问您的商务负责人获取
        String smsSingleRequestServerUrl = "http://intapi.253.com/send/json";
        // 短信内容
        String msg = "【TaskP】 your verification code is 011256. To ensure information security, please do not tell others.";
        //手机号码
        String phone = "2348130191788";

        SmsSendGJRequest smsSingleRequest = new SmsSendGJRequest(account, password, msg, phone);

        String requestJson = JSON.toJSONString(smsSingleRequest);

        System.out.println("before request string is: " + requestJson);

        String response = ChuangLanSmsUtil.sendSmsByPost(smsSingleRequestServerUrl, requestJson);

        System.out.println("response after request result is :" + response);

        SmsSendResponse smsSingleResponse = JSON.parseObject(response, SmsSendResponse.class);

        System.out.println("response  toString is :" + smsSingleResponse);
    }
}
