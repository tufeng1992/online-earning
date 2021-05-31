package com.powerboot.service.impl;

import com.powerboot.base.BaseResponse;
import com.powerboot.config.BaseException;
import com.powerboot.consts.DictConsts;
import com.powerboot.consts.I18nEnum;
import com.powerboot.consts.TipConsts;
import com.powerboot.domain.PayDO;
import com.powerboot.domain.UserDO;
import com.powerboot.enums.PayEnums;
import com.powerboot.request.payment.CreatePayInOrder;
import com.powerboot.request.payment.CreatePayOutOrder;
import com.powerboot.request.payment.QueryPayInParam;
import com.powerboot.request.payment.QueryPayOutParam;
import com.powerboot.response.pay.PaymentResult;
import com.powerboot.response.pay.WalletResult;
import com.powerboot.service.PaymentService;
import com.powerboot.utils.DateUtils;
import com.powerboot.utils.MD5Util;
import com.powerboot.utils.RedisUtils;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service(value = "WeGamePay")
public class WeGamePayServiceImpl implements PaymentService {
    private static Logger logger = LoggerFactory.getLogger(WeGamePayServiceImpl.class);

    private String getBaseUrl() {
        String baseUrl = RedisUtils.getValue(DictConsts.WE_GAME_BASE_URL, String.class);
        if (StringUtils.isBlank(baseUrl)) {
            baseUrl = "https://api.aaa.com";
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


    @Override
    public BaseResponse<PaymentResult> getPayInOrder(QueryPayInParam queryPayInParam) {
        PaymentResult result = new PaymentResult();
        Map<String, String> requestParam = new HashMap<>();
        requestParam.put("mchid", getNumber());
        requestParam.put("out_trade_no", queryPayInParam.getLocalOrderNo());
        getSign(requestParam, "mchid|out_trade_no");
        String requestUrl = getBaseUrl() + "/Payment_Trade_Query.html";
        JSONObject post = post(requestUrl, requestParam);
        if (check(post)) {
            String thirdOrderNo = post.getJSONObject("data").getString("pay_orderid");
            String thirdOrderStatus = post.getJSONObject("data").getString("result_code");
            result.setThirdOrderNo(thirdOrderNo);
            result.setThirdOrderStatus(thirdOrderStatus);
            if ("SUCCESS".equals(result.getThirdOrderStatus())) {
                result.setStatus(PayEnums.PayStatusEnum.PAID.getCode());
            } else if ("FAIL".equals(result.getThirdOrderStatus())) {
                result.setStatus(PayEnums.PayStatusEnum.FAIL.getCode());
            } else {
                result.setStatus(PayEnums.PayStatusEnum.PAYING.getCode());
            }
            return BaseResponse.success(result);
        }
        return null;
    }

    @Override
    public BaseResponse<PaymentResult> getPayoutOrder(QueryPayOutParam queryPayOutParam) {
        PaymentResult result = new PaymentResult();
        Map<String, String> requestParam = new HashMap<>();
        requestParam.put("mchid", getNumber());
        requestParam.put("out_trade_no", queryPayOutParam.getLocalOrderNo());
        getSign(requestParam, "mchid|out_trade_no");
        String requestUrl = getBaseUrl() + "/Payout_Submit_Query.html";
        JSONObject post = post(requestUrl, requestParam);
        if (check(post)) {
            String thirdOrderNo = post.getJSONObject("data").getString("pay_orderid");
            String thirdOrderStatus = post.getJSONObject("data").getString("result_code");
            result.setThirdOrderNo(thirdOrderNo);
            result.setThirdOrderStatus(thirdOrderStatus);
            if ("SUCCESS".equals(result.getThirdOrderStatus())) {
                result.setStatus(PayEnums.PayStatusEnum.PAID.getCode());
            } else if ("FAIL".equals(result.getThirdOrderStatus())) {
                result.setStatus(PayEnums.PayStatusEnum.FAIL.getCode());
            } else {
                result.setStatus(PayEnums.PayStatusEnum.PAYING.getCode());
            }
            return BaseResponse.success(result);
        }
        return null;
    }

    @Override
    public BaseResponse<PaymentResult> payout(CreatePayOutOrder createPayOutOrder) {
        UserDO userDO = createPayOutOrder.getUserDO();
        PaymentResult result = new PaymentResult();
        Map<String, String> requestParam = new HashMap<>();
        requestParam.put("mchid", getNumber());
        requestParam.put("out_trade_no", createPayOutOrder.getOrderNo());
        requestParam.put("money", createPayOutOrder.getAmount().toString());
        requestParam.put("paymentModes", "IMPS");
        requestParam.put("beneficiaryIFSC", StringUtils.trim(userDO.getAccountIfsc()));
        requestParam.put("beneficiaryName", StringUtils.trim(userDO.getName()));
        requestParam.put("beneficiaryAccount", StringUtils.trim(userDO.getAccountNumber()));
        requestParam.put("beneficiaryEmail", StringUtils.trim(userDO.getEmail()));
        requestParam.put("beneficiaryContact", StringUtils.trim(userDO.getAccountPhone()));
        requestParam.put("beneficiaryAddress", StringUtils.trim(userDO.getAddress()));
        requestParam.put("notifyurl", StringUtils.trim(getPayoutURL()));
        getSign(requestParam, "mchid|out_trade_no|money|beneficiaryIFSC|beneficiaryName|beneficiaryAccount|beneficiaryVPA|beneficiaryPhoneNo|extends");
        String requestUrl = getBaseUrl() + "/Payout_Submit.html";
        JSONObject post = post(requestUrl, requestParam);
        if (check(post)) {
            result.setThirdOrderNo(post.getJSONObject("data").getString("transaction_id"));
            return BaseResponse.success(result);
        }
        return null;
    }

    @Override
    public BaseResponse<PaymentResult> payIn(CreatePayInOrder createPayInOrder) {
        PaymentResult result = new PaymentResult();
        PayDO payDO = createPayInOrder.getPayDO();
        UserDO userDO = createPayInOrder.getUserDO();
        Map<String, String> requestParam = new HashMap<>();
        requestParam.put("pay_memberid", getNumber());
        requestParam.put("out_trade_no", StringUtils.trim(payDO.getOrderNo()));
        requestParam.put("pay_applydate", DateUtils.getFormatNow(DateUtils.DATE_TIME_PATTERN));
        requestParam.put("pay_type", getPayType());
        requestParam.put("pay_notifyurl", getPayURL());
        requestParam.put("pay_amount", payDO.getAmount().toString());
        getSign(requestParam, "pay_memberid|out_trade_no|pay_applydate|pay_type|pay_notifyurl|pay_callbackurl|pay_amount");
        String requestUrl = getBaseUrl() + "/Payment_Submit.html";
        JSONObject post = post(requestUrl, requestParam);
        if (check(post)) {
            result.setThirdPayUrl(post.getJSONObject("data").getString("url"));
            return BaseResponse.success(result);
        }
        return null;
    }

    @Override
    public List<WalletResult> wallet() {
        Map<String, String> requestParam = new HashMap<>();
        requestParam.put("pay_memberid", getNumber());
        getSign(requestParam, "pay_memberid");
        String requestUrl = getBaseUrl() + "/Payment_Trade_QueryAccount.html";
        JSONObject post = post(requestUrl, requestParam);
        if (check(post)) {
//            return post.getJSONObject("data").toMap();
        }
        return null;
    }

    @Override
    public Boolean check(JSONObject post) {
        Boolean check = false;
        if (post == null) {
            throw new BaseException(I18nEnum.PAYMENT_SYSTEM_FAIL.getMsg());
        }
        String status = post.getString("status");
        if ("SUCCESS".equals(status)) {
            check = true;
        } else if ("FAIL".equals(status)) {
            throw new BaseException(I18nEnum.PAYMENT_SYSTEM_FAIL.getMsg());
        }
        return check;
    }

}
