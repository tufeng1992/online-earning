package com.powerboot.utils.gms.core;

import cn.hutool.core.codec.Base64;
import cn.hutool.crypto.digest.MD5;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.powerboot.consts.DictConsts;
import com.powerboot.utils.DateUtils;
import com.powerboot.utils.MD5Util;
import com.powerboot.utils.RedisUtils;
import com.powerboot.utils.gms.model.GmsCreatePayOutRes;
import com.powerboot.utils.gms.model.GmsCreatePayRes;
import com.powerboot.utils.grecash.model.*;
import com.powerboot.utils.opay.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * gms 支付调用
 */
@Component
@Slf4j
public class GMSClient {

    private String getBaseUrl() {
        return RedisUtils.getValue(DictConsts.GMS_BASE_URL, String.class);
    }

    private String getMerchantId() {
        return RedisUtils.getValue(DictConsts.GMS_MERCHANT_ID, String.class);
    }

    private String getPayNotifyUrl() {
        return RedisUtils.getValue(DictConsts.GMS_PAY_NOTIFY_URL, String.class);
    }

    private String getPayOutNotifyUrl() {
        return RedisUtils.getValue(DictConsts.GMS_PAY_OUT_NOTIFY_URL, String.class);
    }

    private String getPayRedirectUrl() {
        return RedisUtils.getValue(DictConsts.GMS_PAY_REDIRECT_URL, String.class);
    }

    private String getPayInKey() {
        return RedisUtils.getValue(DictConsts.GMS_PAY_IN_KEY, String.class);
    }

    private String getPayOutKey() {
        return RedisUtils.getValue(DictConsts.GMS_PAY_OUT_KEY, String.class);
    }

    private String getDefaultPayType() {
        String type = RedisUtils.getValue(DictConsts.GMS_DEFAULT_PAY_TYPE, String.class);
        return StringUtils.isNotBlank(type) ? type : "321";
    }

    /**
     * 创建支付
     * @param orderNo
     * @param amount
     * @param bankCode
     * @return
     */
    public GmsCreatePayRes createPay(String orderNo, BigDecimal amount, String bankCode) {
        Map<String, String> map = new HashMap<>();
        map.put("trade_amount", amount.toString());
        map.put("version", "1.0");
        map.put("mch_id", getMerchantId());
        map.put("notify_url", getPayNotifyUrl());
        map.put("page_url", getPayRedirectUrl());
        map.put("mch_order_no", orderNo);
        map.put("pay_type", getDefaultPayType());
        map.put("order_date", DateUtils.getFormatNow(DateUtils.SIMPLE_DATEFORMAT));
        map.put("bank_code", bankCode);
        map.put("goods_name", "taskp order");
        String sign = SignUtil.sortData(map);
        sign = SignAPI.sign(sign, getPayInKey());
        map.put("sign_type", "MD5");
        map.put("sign", sign);
        String url = getBaseUrl() + "/pay/web";
        log.info("GmsCreatePay:{}", map.toString());
        FormBody.Builder builder = new FormBody.Builder();
        map.forEach(builder::add);
        RequestBody body = builder.build();
        JSONObject jsonObject = HttpUtil.invokePostRequest(url, body).orElseThrow(() -> new RuntimeException("请求响应为空"));
        return jsonObject.toJavaObject(GmsCreatePayRes.class);
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
    public GmsCreatePayOutRes createTransfer(String orderNo, BigDecimal amount,
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
        return jsonObject.toJavaObject(GmsCreatePayOutRes.class);
    }

    /**
     * 查询转账
     * @param orderNo
     * @return
     */
    public GmsCreatePayOutRes queryTransfer(String orderNo) {
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
        return jsonObject.toJavaObject(GmsCreatePayOutRes.class);
    }

}
