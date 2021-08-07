package com.powerboot.utils.happylife.core;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.powerboot.consts.DictConsts;
import com.powerboot.utils.DateUtils;
import com.powerboot.utils.RedisUtils;
import com.powerboot.utils.gms.core.SignAPI;
import com.powerboot.utils.gms.core.SignUtil;
import com.powerboot.utils.opay.HttpUtil;
import com.powerboot.utils.sepro.model.SeproCreatePayOutRes;
import com.powerboot.utils.sepro.model.SeproCreatePayRes;
import com.powerboot.utils.wallyt.core.WallytRSAUtil;
import com.powerboot.utils.wallyt.domain.dto.*;
import lombok.extern.slf4j.Slf4j;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;

@Component
@Slf4j
public class HappyLifeClient {

    private String getBaseUrl() {
        return RedisUtils.getValue(DictConsts.SEPRO_BASE_URL, String.class);
    }

    private String getMerchantId() {
        return RedisUtils.getValue(DictConsts.SEPRO_MERCHANT_ID, String.class);
    }

    private String getPayNotifyUrl() {
        return RedisUtils.getValue(DictConsts.SEPRO_PAY_NOTIFY_URL, String.class);
    }

    private String getPayOutNotifyUrl() {
        return RedisUtils.getValue(DictConsts.SEPRO_PAY_OUT_NOTIFY_URL, String.class);
    }

    private String getPayRedirectUrl() {
        return RedisUtils.getValue(DictConsts.SEPRO_PAY_REDIRECT_URL, String.class);
    }

    private String getPayInKey() {
        return RedisUtils.getValue(DictConsts.SEPRO_PAY_IN_KEY, String.class);
    }

    private String getPayOutKey() {
        return RedisUtils.getValue(DictConsts.SEPRO_PAY_OUT_KEY, String.class);
    }

    private String getDefaultPayType() {
        String type = RedisUtils.getValue(DictConsts.SEPRO_DEFAULT_PAY_TYPE, String.class);
        return StringUtils.isNotBlank(type) ? type : "321";
    }

    /**
     * 创建支付
     * @param orderNo
     * @param amount
     * @param bankCode
     * @return
     */
    public SeproCreatePayRes createPay(String orderNo, BigDecimal amount, String bankCode) {
        Map<String, String> map = new HashMap<>();
        map.put("amount", amount.toString());
        map.put("identify", "Beeearning897");
        map.put("desc", "desctest");
        map.put("ref", orderNo);
        map.put("notify_url", "https://www.baidu.com");
        map.put("successurl", "http://3.16.84.30/paysuccess/index.html");
        map.put("failureurl", "http://3.16.84.30/paysuccess/index.html");
        String sign = SignUtil.sortData(map);
        sign = SignAPI.sign(sign, "AFFWSSRT%47RE");
        map.put("sign", sign);
        String url = "https://payment.happylife231.xyz/apipay/order/pay";
        log.info("SeproCreatePay:{}", map.toString());
        FormBody.Builder builder = new FormBody.Builder();
        map.forEach(builder::add);
        RequestBody body = builder.build();
        JSONObject jsonObject = HttpUtil.invokePostRequest(url, body).orElseThrow(() -> new RuntimeException("请求响应为空"));
        return jsonObject.toJavaObject(SeproCreatePayRes.class);
    }

//    /**
//     * 查询支付信息
//     * @param thirdOrderNo
//     */
//    public BaseGrecashRes<QueryPayRes> queryPay(String thirdOrderNo) {
//        String url = getBaseUrl() + "/core/api/payment/payorder/" + thirdOrderNo;
//        JSONObject j = HttpUtil.get4JsonObj(url, Maps.newHashMap(), getHeader()).orElseThrow(() -> new RuntimeException("请求响应为空"));
//        BaseGrecashRes res = j.toJavaObject(BaseGrecashRes.class);
//        return res;
//    }


    /**
     * 创建转账
     * @param orderNo
     * @param amount
     * @param name
     * @param accountNumber
     * @param bankCode
     * @return
     */
    public SeproCreatePayOutRes createTransfer(String orderNo, BigDecimal amount,
                                               String name, String accountNumber, String bankCode) {
        Map<String, String> map = new HashMap<>();
        map.put("transfer_amount", amount.toString());
        map.put("mch_id", getMerchantId());
        map.put("back_url", getPayOutNotifyUrl());
        map.put("mch_transferId", orderNo);
        map.put("apply_date", DateUtils.getFormatNow(DateUtils.SIMPLE_DATEFORMAT));
        map.put("bank_code", bankCode);
        map.put("receive_name", name);
        map.put("receive_account", accountNumber);
        String sign = SignUtil.sortData(map);
        sign = SignAPI.sign(sign, getPayOutKey());
        map.put("sign_type", "MD5");
        map.put("sign", sign);
        String url = getBaseUrl() + "/pay/transfer";
        FormBody.Builder builder = new FormBody.Builder();
        map.forEach(builder::add);
        RequestBody body = builder.build();
        JSONObject jsonObject = HttpUtil.invokePostRequest(url, body).orElseThrow(() -> new RuntimeException("请求响应为空"));
        return jsonObject.toJavaObject(SeproCreatePayOutRes.class);
    }

    /**
     * 查询转账
     * @param orderNo
     * @return
     */
    public SeproCreatePayOutRes queryTransfer(String orderNo) {
        Map<String, String> map = new HashMap<>();
        map.put("mch_id", getMerchantId());
        map.put("mch_transferId", orderNo);
        String sign = SignUtil.sortData(map);
        sign = SignAPI.sign(sign, getPayOutKey());
        map.put("sign_type", "MD5");
        map.put("sign", sign);
        String url = getBaseUrl() + "/query/transfer";
        FormBody.Builder builder = new FormBody.Builder();
        map.forEach(builder::add);
        RequestBody body = builder.build();
        JSONObject jsonObject = HttpUtil.invokePostRequest(url, body).orElseThrow(() -> new RuntimeException("请求响应为空"));
        return jsonObject.toJavaObject(SeproCreatePayOutRes.class);
    }


}
