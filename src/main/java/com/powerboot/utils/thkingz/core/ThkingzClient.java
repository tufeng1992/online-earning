package com.powerboot.utils.thkingz.core;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.google.common.collect.Maps;
import com.powerboot.consts.DictConsts;
import com.powerboot.utils.DateUtils;
import com.powerboot.utils.RedisUtils;
import com.powerboot.utils.gms.core.SignAPI;
import com.powerboot.utils.gms.core.SignUtil;
import com.powerboot.utils.grecash.model.BaseGrecashRes;
import com.powerboot.utils.grecash.model.QueryPayRes;
import com.powerboot.utils.opay.HttpUtil;
import com.powerboot.utils.sepro.model.SeproCreatePayOutRes;
import com.powerboot.utils.sepro.model.SeproCreatePayRes;
import com.powerboot.utils.thkingz.model.ThkingzBaseRes;
import com.powerboot.utils.thkingz.model.ThkingzCreatePayOutRes;
import com.powerboot.utils.thkingz.model.ThkingzCreatePayRes;
import com.powerboot.utils.thkingz.model.ThkingzQueryPayRes;
import lombok.extern.slf4j.Slf4j;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * Thkingz 支付调用
 */
@Component
@Slf4j
public class ThkingzClient {

    private static final String THKINGZ_BANK_MAPPING = "THKINGZ_BANK_MAPPING";

    private static Map<String, String> bankInfo;

    private String getBaseUrl() {
        return RedisUtils.getValue(DictConsts.THKINGZ_BASE_URL, String.class);
    }

    private String getMerchantId() {
        return RedisUtils.getValue(DictConsts.THKINGZ_MERCHANT_ID, String.class);
    }

    private String getPayNotifyUrl() {
        return RedisUtils.getValue(DictConsts.THKINGZ_PAY_NOTIFY_URL, String.class);
    }

    private String getPayOutNotifyUrl() {
        return RedisUtils.getValue(DictConsts.THKINGZ_PAY_OUT_NOTIFY_URL, String.class);
    }

    private String getPayRedirectUrl() {
        return RedisUtils.getValue(DictConsts.THKINGZ_PAY_REDIRECT_URL, String.class);
    }

    private String getPayInKey() {
        return RedisUtils.getValue(DictConsts.THKINGZ_PAY_IN_KEY, String.class);
    }

    private String getPayErrorRedirectUrl() {
        return RedisUtils.getValue(DictConsts.THKINGZ_PAY_ERROR_REDIRECT_URL, String.class);
    }

    private String getDefaultPayType() {
        String type = RedisUtils.getValue(DictConsts.THKINGZ_DEFAULT_PAY_TYPE, String.class);
        return StringUtils.isNotBlank(type) ? type : "321";
    }

    private String getBankIdByCode(String bankCode) {
        if (null == bankInfo) {
            synchronized (this) {
                if (null == bankInfo) {
                    String bankMappingJson = RedisUtils.getValue(THKINGZ_BANK_MAPPING, String.class);
                    bankInfo = JSONObject.parseObject(bankMappingJson, HashMap.class);
                }
            }
        }
        return bankInfo.get(bankCode);
    }

    /**
     * 创建支付
     * @param orderNo
     * @param amount
     * @param userId
     * @return
     */
    public ThkingzBaseRes<ThkingzCreatePayRes> createPay(String orderNo, BigDecimal amount, String userId) {
        Map<String, String> map = new HashMap<>();
        map.put("amount", amount.setScale(2).toString());
        map.put("version", "v1.0");
        map.put("appid", getMerchantId());
        map.put("callback_url", getPayNotifyUrl());
        map.put("success_url", getPayRedirectUrl());
        map.put("error_url", getPayErrorRedirectUrl());
        map.put("out_trade_no", orderNo);
        map.put("pay_type", getDefaultPayType());
        map.put("return_type", "app");
        map.put("out_uid", userId);
        String sign = Sign.signForInspiry(map, getPayInKey());
        map.put("sign", sign);
        String url = getBaseUrl() + "/index/unifiedorder?format=json";
        log.info("thkingzCreatePay:{}", map.toString());
        FormBody.Builder builder = new FormBody.Builder();
        map.forEach(builder::add);
        RequestBody body = builder.build();
        JSONObject jsonObject = HttpUtil.invokePostRequest(url, body).orElseThrow(() -> new RuntimeException("请求响应为空"));
        return jsonObject.toJavaObject(ThkingzBaseRes.class);
    }

    /**
     * 查询支付信息
     * @param thirdOrderNo
     */
    public ThkingzBaseRes<ThkingzQueryPayRes> queryPay(String thirdOrderNo) {
        String url = getBaseUrl() + "/index/getorder";
        Map<String, String> map = new HashMap<>();
        map.put("out_trade_no", thirdOrderNo);
        map.put("appid", getMerchantId());
        String sign = Sign.signForInspiry(map, getPayInKey());
        map.put("sign", sign);
        FormBody.Builder builder = new FormBody.Builder();
        map.forEach(builder::add);
        RequestBody body = builder.build();
        JSONObject jsonObject = HttpUtil.invokePostRequest(url, body).orElseThrow(() -> new RuntimeException("请求响应为空"));
        ThkingzBaseRes res = jsonObject.toJavaObject(ThkingzBaseRes.class);
        return res;
    }


    /**
     * 创建转账
     * @param orderNo
     * @param amount
     * @param name
     * @param accountNumber
     * @param bankCode
     * @return
     */
    public ThkingzBaseRes<ThkingzCreatePayOutRes> createTransfer(String orderNo, BigDecimal amount,
                                                                 String name, String accountNumber, String bankCode) {
        Map<String, String> map = new HashMap<>();
        map.put("money", amount.setScale(2).toString());
        map.put("appid", getMerchantId());
        map.put("callback", getPayOutNotifyUrl());
        map.put("out_trade_no", orderNo);
        map.put("bank_id", getBankIdByCode(bankCode));
        map.put("name", name);
        map.put("account", accountNumber);
        map.put("bank_type", "2");
        map.put("remark", "Taskp");
        String sign = Sign.signForInspiry(map, getPayInKey());
        map.put("sign", sign);
        String url = getBaseUrl() + "/withdrawal/creatWithdrawal";
        FormBody.Builder builder = new FormBody.Builder();
        map.forEach(builder::add);
        RequestBody body = builder.build();
        JSONObject jsonObject = HttpUtil.invokePostRequest(url, body).orElseThrow(() -> new RuntimeException("请求响应为空"));
        return jsonObject.toJavaObject(ThkingzBaseRes.class);
    }

    /**
     * 查询转账
     * @param orderNo
     * @return
     */
    public ThkingzBaseRes<ThkingzCreatePayOutRes> queryTransfer(String orderNo) {
        Map<String, String> map = new HashMap<>();
        map.put("order_no", orderNo);
        map.put("appid", getMerchantId());
        String sign = Sign.signForInspiry(map, getPayInKey());
        map.put("sign", sign);

        String url = getBaseUrl() + "/withdrawal/queryorder";
        FormBody.Builder builder = new FormBody.Builder();
        map.forEach(builder::add);
        RequestBody body = builder.build();
        JSONObject jsonObject = HttpUtil.invokePostRequest(url, body).orElseThrow(() -> new RuntimeException("请求响应为空"));
        return jsonObject.toJavaObject(ThkingzBaseRes.class);
    }

}
