package com.powerboot.utils.infobip.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.infobip.ApiClient;
import com.infobip.ApiException;
import com.powerboot.consts.DictConsts;
import com.powerboot.dao.SmsSendResponse;
import com.powerboot.utils.RedisUtils;
import com.powerboot.utils.StringRandom;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class VoiceMessageSendUtil {

    @SneakyThrows
    public JSONObject sendVoiceMessage(String sendMsg, String tel) {
        String authorization = "App 45073603838c74ce00d53b29ca1005f2-297dfccb-04c4-4014-8b13-10e90e3d23c6";
        String from = "2347080631326";
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("application/json");
        String content = "{\n" +
                "\t\"messages\": [{\n" +
                "\t\t\"from\": \"" + from + "\",\n" +
                "\t\t\"destinations\": [{\n" +
                "\t\t\t\"to\": \"" + tel + "\"\n" +
                "\t\t}],\n" +
                "\t\t\"text\": \"" + sendMsg + "\",\n" +
                "\t\t\"speechRate\": 0.6\n" +
                "\n" +
                "\t}]\n" +
                "}";
        log.info("send voice message : msg:{}, tel:{}", sendMsg, tel);
        RequestBody body = RequestBody.create(mediaType, content);
        Request request = new Request.Builder()
                .url("https://qgdn6m.api.infobip.com/tts/3/advanced")
                .method("POST", body)
                .addHeader("Authorization", authorization)
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .build();
        Response response = client.newCall(request).execute();
        log.info("send voice message : response:{}", response);
        String respBody;
        try {
            if (response.body() != null) {
                respBody = response.body().string();
            } else {
                respBody = null;
            }
        } catch (IOException var6) {
            throw new ApiException(var6);
        }
        log.info("send voice message : respBody:{}", respBody);
        return JSONObject.parseObject(respBody);
    }

    public static void main(String[] args) {
        String sms = StringRandom.getStringRandom(6);
        String tel = "2349065572116";
        StringBuilder g = new StringBuilder("Your verification code is ");
        for (char c : sms.toCharArray()) {
            g.append(c).append(" ");
        }
        System.out.println(g.toString());
        System.out.println(g.toString() + " " + g.toString());
        VoiceMessageSendUtil voiceMessageSendUtil = new VoiceMessageSendUtil();
        JSONObject jsonObject = voiceMessageSendUtil.sendVoiceMessage(g.toString() + " "
                + g.toString() + " " + g.toString(), tel);
        SmsSendResponse smsSendResponse = new SmsSendResponse();
        if (null != jsonObject) {
            JSONArray messages = jsonObject.getJSONArray("messages");
            if (CollectionUtils.isNotEmpty(messages)) {
                JSONObject message = messages.getJSONObject(0);
                JSONObject status = message.getJSONObject("status");
                smsSendResponse.setMsgId(message.getString("messageId"));
                smsSendResponse.setError(status.getString("description"));
                if ("PENDING".equalsIgnoreCase(status.getString("groupName"))) {
                    smsSendResponse.setCode("0");
                } else {
                    smsSendResponse.setCode("500");
                }
            }
        }
        System.out.println(smsSendResponse);
    }

}
