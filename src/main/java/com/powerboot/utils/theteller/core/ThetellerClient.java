package com.powerboot.utils.theteller.core;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.powerboot.consts.DictConsts;
import com.powerboot.utils.DateUtils;
import com.powerboot.utils.RedisUtils;
import com.powerboot.utils.gms.core.SignAPI;
import com.powerboot.utils.gms.core.SignUtil;
import com.powerboot.utils.gms.model.GmsCreatePayOutRes;
import com.powerboot.utils.gms.model.GmsCreatePayRes;
import com.powerboot.utils.grecash.model.BaseGrecashRes;
import com.powerboot.utils.grecash.model.QueryPayRes;
import com.powerboot.utils.opay.HttpUtil;
import com.powerboot.utils.theteller.model.ThetellerCreatePay;
import com.powerboot.utils.theteller.model.ThetellerCreatePayOut;
import com.powerboot.utils.theteller.model.ThetellerQueryPay;
import lombok.extern.slf4j.Slf4j;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * theteller 支付调用
 */
@Component
@Slf4j
public class ThetellerClient {

    private String getBaseUrl() {
        return RedisUtils.getValue(DictConsts.THETELLER_BASE_URL, String.class);
    }

    private String getMerchantId() {
        return RedisUtils.getValue(DictConsts.THETELLER_MERCHANT_ID, String.class);
    }

    private String getPayNotifyUrl() {
        return RedisUtils.getValue(DictConsts.THETELLER_PAY_NOTIFY_URL, String.class);
    }

    private String getApiUser() {
        return RedisUtils.getValue(DictConsts.THETELLER_API_USER, String.class);
    }

    private String getApiKey() {
        return RedisUtils.getValue(DictConsts.THETELLER_API_KEY, String.class);
    }

    private String getPasscode() {
        return RedisUtils.getString(DictConsts.THETELLER_PASSCODE);
    }

    private Map<String, String> getHeader() {
        String authorization = getAuthorization();
        Map<String, String> header = new HashMap<>();
        header.put("Authorization", authorization);
        header.put("Content-Type", "application/json");
        return header;
    }

    private String getAuthorization() {
        String encodeStr = getApiUser() + ":" + getApiKey();
        String enrpt = Base64.getEncoder().encodeToString(encodeStr.getBytes());
        return "Basic " + enrpt;
    }

    /**
     * 创建支付
     * @param amount
     * @param email
     * @return
     */
    public ThetellerCreatePay createPay(BigDecimal amount, String email) {
        Map<String, String> map = new HashMap<>();
        map.put("amount", converAmount(amount));
        String transactionId = createTransactionId(12);
        map.put("transaction_id", transactionId);
        map.put("merchant_id", getMerchantId());
        map.put("redirect_url", getPayNotifyUrl());
        map.put("desc", "User payment description: goods payment");
        map.put("email", email);
        String url = getBaseUrl() + "/checkout/initiate";
        log.info("thetellerCreatePay:{}", map.toString());
        JSONObject jsonObject = HttpUtil.post4JsonObj(url, getHeader(), map).orElseThrow(() -> new RuntimeException("请求响应为空"));
        ThetellerCreatePay thetellerCreatePay = jsonObject.toJavaObject(ThetellerCreatePay.class);
        thetellerCreatePay.setTransactionId(transactionId);
        return thetellerCreatePay;
    }

    /**
     * 查询支付信息
     * @param thirdOrderNo
     */
    public ThetellerQueryPay queryPay(String thirdOrderNo) {
        String url = getBaseUrl() + "/v1.1/users/transactions/" + thirdOrderNo + "/status";
        Map<String, String> header = getHeader();
        header.put("Merchant-Id", getMerchantId());
        JSONObject j = HttpUtil.get4JsonObj(url, Maps.newHashMap(), header).orElseThrow(() -> new RuntimeException("请求响应为空"));
        return j.toJavaObject(ThetellerQueryPay.class);
    }


    /**
     * 创建转账
     * @param amount
     * @param accountNumber
     * @param bankCode
     * @return
     */
    public ThetellerCreatePayOut createTransfer(String orderNo, BigDecimal amount,
                                                String accountNumber, String bankCode) {
        String transactionId = createTransactionId(12);
        Map<String, String> map = new HashMap<>();
        map.put("amount", converAmount(amount));
        map.put("merchant_id", getMerchantId());
        map.put("desc", "Transfer to user: Merchant service fee");
        map.put("transaction_id", transactionId);
        map.put("r-switch", "FLT");
        map.put("processing_code", "404000");
        map.put("pass_code", getPasscode());
        map.put("account_issuer", bankCode);
        map.put("account_number", accountNumber);
        String url = getBaseUrl() + "/v1.1/transaction/process";

        HttpRequest httpRequest = cn.hutool.http.HttpUtil.createPost(url);

        getHeader().forEach((k, v) -> {
            httpRequest.header(k, v, true);
        });
        String json = JSONObject.toJSONString(map);
        log.info("theteller, createTransfer req:" + json);
        HttpResponse httpResponse = httpRequest.body(json).execute();
        String repBody = httpResponse.body();
        log.info("theteller, createTransfer resp:" + repBody);
        ThetellerCreatePayOut thetellerCreatePay = JSONObject.parseObject(repBody, ThetellerCreatePayOut.class);
        return thetellerCreatePay;
    }

//    /**
//     * 查询转账
//     * @param orderNo
//     * @return
//     */
//    public GmsCreatePayOutRes queryTransfer(String orderNo) {
//        Map<String, String> map = new HashMap<>();
//        map.put("mch_id", getMerchantId());
//        map.put("mch_transferId", orderNo);
//        String sign = SignUtil.sortData(map);
//        sign = SignAPI.sign(sign, getPayOutKey());
//        map.put("sign_type", "MD5");
//        map.put("sign", sign);
//        String url = getBaseUrl() + "/query/transfer";
//        FormBody.Builder builder = new FormBody.Builder();
//        map.forEach(builder::add);
//        RequestBody body = builder.build();
//        JSONObject jsonObject = HttpUtil.invokePostRequest(url, body).orElseThrow(() -> new RuntimeException("请求响应为空"));
//        return jsonObject.toJavaObject(GmsCreatePayOutRes.class);
//    }

    private static String converAmount(BigDecimal amount) {
        return String.format("%012d", amount.multiply(new BigDecimal("100")).intValue());
    }

    private static String createTransactionId(int count) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < count; i++) {
            sb.append(RandomUtil.randomInt(10));
        }
        return sb.toString();
    }

}
