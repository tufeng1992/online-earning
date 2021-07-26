package com.powerboot.utils.qeapay.core;

import cn.hutool.core.codec.Base64;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.powerboot.consts.DictConsts;
import com.powerboot.request.payment.CreatePayOutOrder;
import com.powerboot.utils.DateUtils;
import com.powerboot.utils.RedisUtils;
import com.powerboot.utils.gms.core.SignUtil;
import com.powerboot.utils.gms.model.GmsCreatePayOutRes;
import com.powerboot.utils.grecash.model.*;
import com.powerboot.utils.opay.HttpUtil;
import com.powerboot.utils.qeapay.domain.QeaPayCreatePayOutRes;
import com.powerboot.utils.qeapay.domain.QeaPayCreatePayRes;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.*;

/**
 * qeapay 支付调用
 */
@Component
@Slf4j
public class QeaPayClient {

    private String getBaseUrl() {
        return RedisUtils.getValue(DictConsts.QEAPAY_BASE_URL, String.class);
    }

    private String getMerchantId() {
        return RedisUtils.getValue(DictConsts.QEAPAY_MERCHANT_ID, String.class);
    }

    private String getPayNotifyUrl() {
        return RedisUtils.getValue(DictConsts.QEAPAY_PAY_NOTIFY_URL, String.class);
    }

    private String getPayOutNotifyUrl() {
        return RedisUtils.getValue(DictConsts.QEAPAY_PAY_OUT_NOTIFY_URL, String.class);
    }

    private String getPayRedirectUrl() {
        return RedisUtils.getValue(DictConsts.QEAPAY_PAY_REDIRECT_URL, String.class);
    }

    private String getPayInKey() {
        return RedisUtils.getValue(DictConsts.QEAPAY_PAY_IN_KEY, String.class);
    }

    private String getPayOutKey() {
        return RedisUtils.getValue(DictConsts.QEAPAY_PAY_OUT_KEY, String.class);
    }

    /**
     * 创建支付
     * @param orderNo
     * @param amount
     * @param bankCode
     * @return
     */
    public QeaPayCreatePayRes createPay(String orderNo, BigDecimal amount, String bankCode) {
        //可选的
//        String bank_code = (String) request.getParameter("bank_code");//网银必填，其他一定不填 【商户后台下载银行编码】

        Map<String, String> params = new TreeMap<>();
        params.put("version", "1.0");
        params.put("goods_name", "Pt_wallet");
        params.put("mch_id", getMerchantId());
        params.put("mch_order_no", orderNo);
        params.put("notify_url", getPayNotifyUrl());
        params.put("order_date", DateUtils.format(new Date(), DateUtils.DATE_TIME_PATTERN));
        params.put("pay_type", "920");
        params.put("trade_amount", amount.toString());

        //可选的
        params.put("page_url", getPayRedirectUrl());
        /* params.put("mch_return_msg", mch_return_msg); */
        /* params.put("bank_code", bank_code); */


        String signInfo = SignUtil.sortData(params);
        String sign = SignAPI.sign(signInfo, getPayInKey()); // 签名   signInfo签名参数排序，  merchant_key商户私钥
        params.put("sign", sign);
        params.put("sign_type", "MD5");//不参与签名

        log.info("请求参数：" + params.toString());
        String result = HttpClientUtil.doPost(getBaseUrl() + "/pay/web", params, "utf-8");

        log.info("签名参数排序：" + signInfo.length() + " --> " + signInfo);
        log.info("sign值：" + sign.length() + " --> " + sign);

        log.info("result值：" + result);
        log.info("---------------------------------------------------------------------------------------------------------------------------------------------");
        return JSONObject.parseObject(result, QeaPayCreatePayRes.class);
    }


    /**
     * 创建转账
     * @param orderNo
     * @param amount
     * @param phone
     * @param name
     * @param accountNumber
     * @param bankCode
     * @return
     */
    public QeaPayCreatePayOutRes createTransfer(String orderNo, BigDecimal amount, String phone,
                                                                 String name, String accountNumber, String bankCode) {
        Map<String, String> reqMap = new HashMap<>();
        reqMap.put("apply_date", DateUtils.format(new Date(), DateUtils.DATE_TIME_PATTERN));
        reqMap.put("bank_code", bankCode);
        reqMap.put("mch_id", getMerchantId());
        reqMap.put("mch_transferId", orderNo);
        reqMap.put("receive_account", accountNumber);
        reqMap.put("receive_name", name);
        reqMap.put("receiver_telephone", phone);
        reqMap.put("transfer_amount", amount.toString());
        reqMap.put("back_url", getPayOutNotifyUrl());
        String signStr = SignUtil.sortData(reqMap);
        reqMap.put("sign_type", "MD5");

        String result = null;
        String reqUrl = getBaseUrl() + "/pay/transfer";
        String merchant_key = getPayOutKey();
        String sign = SignAPI.sign(signStr, merchant_key);
        reqMap.put("sign", sign);

        log.info("reqMap：" + reqMap.toString().length() + " --> " + reqMap.toString());
        log.info("签名参数排序：" + signStr.length() + " --> " + signStr);
        log.info("sign值：" + sign.length() + " --> " + sign);

        result = HttpClientUtil.doPost(reqUrl, reqMap, "utf-8");
        log.info("result值：" + result);
        return JSONObject.parseObject(result, QeaPayCreatePayOutRes.class);
    }


    /**
     * 查询转账
     * @param thirdOrderNo
     * @return
     */
    public QeaPayCreatePayOutRes queryTransfer(String thirdOrderNo) {
        String mch_id = getMerchantId();
        StringBuffer signSrc = new StringBuffer();
        signSrc.append("mch_id=").append(mch_id).append("&");
        signSrc.append("mch_transferId=").append(thirdOrderNo);
        Map<String, String> reqMap = new HashMap<>();
        reqMap.put("sign_type", "MD5");
        reqMap.put("mch_id", mch_id);
        reqMap.put("mch_transferId", thirdOrderNo);
        String signInfo = signSrc.toString();
        String result = null;
        String reqUrl = "";
        String merchant_key = "";

        String sign = SignAPI.sign(signInfo, merchant_key);
        reqMap.put("sign", sign);
        log.info("reqMap：" + reqMap.toString().length() + " --> " + reqMap.toString());
        log.info("签名参数排序：" + signInfo.length() + " --> " + signInfo);
        log.info("sign值：" + sign.length() + " --> " + sign);

        result = HttpClientUtil.doPost(reqUrl, reqMap, "utf-8");
        log.info("result值：" + result);
        return JSONObject.parseObject(result, QeaPayCreatePayOutRes.class);
    }

}
