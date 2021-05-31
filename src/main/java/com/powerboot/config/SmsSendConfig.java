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
import com.powerboot.utils.chuanglan.model.request.SmsSendGJRequest;
import com.powerboot.utils.chuanglan.util.ChuangLanSmsUtil;
import com.powerboot.utils.infobip.utils.VoiceMessageSendUtil;
import org.apache.commons.collections.CollectionUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;


public class SmsSendConfig {

    Logger logger = LoggerFactory.getLogger(SmsSendConfig.class);

    @Autowired
    private VoiceMessageSendUtil voiceMessageSendUtil;

    public BaseResponse<SmsSendResponse> sendKenya(String tel, String msg) {
        //请求地址请登录253云通讯自助通平台查看或者询问您的商务负责人获取
        String smsSingleRequestServerUrl = RedisUtils.getString(DictConsts.CL_SMS_SEND_GJ_URL);
        // 短信内容
        tel = tel.replaceAll(" ", "");
        SmsSendGJRequest smsSingleRequest = new SmsSendGJRequest(RedisUtils.getString(DictConsts.CL_SMS_SEND_GJ_ACCOUNT),
                RedisUtils.getString(DictConsts.CL_SMS_SEND_GJ_PASSWORD), msg, tel);
        String requestJson = JSON.toJSONString(smsSingleRequest);
        logger.info("before request string is: " + requestJson);
        String response = ChuangLanSmsUtil.sendSmsByPost(smsSingleRequestServerUrl, requestJson);
        logger.info("response after request result is :" + response);
        SmsSendResponse smsSingleResponse = JSON.parseObject(response, SmsSendResponse.class);
        logger.info("response  toString is :" + smsSingleResponse);
        return BaseResponse.success(smsSingleResponse);
    }

    /**
     * 发送语音消息
     * @param tel
     * @param msg
     * @return
     */
    public BaseResponse<SmsSendResponse> sendVoiceMessage(String tel, String msg) {
        StringBuilder g = new StringBuilder("Your verification code is ");
        StringBuilder s = new StringBuilder(" code is ");
        for (char c : msg.toCharArray()) {
            g.append(c).append("    ");
            s.append(c).append("    ");
        }
        com.alibaba.fastjson.JSONObject res =
                voiceMessageSendUtil.sendVoiceMessage(g.toString() + "        "
                + s.toString() + "        " + s.toString() + "        " + s.toString(), tel);
        SmsSendResponse smsSendResponse = new SmsSendResponse();
        if (null != res) {
            com.alibaba.fastjson.JSONArray messages = res.getJSONArray("messages");
            if (CollectionUtils.isNotEmpty(messages)) {
                com.alibaba.fastjson.JSONObject message = messages.getJSONObject(0);
                com.alibaba.fastjson.JSONObject status = message.getJSONObject("status");
                smsSendResponse.setMsgId(message.getString("messageId"));
                smsSendResponse.setError(status.getString("description"));
                if ("PENDING".equalsIgnoreCase(status.getString("groupName"))) {
                    smsSendResponse.setCode("0");
                } else {
                    smsSendResponse.setCode("500");
                }
            }
        }
        return BaseResponse.success(smsSendResponse);
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
