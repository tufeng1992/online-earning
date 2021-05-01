package com.powerboot.utils.flutter.core;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.powerboot.consts.DictConsts;
import com.powerboot.utils.RedisUtils;
import com.powerboot.utils.opay.HttpUtil;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;

/**
 * flutter 支付类
 * @Date: 2021/4/13 16:37
 */
@Component
public class FlutterPayment {

    private Map<String, String> header = Maps.newHashMap();

    private String getSKey() {
        return RedisUtils.getValue(DictConsts.FLUTTER_SK, String.class);
    }

    private String getBaseUrl() {
        return RedisUtils.getValue(DictConsts.FLUTTER_GATEWAY_URL, String.class);
    }

    private String getRedirectUrl() {
        return RedisUtils.getValue(DictConsts.FLUTTER_REDIRECT_URL, String.class);
    }

    private String getCallbackUrl() {
        return RedisUtils.getValue(DictConsts.FLUTTER_CALLBACK_URL, String.class);
    }

    private String getCustomerTitle() {
        return RedisUtils.getValue(DictConsts.FLUTTER_CUSTOMER_TITLE, String.class);
    }

    private String getCustomerDes() {
        return RedisUtils.getValue(DictConsts.FLUTTER_CUSTOMER_DES, String.class);
    }

    private String getCustomerLogo() {
        return RedisUtils.getValue(DictConsts.FLUTTER_CUSTOMER_LOGO, String.class);
    }

    private Map<String, String> getHeader() {
        if (header.isEmpty()) {
            synchronized (this) {
                if (header.isEmpty()) {
                    header.put("Authorization", "Bearer " + getSKey());
                }
            }
        }
        return header;
    }

    /**
     * 创建卡号支付
     * @param reference
     * @param amount
     * @param email
     * @param phoneNumber
     * @param name
     * @return
     */
    public JSONObject createPayment(String reference, String amount, String email, String phoneNumber, String name) {
        JSONObject body = new JSONObject();
        body.put("tx_ref", reference);
        body.put("amount", amount);
        body.put("currency", "NGN");
        body.put("redirect_url", getRedirectUrl());
        body.put("payment_options", "card");
        JSONObject customer = new JSONObject();
        customer.put("email", email);
        customer.put("phonenumber", phoneNumber);
        customer.put("name", name);
        JSONObject customizations = new JSONObject();
        customizations.put("title", getCustomerTitle());
        customizations.put("description", getCustomerDes());
        customizations.put("logo", getCustomerLogo());
        body.put("customer", customer);
        body.put("customizations", customizations);
        String url = getBaseUrl() + "/v3/payments";
        return HttpUtil.post4JsonObj(url, getHeader(), body).orElseThrow(() -> new RuntimeException("请求响应为空"));
    }

    /**
     * 验证交易结果
     * @param transactionId
     * @return
     */
    public JSONObject verityPayment(String transactionId) {
        String url = getBaseUrl() + "/v3/transactions/" + transactionId + "/verify";
        return HttpUtil.get4JsonObj(url, null, getHeader()).orElseThrow(() -> new RuntimeException("请求响应为空"));
    }

    /**
     * 获取事务列表
     * @param from
     * @param to
     * @param reference
     * @param page
     * @return
     */
    public JSONObject getTransactions(String from, String to, String reference, Integer page) {
        String url = getBaseUrl() + "/v3/transactions";
        Map<String, String> param = Maps.newHashMap();
        param.put("tx_ref", reference);
        return HttpUtil.get4JsonObj(url, param, getHeader()).orElseThrow(() -> new RuntimeException("请求响应为空"));
    }

    /**
     * 创建转账
     * @param reference
     * @param amount
     * @param accountNumber
     * @param accountBank
     * @param narration
     * @return
     */
    public JSONObject createTransfer(String reference, String amount, String accountNumber, String accountBank, String narration) {
        JSONObject body = new JSONObject();
        body.put("account_bank", accountBank);
        body.put("account_number", accountNumber);
        body.put("amount", amount);
        body.put("narration", narration);
        body.put("currency", "NGN");
        body.put("reference", reference);
//        body.put("callback_url", getCallbackUrl());
        body.put("debit_currency", "NGN");
        String url = getBaseUrl() + "/v3/transfers";
        return HttpUtil.post4JsonObj(url, getHeader(), body).orElseThrow(() -> new RuntimeException("请求响应为空"));
    }

    /**
     * 验证转账结果
     * @param thirdOrderNo
     * @return
     */
    public JSONObject verityTransfer(String thirdOrderNo) {
        String url = getBaseUrl() + "/v3/transfers/" + thirdOrderNo;
        return HttpUtil.get4JsonObj(url, null, getHeader()).orElseThrow(() -> new RuntimeException("请求响应为空"));
    }



}
