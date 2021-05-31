package com.powerboot.utils.grecash.core;

import cn.hutool.core.codec.Base64;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.powerboot.consts.DictConsts;
import com.powerboot.utils.RedisUtils;
import com.powerboot.utils.grecash.model.*;
import com.powerboot.utils.opay.HttpUtil;
import com.powerboot.utils.wallyt.core.WallytRSAUtil;
import com.powerboot.utils.wallyt.domain.dto.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * grecash 支付调用
 */
@Component
public class GrecashClient {

    private String getBaseUrl() {
        return RedisUtils.getValue(DictConsts.GRECASH_BASE_URL, String.class);
    }

    private String getMerchantId() {
        return RedisUtils.getValue(DictConsts.GRECASH_MERCHANT_ID, String.class);
    }

    private String getAuthorization() {
        String appId = RedisUtils.getValue(DictConsts.GRECASH_APP_ID, String.class);
        String appSecret = RedisUtils.getValue(DictConsts.GRECASH_APP_SECRET, String.class);
        String key = appId + ":" + appSecret;
        return "Basic " + Base64.encode(key.getBytes());
    }

    private String getAccessToken() {
        String accessToken = RedisUtils.getString(DictConsts.GRECASH_ACCESS_TOKEN);
        if (StringUtils.isNotBlank(accessToken)) {
            return accessToken;
        }
        String url = getBaseUrl() + "/gpauth/oauth/token?grant_type=client_credentials";
        Map<String, String> header = Maps.newHashMap();
        header.put("Authorization", getAuthorization());
        JSONObject j = HttpUtil.post4JsonObj(url, header, new JSONObject()).orElseThrow(() -> new RuntimeException("请求响应为空"));
        GetAccessTokenRes res = j.toJavaObject(GetAccessTokenRes.class);
        //默认缓存一天
        RedisUtils.setValue(DictConsts.GRECASH_ACCESS_TOKEN, res.getAccess_token(), 86400);
        return res.getAccess_token();
    }

    private Map<String, String> getHeader() {
        Map<String, String> header = Maps.newHashMap();
        header.put("Authorization", "Bearer " + getAccessToken());
        header.put("Content-Type", "application/json");
        return header;
    }

    private String getPayNotifyUrl() {
        return RedisUtils.getValue(DictConsts.GRECASH_PAY_NOTIFY_URL, String.class);
    }

    private String getPayOutNotifyUrl() {
        return RedisUtils.getValue(DictConsts.GRECASH_PAY_NOTIFY_URL, String.class);
    }

    private String getPayRedirectUrl() {
        return RedisUtils.getValue(DictConsts.GRECASH_PAY_REDIRECT_URL, String.class);
    }

    /**
     * 创建支付
     * @param orderNo
     * @param amount
     * @param email
     * @param phone
     * @return
     */
    public BaseGrecashRes<CreatePayRes> createPay(String orderNo, BigDecimal amount, String email, String phone) {
        JSONObject body = new JSONObject();
        body.put("amount", amount);
        body.put("merchantID", getMerchantId());
        body.put("merchantOrderId", orderNo);
        body.put("merchantUserEmail", email);
        body.put("countryCode", "NG");
        body.put("currency", "NGN");
        body.put("merchantUserPhone", phone);
        body.put("redirectUrl", getPayRedirectUrl());
        String url = getBaseUrl() + "/core/api/payment/africa/prepay";
        BaseGrecashRes baseGrecashRes = HttpUtil.post4JsonObj(url, getHeader(), body)
                .orElseThrow(() -> new RuntimeException("请求响应为空")).toJavaObject(BaseGrecashRes.class);
        return baseGrecashRes;
    }

    /**
     * 查询支付信息
     * @param thirdOrderNo
     */
    public BaseGrecashRes<QueryPayRes> queryPay(String thirdOrderNo) {
        String url = getBaseUrl() + "/core/api/payment/payorder/" + thirdOrderNo;
        JSONObject j = HttpUtil.get4JsonObj(url, Maps.newHashMap(), getHeader()).orElseThrow(() -> new RuntimeException("请求响应为空"));
        BaseGrecashRes res = j.toJavaObject(BaseGrecashRes.class);
        return res;
    }


    /**
     * 创建转账
     * @param orderNo
     * @param amount
     * @param phone
     * @param email
     * @param name
     * @param accountNumber
     * @param bankCode
     * @return
     */
    public BaseGrecashRes<CreatePayOutRes> createTransfer(String orderNo, BigDecimal amount, String phone,
                                                                 String email, String name, String accountNumber, String bankCode) {
        JSONObject body = new JSONObject();
        body.put("countryCode", "NG");
        body.put("currency", "NGN");
        body.put("payType", "card");
        body.put("payoutId", orderNo);
        body.put("callBackUrl", getPayOutNotifyUrl());
        JSONObject detail = new JSONObject();
        detail.put("amount", amount);
        detail.put("phone", phone);
        detail.put("email", email);
        detail.put("payeeAccount", accountNumber);
        detail.put("payeeName", name);
        detail.put("ifsc", bankCode);
        JSONArray detailList = new JSONArray();
        detailList.add(detail);
        body.put("details", detailList);
        String url = getBaseUrl() + "/core/api/payment/payout";
        JSONObject j = HttpUtil.post4JsonObj(url, getHeader(), body).orElseThrow(() -> new RuntimeException("请求响应为空"));
        return j.toJavaObject(BaseGrecashRes.class);
    }

    /**
     * 查询转账
     * @param thirdOrderNo
     * @return
     */
    public BaseGrecashRes<CreatePayOutRes> queryTransfer(String thirdOrderNo) {
        JSONObject body = new JSONObject();
        String url = getBaseUrl() + "/core/api/payment/payoutorder/" + thirdOrderNo;
        JSONObject j = HttpUtil.post4JsonObj(url, getHeader(), body).orElseThrow(() -> new RuntimeException("请求响应为空"));
        return j.toJavaObject(BaseGrecashRes.class);
    }

}
