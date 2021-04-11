package com.powerboot.utils.opay;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.google.errorprone.annotations.concurrent.LazyInit;
import com.google.gson.Gson;
import com.powerboot.config.BaseException;
import com.powerboot.consts.DictConsts;
import com.powerboot.utils.RedisUtils;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.apache.commons.codec.binary.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * CreatedDate: 2020/12/15
 */
@Component
public class OPayClient  {
    private static final String HMAC_SHA512 = "HmacSHA512";
    private final Map<String, String> headers = new HashMap<>();

    public static final String CLOSE_WEB_VIEW_KEY_WORD = "closethisvxwebview";

    public String getSecretKey() {
        return RedisUtils.getValue(DictConsts.OPAY_SECURT_KEY, String.class);
    }

    public String getMerchantId() {
        return RedisUtils.getValue(DictConsts.OPAY_MERCHANTID, String.class);
    }

    public String getPublicKey() {
        return RedisUtils.getValue(DictConsts.OPAY_PUBLIC_KEY, String.class);
    }

    public String getGatewayUrl() {
        return RedisUtils.getValue(DictConsts.OPAY_GATEWAY_URL, String.class);
    }


    public void afterPropertiesSet() throws Exception {
        headers.put("MerchantId", getMerchantId().trim());
        headers.put("Authorization", "Bearer " + getPublicKey().trim());
        headers.put("Accept", "Application/json");
    }

    public PayReply cardInitialize(BigDecimal amount, String cardNo, String cardCvc, String expireYear, String expireMonth) {
        JSONObject body = new JSONObject();
        body.put("payType", "bankcard");
        body.put("cardNumber", cardNo);
        body.put("cardCVC", cardCvc);
        body.put("cardDateMonth", expireMonth);
        body.put("cardDateYear", expireYear);
        return initialize(body, amount);
    }

    public PayReply tokenInitialize(BigDecimal amount, String token) {
        JSONObject body = new JSONObject();
        body.put("payType", "token");
        body.put("token", token);
        return initialize(body, amount);
    }

    private PayReply initialize(JSONObject body, BigDecimal amount) {
        body.put("reference", generateInnerOrderNo());
        body.put("amount", amount.multiply(new BigDecimal(100)));
        body.put("currency", "NGN");
        body.put("country", "NG");
        String url = getGatewayUrl() + "/transaction/initialize";
        body.put("return3dsUrl", "https://cashierapi.opayweb.com/" + CLOSE_WEB_VIEW_KEY_WORD);
        return HttpUtil.post4JsonObj(url, this.headers, body).map(this::parseError).orElseThrow(() -> new RuntimeException("请求响应为空")).toJavaObject(PayReply.class);
    }

    public PayReply submitOtp(String innerOrderNo, String channelOrderNo, String otp) {
        JSONObject body = new JSONObject();
        body.put("reference", innerOrderNo);
        body.put("orderNo", channelOrderNo);
        body.put("otp", otp);
        String url = getGatewayUrl() + "/transaction/input-otp";
        return HttpUtil.post4JsonObj(url, this.headers, body).map(this::parseError).orElseThrow(() -> new RuntimeException("请求响应为空")).toJavaObject(PayReply.class);
    }

    public PayReply submitPhone(String innerOrderNo, String channelOrderNo, String phone) {
        if (!phone.startsWith("+234")) {
            if (phone.startsWith("234")) {
                phone = "+" + phone;
            } else {
                phone = "+234" + phone;
            }
        }
        JSONObject body = new JSONObject();
        body.put("reference", innerOrderNo);
        body.put("orderNo", channelOrderNo);
        body.put("phone", phone);
        String url = getGatewayUrl() + "/transaction/input-phone";
        return HttpUtil.post4JsonObj(url, this.headers, body).map(this::parseError).orElseThrow(() -> new RuntimeException("请求响应为空")).toJavaObject(PayReply.class);
    }

    public PayReply submitPin(String innerOrderNo, String channelOrderNo, String pin) {
        JSONObject body = new JSONObject();
        body.put("reference", innerOrderNo);
        body.put("orderNo", channelOrderNo);
        body.put("pin", pin);
        String url = getGatewayUrl() + "/transaction/input-pin";
        return HttpUtil.post4JsonObj(url, this.headers, body).map(this::parseError).orElseThrow(() -> new RuntimeException("请求响应为空")).toJavaObject(PayReply.class);
    }

    @SneakyThrows
    public PayReply queryStatus(String innerOrderNo, String channelOrderNo) {
        //TODO 签名？
        JSONObject body = new JSONObject();
        body.put("reference", innerOrderNo);
        body.put("orderNo", channelOrderNo);
        TreeMap<String, Object> parameters = new TreeMap<>(body);
        Gson gson = new Gson();
        String json = gson.toJson(parameters);
        String sign = hmacSha512(json, getSecretKey());
        String url = getGatewayUrl() + "/transaction/status";
        Map<String, String> thisHeaders = Maps.newHashMap(this.headers);
        thisHeaders.put("Authorization", "Bearer " + sign);
        Request request = HttpUtil.newPostRequest(url, null, thisHeaders, RequestBody.create(HttpUtil.MEDIA_TYPE_JSON_UTF_8, StringUtils.getBytesUtf8(json)));
        return HttpUtil.invoke4JsonObj(request).map(this::parseError).orElseThrow(() -> new RuntimeException("请求响应为空")).toJavaObject(PayReply.class);
    }

    /**
     * 初始化转帐到银行帐户交易
      * @param reference
     * @param amount
     * @param reason
     * @param receiverName
     * @param receiverBankCode
     * @param receiverBankAccountNumber
     * @return
     */
    @SneakyThrows
    public JSONObject transferToBank(String reference, String amount, String reason,
                                   String receiverName, String receiverBankCode, String receiverBankAccountNumber) {
        JSONObject body = new JSONObject();
        body.put("reference", reference);
        body.put("amount", amount);
        body.put("currency", "NGN");
        body.put("country", "NG");
        JSONObject receiverObj = new JSONObject();
        receiverObj.put("name", receiverName);
        receiverObj.put("bankCode", receiverBankCode);
        receiverObj.put("bankAccountNumber", receiverBankAccountNumber);
        body.put("receiver", receiverObj);
        body.put("reason", reason);
        String url = getGatewayUrl() + "/transfer/toBank";
        TreeMap<String, Object> parameters = new TreeMap<>(body);
        Gson gson = new Gson();
        String json = gson.toJson(parameters);
        String sign = hmacSha512(json, getSecretKey());
        Map<String, String> thisHeaders = Maps.newHashMap(this.headers);
        thisHeaders.put("Authorization", "Bearer " + sign);
        Request request = HttpUtil.newPostRequest(url, null, thisHeaders, RequestBody.create(HttpUtil.MEDIA_TYPE_JSON_UTF_8, StringUtils.getBytesUtf8(json)));
        return HttpUtil.invoke4JsonObj(request).map(this::parseError).orElseThrow(() -> new RuntimeException("请求响应为空"));
    }

    /**
     * 获取转账到银行状态
     * @param reference
     * @param orderNo
     * @return
     */
    public JSONObject getTransferToBankStatus(String reference, String orderNo) {
        JSONObject body = new JSONObject();
        body.put("reference", reference);
        body.put("orderNo", orderNo);
        String url = getGatewayUrl() + "/transfer/status/toBank";
        return HttpUtil.post4JsonObj(url, this.headers, body).map(this::parseError).orElseThrow(() -> new RuntimeException("请求响应为空"));
    }

    private JSONObject parseError(JSONObject it) {
        String code = it.getString("code");
        if (!StringUtils.equals("00000", code)) {
            throw new BaseException(it.getString("message"));
        }
        return it.getJSONObject("data");
    }

    public String hmacSha512(String data, String key) throws NoSuchAlgorithmException, InvalidKeyException {
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(), HMAC_SHA512);
        Mac mac = Mac.getInstance(HMAC_SHA512);
        mac.init(secretKeySpec);
        return toHexString(mac.doFinal(data.getBytes()));
    }

    private String toHexString(byte[] bytes) {
        Formatter formatter = new Formatter();
        for (byte b : bytes) {
            formatter.format("%02x", b);
        }
        return formatter.toString();
    }

    private String generateInnerOrderNo() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    @Data
    public static class PayReply {

        private String orderNo;
        private String reference;
        private String status;
        private String token;
        private String authURL;
        private String failureReason;

        public String getInnerOrderNo() {
            return reference;
        }

        public String getChannelOrderNo() {
            return orderNo;
        }

        public PayResult getPayResult() {
            return PayResult.find(status);
        }
    }
}
