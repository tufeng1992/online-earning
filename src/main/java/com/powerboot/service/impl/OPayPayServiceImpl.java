package com.powerboot.service.impl;

import com.powerboot.base.BaseResponse;
import com.powerboot.config.BaseException;
import com.powerboot.consts.DictConsts;
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
import com.powerboot.utils.opay.OPayClient;
import com.powerboot.utils.opay.PayResult;
import com.powerboot.utils.paystack.constants.PayStackConts;
import com.powerboot.utils.paystack.core.PaystackInline;
import com.powerboot.utils.paystack.domain.dto.PayStackResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service(value = "OPay")
@Slf4j
public class OPayPayServiceImpl implements PaymentService {

    @Autowired
    private OPayClient oPayClient;

    private String getPayCallBackURL() {
        return RedisUtils.getValue(DictConsts.OPAY_PAY_CALL_BACK_URL, String.class);
    }

    @Override
    public BaseResponse<PaymentResult> getPayInOrder(QueryPayInParam queryPayInParam) {
        log.info("getPayInfoOrder : {}", queryPayInParam);
        PaymentResult paymentResult = new PaymentResult();
        String localOrderNo = queryPayInParam.getLocalOrderNo();
        PaystackInline p = new PaystackInline();
        JSONObject jsonObject = p.verifyTransactions(localOrderNo);
        log.info("getPayInfoOrder : jsonObject : {}", jsonObject);
        if (check(jsonObject)) {
            JSONObject data = jsonObject.getJSONObject("data");
            paymentResult.setThirdOrderAmount(data.getBigDecimal("amount"));
            paymentResult.setDescription(jsonObject.getString("message"));
            paymentResult.setThirdOrderStatus(data.getString("status"));
            if (PayStackConts.PAY_STATUS_SUCCESS.equalsIgnoreCase(paymentResult.getThirdOrderStatus())) {
                paymentResult.setStatus(PayEnums.PayStatusEnum.PAID.getCode());
            } else if (PayStackConts.PAY_STATUS_PENDING.equalsIgnoreCase(paymentResult.getThirdOrderStatus())) {
                paymentResult.setStatus(PayEnums.PayStatusEnum.PAYING.getCode());
            } else if (PayStackConts.PAY_STATUS_ABANDONED.equalsIgnoreCase(paymentResult.getThirdOrderStatus())){
                paymentResult.setStatus(PayEnums.PayStatusEnum.TIMEOUT.getCode());
            } else {
                paymentResult.setStatus(PayEnums.PayStatusEnum.FAIL.getCode());
            }
        }
        return BaseResponse.success(paymentResult);
    }

    @Override
    public BaseResponse<PaymentResult> getPayoutOrder(QueryPayOutParam queryPayOutParam) {
        PaymentResult result = new PaymentResult();
        com.alibaba.fastjson.JSONObject post = oPayClient.getTransferToBankStatus(queryPayOutParam.getLocalOrderNo(), queryPayOutParam.getThirdOrderNo());
        String status = post.getString("status");
        if (PayResult.INITIAL.name().equalsIgnoreCase(status) || PayResult.PENDING.name().equalsIgnoreCase(status)) {
            result.setStatus(PayEnums.PayStatusEnum.PAYING.getCode());
        } else if ("SUCCESSFUL".equalsIgnoreCase(status)) {
            result.setStatus(PayEnums.PayStatusEnum.PAID.getCode());
        } else {
            result.setStatus(PayEnums.PayStatusEnum.FAIL.getCode());
            result.setDescription(post.getString("failureReason"));
        }
        return BaseResponse.success(result);
    }

    @Override
    public BaseResponse<PaymentResult> payout(CreatePayOutOrder createPayOutOrder) {
        log.info("payout : {}", createPayOutOrder);
        UserDO userDO = createPayOutOrder.getUserDO();
        PaymentResult result = new PaymentResult();
        com.alibaba.fastjson.JSONObject post = oPayClient.transferToBank(createPayOutOrder.getOrderNo(), createPayOutOrder.getAmount().toString(),
                "payout", userDO.getName(), userDO.getBankCode(), userDO.getAccountNumber());
        log.info("payout : post : {}", post);
        if (PayResult.INITIAL.name().equalsIgnoreCase(post.getString("status"))) {
            result.setThirdOrderNo(post.getString("orderNo"));
            return BaseResponse.success(result);
        }
        return null;
    }

    @Override
    public BaseResponse<PaymentResult> payIn(CreatePayInOrder createPayInOrder) {
        PayDO payDO = createPayInOrder.getPayDO();
        UserDO userDO = createPayInOrder.getUserDO();
        //TODO
        try {
            return BaseResponse.fail();
        }catch (Exception e){
            log.error("@@@@@@  wegame 支付异常 ",e);
            throw new BaseException("The payment system is being upgraded, please wait for 1 hour");
        }
    }

    @Override
    public List<WalletResult> wallet() {
        return null;
    }

    @Override
    public Boolean check(JSONObject post) {
        Boolean check = false;
        Boolean status = post.getBoolean("status");
        if (status) {
            check = true;
        } else {
            throw new BaseException("The payment system is being upgraded, please wait for 1 hour");
        }
        return check;
    }
}
