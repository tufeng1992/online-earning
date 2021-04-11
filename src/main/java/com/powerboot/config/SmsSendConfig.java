package com.powerboot.config;


import com.alibaba.fastjson.JSON;
import com.powerboot.base.BaseResponse;
import com.powerboot.common.HttpRequestUtils;
import com.powerboot.consts.DictConsts;
import com.powerboot.dao.SmsSendRequest;
import com.powerboot.dao.SmsSendResponse;
import com.powerboot.utils.DateUtils;
import com.powerboot.utils.RedisUtils;
import com.powerboot.utils.SmsUtil;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;


public class SmsSendConfig {
    Logger logger = LoggerFactory.getLogger(SmsSendConfig.class);

    public BaseResponse<SmsSendResponse> sendKenya(String tel, String msg) {
//        String requestURL = "http://www.aaa.com/api/sendsms";
//        String api_key = "123";
//        String username = "123";
//        String sender_id = "123";
//        Map<String,Object> requestJson = new HashMap<>();
//        requestJson.put("api_key",api_key);
//        requestJson.put("username",username);
//        requestJson.put("sender_id",sender_id);
//        requestJson.put("message",msg);
//        requestJson.put("phone",tel);
//        logger.info("before request string is: " + requestJson);
//        JSONArray response = HttpRequestUtils.doPostJsonArray(requestURL, requestJson);
//        logger.info("response after request result is :" + response);
//        if (response == null){
//            return BaseResponse.fail("unknown error");
//        }
//        JSONObject jsonObject = response.getJSONObject(0);
//        SmsSendResponse smsSingleResponse = new SmsSendResponse();
//        smsSingleResponse.setCode(jsonObject.getString("status"));
//        smsSingleResponse.setError(jsonObject.getString("response"));
//        smsSingleResponse.setMsgId(String.valueOf(jsonObject.getInt("message_id")));
//        smsSingleResponse.setTime(DateUtils.getFormatNow());
//        logger.info("response  toString is :" + smsSingleResponse);
        SmsSendResponse smsSingleResponse = new SmsSendResponse();
        smsSingleResponse.setCode("0");
        return BaseResponse.success(smsSingleResponse);
    }

    public static void main(String[] args) {
        String requestURL = "http://111.aaa.com/api/sendsms";
        String api_key = "21122";
        String username = "123";
        String sender_id = "123";
        String msg = "test";
        String tel = "231123";
        Map<String,Object> requestJson = new HashMap<>();
        requestJson.put("api_key",api_key);
        requestJson.put("username",username);
        requestJson.put("sender_id",sender_id);
        requestJson.put("message",msg);
        requestJson.put("phone",tel);
        JSONArray response = HttpRequestUtils.doPostJsonArray(requestURL, requestJson);
        JSONObject jsonObject = response.getJSONObject(0);
        SmsSendResponse smsSingleResponse = new SmsSendResponse();
        smsSingleResponse.setCode(jsonObject.getString("status"));
        smsSingleResponse.setError(jsonObject.getString("response"));
        smsSingleResponse.setMsgId(String.valueOf(jsonObject.getInt("message_id")));
        smsSingleResponse.setTime(DateUtils.getFormatNow());
        System.out.println(response.toString());
        System.out.println(smsSingleResponse.toString());
    }

    public BaseResponse<SmsSendResponse> send(String tel, String msg) {
        String SMS_SINGLE_REQUEST_SERVER_URL = String.valueOf(RedisUtils.getValue(DictConsts.SMS_SINGLE_REQUEST_SERVER_URL, String.class));
        String SMS_ACCOUNT = String.valueOf(RedisUtils.getValue(DictConsts.SMS_ACCOUNT, String.class));
        String SMS_PASSWORD = String.valueOf(RedisUtils.getValue(DictConsts.SMS_PASSWORD, String.class));

        SmsSendRequest sendRequest = new SmsSendRequest(SMS_ACCOUNT, SMS_PASSWORD, msg, tel);
        String requestJson = JSON.toJSONString(sendRequest);
        logger.info("before request string is: " + requestJson);
        String response = SmsUtil.sendSmsByPost(SMS_SINGLE_REQUEST_SERVER_URL, requestJson);
        logger.info("response after request result is :" + response);
        SmsSendResponse smsSingleResponse = JSON.parseObject(response, SmsSendResponse.class);
        logger.info("response  toString is :" + smsSingleResponse);
        return BaseResponse.success(smsSingleResponse);
    }
}
