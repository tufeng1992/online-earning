package com.powerboot.utils.infobip.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.infobip.ApiClient;
import com.infobip.ApiException;
import com.infobip.Configuration;
import com.infobip.api.SendSmsApi;
import com.infobip.model.SmsAdvancedTextualRequest;
import com.infobip.model.SmsDestination;
import com.infobip.model.SmsResponse;
import com.infobip.model.SmsTextualMessage;
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
import java.util.Collections;

@Component
@Slf4j
public class InfobMessageSendUtil {

    //轮询标记
    private volatile Integer balanceIndex = 0;

    @SneakyThrows
    public JSONObject sendVoiceMessage(String sendMsg, String tel) {
//        String authorization = "App 45073603838c74ce00d53b29ca1005f2-297dfccb-04c4-4014-8b13-10e90e3d23c6";
//        String from = "2347080631326";
        String authorization = "App " + RedisUtils.getString(DictConsts.INFOBIP_VOICE_MESSAGE_API_KEY);
        String from = getFromBalance();

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
                "\t\t\"speechRate\": 0.5\n" +
                "\n" +
                "\t}]\n" +
                "}";
        String baseUrl = RedisUtils.getString(DictConsts.INFOBIP_VOICE_MESSAGE_BASE_URL);
        log.info("send voice message : msg:{}, tel:{}", sendMsg, tel);
        RequestBody body = RequestBody.create(mediaType, content);
        Request request = new Request.Builder()
                .url(baseUrl + "/tts/3/advanced")
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

    /**
     * 发送短信
     * @param sendMsg
     * @param tel
     */
    public SmsResponse sendSms(String sendMsg, String tel) {
        ApiClient apiClient = new ApiClient();
        apiClient.setApiKeyPrefix("App");
        apiClient.setApiKey(RedisUtils.getString(DictConsts.INFOBIP_VOICE_MESSAGE_API_KEY));
        apiClient.setBasePath(RedisUtils.getString(DictConsts.INFOBIP_VOICE_MESSAGE_BASE_URL));

        SendSmsApi sendSmsApi = new SendSmsApi(apiClient);
        SmsTextualMessage smsMessage = new SmsTextualMessage()
                .from(getFromBalance())
                .addDestinationsItem(new SmsDestination().to(tel))
                .text(sendMsg);
        SmsAdvancedTextualRequest smsMessageRequest = new SmsAdvancedTextualRequest().messages(
                Collections.singletonList(smsMessage));
        SmsResponse smsResponse = null;
        try {
            smsResponse = sendSmsApi.sendSmsMessage(smsMessageRequest);
            log.info("infob sendSms:" + smsResponse);
        } catch (ApiException apiException) {
            apiException.printStackTrace();
        }
        return smsResponse;
    }

    /**
     * 轮询获取from
     * @return
     */
    private String getFromBalance() {
        String from = RedisUtils.getString(DictConsts.INFOBIP_VOICE_MESSAGE_FROM);
        String[] fromArr = from.split(",");
        synchronized (this) {
            if (balanceIndex >= fromArr.length) {
                balanceIndex = 0;
            }
            return fromArr[balanceIndex++];
        }
    }

    public static void main(String[] args) {
        InfobMessageSendUtil voiceMessageSendUtil = new InfobMessageSendUtil();
//
//        String sms = StringRandom.getStringRandom(6);
//        String tel = "2349065572116";
//        StringBuilder g = new StringBuilder("Your verification code is ");
//        StringBuilder s = new StringBuilder(" code is ");
//        for (char c : sms.toCharArray()) {
//            g.append(c).append("    ");
//            s.append(c).append("    ");
//        }
//        System.out.println(g.toString());
//        System.out.println(g.toString() + " " + g.toString());
//
//        JSONObject jsonObject = voiceMessageSendUtil.sendVoiceMessage(g.toString() + "        "
//                + s.toString() + "        " + s.toString() + "        " + s.toString(), tel);
//        SmsSendResponse smsSendResponse = new SmsSendResponse();
//        if (null != jsonObject) {
//            JSONArray messages = jsonObject.getJSONArray("messages");
//            if (CollectionUtils.isNotEmpty(messages)) {
//                JSONObject message = messages.getJSONObject(0);
//                JSONObject status = message.getJSONObject("status");
//                smsSendResponse.setMsgId(message.getString("messageId"));
//                smsSendResponse.setError(status.getString("description"));
//                if ("PENDING".equalsIgnoreCase(status.getString("groupName"))) {
//                    smsSendResponse.setCode("0");
//                } else {
//                    smsSendResponse.setCode("500");
//                }
//            }
//        }
//        System.out.println(smsSendResponse);
        for (int i = 0; i < 10; i++) {
            System.out.println(voiceMessageSendUtil.getFromBalance());
        }

    }

}
