package com.powerboot.utils.happylife.core;

import cn.hutool.core.math.Money;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.powerboot.consts.DictConsts;
import com.powerboot.utils.DateUtils;
import com.powerboot.utils.RedisUtils;
import com.powerboot.utils.gms.core.SignAPI;
import com.powerboot.utils.gms.core.SignUtil;
import com.powerboot.utils.happylife.model.HappyLifePayOutRes;
import com.powerboot.utils.happylife.model.HappyLifePayRes;
import com.powerboot.utils.happylife.model.HappyLifeQueryPayOutRes;
import com.powerboot.utils.happylife.model.HappyLifeQueryPayRes;
import com.powerboot.utils.opay.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.*;

@Component
@Slf4j
public class HappyLifeClient {

    private String getBaseUrl() {
        return RedisUtils.getValue(DictConsts.HAPPY_LIFE_BASE_URL, String.class);
    }

    private String getMerchantId() {
        return RedisUtils.getValue(DictConsts.HAPPY_LIFE_MERCHANT_ID, String.class);
    }



    private String getPayNotifyUrl() {
        return RedisUtils.getValue(DictConsts.HAPPY_LIFE_PAY_NOTIFY_URL, String.class);
    }

    private String getPayOutNotifyUrl() {
        return RedisUtils.getValue(DictConsts.HAPPY_LIFE_PAY_OUT_NOTIFY_URL, String.class);
    }

    private String getPayRedirectUrl() {
        return RedisUtils.getValue(DictConsts.HAPPY_LIFE_PAY_REDIRECT_URL, String.class);
    }

    private String getPayKey() {
        return RedisUtils.getValue(DictConsts.HAPPY_LIFE_PAY_KEY, String.class);
    }

    /**
     * 创建支付
     * @param orderNo
     * @param amount
     * @return
     */
    public HappyLifePayRes createPay(String orderNo, BigDecimal amount, Long userId) {
        Map<String, String> map = new HashMap<>();
        map.put("amount", amount.toString());
        map.put("identify", getMerchantId());
        map.put("uid", userId.toString());
        map.put("desc", "commodity charge");
        map.put("ref", orderNo);
        map.put("notify_url", getPayNotifyUrl());
        map.put("successurl", getPayRedirectUrl());
        map.put("failureurl", getPayRedirectUrl());
        String sign = SignUtil.sortData(map);
        sign = SignAPI.sign(sign, getPayKey());
        map.put("sign", sign);
        String url = getBaseUrl() + "/paymass/init/process";
        log.info("HappyLifeCreatePay:{}", map.toString());
        FormBody.Builder builder = new FormBody.Builder();
        map.forEach(builder::add);
        RequestBody body = builder.build();
        JSONObject jsonObject = HttpUtil.invokePostRequest(url, body).orElseThrow(() -> new RuntimeException("请求响应为空"));
        return jsonObject.toJavaObject(HappyLifePayRes.class);
    }

    /**
     * 查询支付信息
     * @param localOrderNo
     */
    public HappyLifeQueryPayRes queryPay(String localOrderNo) {
        Map<String, String> map = new HashMap<>();
        map.put("identify", getMerchantId());
        map.put("mer_order_no", localOrderNo);
        String sign = SignUtil.sortData(map);
        sign = SignAPI.sign(sign, getPayKey());
        map.put("sign", sign);
        log.info("HappyLifeQueryPay:{}", map.toString());
        FormBody.Builder builder = new FormBody.Builder();
        map.forEach(builder::add);
        RequestBody body = builder.build();
        String url = getBaseUrl() + "/paymass/query/order";
        JSONObject j = HttpUtil.invokePostRequest(url, body).orElseThrow(() -> new RuntimeException("请求响应为空"));
        return j.toJavaObject(HappyLifeQueryPayRes.class);
    }


    /**
     * 创建转账
     * @param orderNo
     * @param amount
     * @param name
     * @param accountNumber
     * @param bankName
     * @return
     */
    public HappyLifePayOutRes createTransfer(String orderNo, BigDecimal amount,
                                               String name, String accountNumber, String bankName, Long userId) {
        Map<String, String> map = new HashMap<>();
        map.put("money", amount.toString());
        map.put("identify", getMerchantId());
        map.put("callback_url", getPayOutNotifyUrl());
        map.put("order_no", orderNo);
        map.put("user_id", userId.toString());
        map.put("bank_number", convertBankCode(bankName));
        map.put("acc_name", name);
        map.put("acc_no", accountNumber);
        String sign = SignUtil.sortData(map);
        sign = SignAPI.sign(sign, getPayKey());
        map.put("sign", sign);
        String url = getBaseUrl() + "/payingweb/payother/withdrawal";
        FormBody.Builder builder = new FormBody.Builder();
        map.forEach(builder::add);
        RequestBody body = builder.build();
        JSONObject jsonObject = HttpUtil.invokePostRequest(url, body).orElseThrow(() -> new RuntimeException("请求响应为空"));
        return jsonObject.toJavaObject(HappyLifePayOutRes.class);
    }

    /**
     * 查询转账
     * @param orderNo
     * @return
     */
    public HappyLifeQueryPayOutRes queryTransfer(String orderNo) {
        Map<String, String> map = new HashMap<>();
        map.put("identify", getMerchantId());
        map.put("order_no", orderNo);
        String sign = SignUtil.sortData(map);
        sign = SignAPI.sign(sign, getPayKey());
        map.put("sign", sign);
        String url = getBaseUrl() + "/payingweb/query/getPayotherOneOrder";
        FormBody.Builder builder = new FormBody.Builder();
        map.forEach(builder::add);
        RequestBody body = builder.build();
        JSONObject jsonObject = HttpUtil.invokePostRequest(url, body).orElseThrow(() -> new RuntimeException("请求响应为空"));
        return jsonObject.toJavaObject(HappyLifeQueryPayOutRes.class);
    }

    private Map<String, String> happyBankCode = Maps.newHashMap();

    @PostConstruct
    public void init() {
        happyBankCode.put("MTN", "NGMTN");
        happyBankCode.put("AirtelTigo", "NGTIGO");
        happyBankCode.put("Vodafone", "NGVODAFONE");
    }

    /**
     * 适配转换银行代码
     * @param bankCode
     * @return
     */
    private String convertBankCode(String bankCode) {
        return happyBankCode.get(bankCode);
    }

}
