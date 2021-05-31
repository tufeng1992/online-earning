package com.powerboot.service;

import com.powerboot.config.BaseException;
import com.powerboot.consts.DictConsts;
import com.powerboot.consts.I18nEnum;
import com.powerboot.consts.TipConsts;
import com.powerboot.domain.PayDO;
import com.powerboot.domain.UserDO;
import com.powerboot.utils.DateUtils;
import com.powerboot.utils.RedisUtils;
import com.powerboot.utils.MD5Util;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
public class WeGamePayService {
    private static Logger logger = LoggerFactory.getLogger(WeGamePayService.class);

    private String getBaseUrl() {
        String baseUrl = RedisUtils.getValue(DictConsts.WE_GAME_BASE_URL, String.class);
        if (StringUtils.isBlank(baseUrl)){
            baseUrl = "https://www.aaa.com";
        }
        return baseUrl;
    }

    private String getPayType() {
        return RedisUtils.getValue(DictConsts.WE_GAME_PAY_TYPE, String.class);
    }

    private String getPayURL() {
        return RedisUtils.getValue(DictConsts.WE_GAME_PAY_URL, String.class);
    }

    private String getPayoutURL() {
        return RedisUtils.getValue(DictConsts.WE_GAME_PAYOUT_URL, String.class);
    }

    private String getNumber() {
        return RedisUtils.getValue(DictConsts.WE_GAME_NUMBER, String.class);
    }

    private String getKey() {
        return RedisUtils.getValue(DictConsts.WE_GAME_API_KEY, String.class);
    }

    public String clean(String s) {
        s = s.trim();
        return s;
    }

    public boolean empty(String s) {
        if (s == null || s.trim().equals("")) {
            return true;
        } else {
            return false;
        }
    }


    public String getSign(Map<String, String> requestParam, String signQueue) {

        String sign;
        StringBuilder sb = new StringBuilder();
        List<String> signs = Arrays.asList(signQueue.split("\\|"));
        Collections.sort(signs);
        for (String key : signs) {
            if (StringUtils.isNotBlank(requestParam.get(key))) {
                if (sb.length() > 0) {
                    sb.append('&');
                }
                sb.append(key).append('=').append(requestParam.get(key));
            }
        }
        String stringSignTemp = sb.toString() + "&key=" + getKey();
        sign = MD5Util.stringToMD5(stringSignTemp).toUpperCase();
        requestParam.put("sign", sign);
        return sign;
    }

    private JSONObject post(String requestUrl, Map<String, String> requestParam) {
        try {
            List<String> keys = new ArrayList<>(requestParam.keySet());
            logger.info("requestURL : {}", requestUrl);
            logger.info("------------requestParam : {} ", requestParam);
            //发送post请求-----start
            PostMethod postMethod = new PostMethod(requestUrl);
            postMethod.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
            //参数设置，需要注意的就是里边不能传NULL，要传空字符串
            NameValuePair[] data = new NameValuePair[requestParam.size()];
            for (int i = 0; i < requestParam.size(); i++) {
                data[i] = new NameValuePair(keys.get(i), requestParam.get(keys.get(i)));
            }
            postMethod.setRequestBody(data);
            org.apache.commons.httpclient.HttpClient httpClient = new org.apache.commons.httpclient.HttpClient();
            httpClient.executeMethod(postMethod);
            String res = postMethod.getResponseBodyAsString();
            //发送post请求-----end
            logger.info("requestURL : {} ---------- response : {}", requestUrl, res);
            if (StringUtils.isBlank(res)) {
                return new JSONObject();
            }
            return new JSONObject(res);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    public JSONObject getPayChannel() {
        Map<String, String> requestParam = new HashMap<>();
        requestParam.put("pay_memberid", getNumber());
        requestParam.put("version", "2.0");
        getSign(requestParam, "pay_memberid|version");
        String requestUrl = getBaseUrl() + "/Pay_Channel.html";
        try {
            return post(requestUrl, requestParam);
        } catch (Exception e) {
            e.printStackTrace();
        }
        throw new BaseException("payment error!");
    }

    public String pay(PayDO payDO, UserDO userDO) {
        try {
            Map<String, String> requestParam = new HashMap<>();
            requestParam.put("pay_memberid", getNumber());
            requestParam.put("out_trade_no", StringUtils.trim(payDO.getOrderNo()));
            requestParam.put("pay_applydate", DateUtils.getFormatNow(DateUtils.DATE_TIME_PATTERN));
            requestParam.put("pay_type", getPayType());
            requestParam.put("pay_notifyurl", getPayURL());
            requestParam.put("pay_amount", payDO.getAmount().toString());
            getSign(requestParam,
                "pay_memberid|out_trade_no|pay_applydate|pay_type|pay_notifyurl|pay_callbackurl|pay_amount");
            //        String requestUrl = getBaseUrl() + "/Home_Index_getUrl.html";
            String requestUrl = getBaseUrl() + "/Payment_Submit.html";
            JSONObject post = post(requestUrl, requestParam);
            if (check(post)) {
                return post.getJSONObject("data").getString("url");
            }
            return "";
        }catch (Exception e){
            logger.error("@@@@@@  wegame 支付异常 ",e);
            throw new BaseException(I18nEnum.PAYMENT_SYSTEM_FAIL.getMsg());
        }
    }

    public JSONObject getOrder(String orderNo) {
        Map<String, String> requestParam = new HashMap<>();
        requestParam.put("pay_memberid", getNumber());
        requestParam.put("out_trade_no", orderNo);
        getSign(requestParam, "pay_memberid|out_trade_no");
//        String requestUrl = getBaseUrl() + "/Pay_trade_query.html";
        String requestUrl = getBaseUrl() + "/Payment_Trade_Query.html";
        JSONObject post = post(requestUrl, requestParam);
        if (check(post)) {
            return post;
        }
        return null;
    }

    public String payout(UserDO userDO, BigDecimal relAccount, String orderNo) {
        Map<String, String> requestParam = new HashMap<>();
        requestParam.put("mchid", getNumber());
        requestParam.put("out_trade_no", orderNo);
        requestParam.put("money", relAccount.toString());
        requestParam.put("paymentModes", "IMPS");
        requestParam.put("beneficiaryIFSC", StringUtils.trim(userDO.getAccountIfsc()));
        requestParam.put("beneficiaryName", StringUtils.trim(userDO.getName()));
        requestParam.put("beneficiaryAccount", StringUtils.trim(userDO.getAccountNumber()));
        requestParam.put("beneficiaryEmail", StringUtils.trim(userDO.getEmail()));
        requestParam.put("beneficiaryContact", StringUtils.trim(userDO.getAccountPhone()));
        requestParam.put("beneficiaryAddress", StringUtils.trim(userDO.getAddress()));
        requestParam.put("notifyurl", StringUtils.trim(getPayoutURL()));
//        requestParam.put("beneficiaryVPA", getNumber());
//        requestParam.put("beneficiaryPhoneNo", getNumber());
        getSign(requestParam, "mchid|out_trade_no|money|beneficiaryIFSC|beneficiaryName|beneficiaryAccount|beneficiaryVPA|beneficiaryPhoneNo|extends");
//        String requestUrl = getBaseUrl() + "/Payment_Payout_Add.html";
        String requestUrl = getBaseUrl() + "/Payout_Submit.html";
        JSONObject post = post(requestUrl, requestParam);
        if (check(post)) {
            return post.getJSONObject("data").getString("transaction_id");
        }
        return "";
    }

    public JSONObject getPayoutOrder(String orderNo) {
        Map<String, String> requestParam = new HashMap<>();
        requestParam.put("mchid", getNumber());
        requestParam.put("out_trade_no", orderNo);
        getSign(requestParam, "mchid|out_trade_no");
//        String requestUrl = getBaseUrl() + "/Payment_Payout_Query.html";
        String requestUrl = getBaseUrl() + "/Payout_Submit_Query.html";
        JSONObject post = post(requestUrl, requestParam);
        if (check(post)) {
            return post;
        }
        return null;
    }

    public Map<String, Object> wallet() {
        Map<String, String> requestParam = new HashMap<>();
        requestParam.put("pay_memberid", getNumber());
        getSign(requestParam, "pay_memberid");
        String requestUrl = getBaseUrl() + "/Payment_Trade_QueryAccount.html";
        JSONObject post = post(requestUrl, requestParam);
        if (check(post)) {
            return post.getJSONObject("data").toMap();
        }
        return null;
    }

    private Boolean check(JSONObject post) {
        Boolean check = false;

        String status = post.getString("status");
        if ("SUCCESS".equals(status)) {
            check = true;
        } else if ("FAIL".equals(status)) {
            throw new BaseException(I18nEnum.PAYMENT_SYSTEM_FAIL.getMsg());
        }
        return check;
    }
}
