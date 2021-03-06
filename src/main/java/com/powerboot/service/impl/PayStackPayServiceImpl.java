package com.powerboot.service.impl;

import com.powerboot.base.BaseResponse;
import com.powerboot.config.BaseException;
import com.powerboot.consts.CacheConsts;
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
import com.powerboot.utils.RedisUtils;
import com.powerboot.utils.StringUtils;
import com.powerboot.utils.opay.PayResult;
import com.powerboot.utils.paystack.constants.PayStackConts;
import com.powerboot.utils.paystack.core.PaystackInline;
import com.powerboot.utils.paystack.domain.dto.PayStackResponse;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service("PayStack")
@Slf4j
public class PayStackPayServiceImpl implements PaymentService {

    private static Logger logger = LoggerFactory.getLogger(PayStackPayServiceImpl.class);

    @Autowired
    private PaystackInline paystackInline;

    private String getPayCallBackURL() {
        return RedisUtils.getValue(DictConsts.PAY_STACK_PAY_CALL_BACK_URL, String.class);
    }

    @Override
    public BaseResponse<PaymentResult> getPayInOrder(QueryPayInParam queryPayInParam) {
        log.info("getPayInfoOrder : {}", queryPayInParam);
        PaymentResult paymentResult = new PaymentResult();
        String localOrderNo = queryPayInParam.getLocalOrderNo();
        JSONObject jsonObject = paystackInline.verifyTransactions(localOrderNo);
        paymentResult.setDescription(jsonObject.toString());
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
        log.info("getPayoutOrder : {}", queryPayOutParam);
        PaymentResult paymentResult = new PaymentResult();
        String localOrderNo = queryPayOutParam.getLocalOrderNo();
        JSONObject jsonObject = paystackInline.verifyTransfer(localOrderNo);
        log.info("getPayInfoOrder : jsonObject : {}", jsonObject);
        paymentResult.setDescription(jsonObject.toString());
        if (check(jsonObject)) {
            JSONObject data = jsonObject.getJSONObject("data");
            String status = data.getString("status");
            if ("success".equalsIgnoreCase(status)) {
                paymentResult.setStatus(PayEnums.PayStatusEnum.PAID.getCode());
            } else {
                paymentResult.setStatus(PayEnums.PayStatusEnum.FAIL.getCode());
                paymentResult.setDescription(data.getString("reason"));
            }
        }
        return BaseResponse.success(paymentResult);
    }

    @Override
    public BaseResponse<PaymentResult> payout(CreatePayOutOrder createPayOutOrder) {
        log.info("payout : {}", createPayOutOrder);
        UserDO userDO = createPayOutOrder.getUserDO();
        PaymentResult result = new PaymentResult();
        String recipient = getUserTransferRecipient(userDO);
        BigDecimal applyAmount = createPayOutOrder.getAmount().multiply(new BigDecimal("100"));
        JSONObject post = paystackInline.initiateTransfer(createPayOutOrder.getOrderNo(),
                applyAmount.intValue() + "", recipient);
        log.info("payout : jsonObject : {}", post);
        result.setDescription(post.toString());
        if (check(post)) {
            result.setThirdOrderNo(post.getJSONObject("data").getInt("id") + "");
            return BaseResponse.success(result);
        }
        return null;
    }

    @Override
    public BaseResponse<PaymentResult> payoutBatch(List<CreatePayOutOrder> createPayOutOrderList) {
        return null;
    }

    /**
     * ??????????????????????????????
     * @param userDO
     * @return
     */
    public String getUserTransferRecipient(UserDO userDO) {
        String key = String.format(CacheConsts.PAYSTACK_USER_RECIPIENT, userDO.getId());
        String recipient = RedisUtils.getString(key);
        if (StringUtils.isBlank(recipient)) {
            recipient = createTransferRecipient(userDO);
            RedisUtils.setValue(key, recipient);
        }
        return recipient;
    }

    /**
     * ??????????????????????????????
     * @param userDO
     * @return
     */
    public String createTransferRecipient(UserDO userDO) {
        log.info("createTransferRecipient : {}", userDO);
        JSONObject jsonObject = paystackInline.createTransferRecipient(userDO.getName(), userDO.getAccountNumber(), userDO.getBankCode());
        log.info("createTransferRecipient : jsonObject:{}", jsonObject);
        if (check(jsonObject)) {
            PayStackResponse payStackResponse = com.alibaba.fastjson.JSONObject.parseObject(jsonObject.toString(), PayStackResponse.class);
            com.alibaba.fastjson.JSONObject data = (com.alibaba.fastjson.JSONObject) payStackResponse.getData();
            return data.getString("recipient_code");
        }
        throw new BaseException("create transfer recipient error");
    }

    @Override
    public BaseResponse<PaymentResult> payIn(CreatePayInOrder createPayInOrder) {
        PayDO payDO = createPayInOrder.getPayDO();
        UserDO userDO = createPayInOrder.getUserDO();
        try {
            log.info("payIn : createPayInfoOrder:{}", createPayInOrder);
            PaymentResult paymentResult = new PaymentResult();
            BigDecimal applyAmount = payDO.getAmount().multiply(new BigDecimal("100"));
            JSONObject post = paystackInline.paystackStandard(payDO.getOrderNo(), applyAmount.intValue(),
                    userDO.getEmail(), "", getPayCallBackURL());
            log.info("payIn third result : {}", post);
            paymentResult.setDescription(post.toString());
            if (check(post)) {
                PayStackResponse payStackResponse = com.alibaba.fastjson.JSONObject.parseObject(post.toString(), PayStackResponse.class);
                com.alibaba.fastjson.JSONObject data = (com.alibaba.fastjson.JSONObject) payStackResponse.getData();
                paymentResult.setThirdPayUrl(data.getString("authorization_url"));
                paymentResult.setStatus(PayEnums.PayStatusEnum.PAYING.getCode());
                paymentResult.setDescription(payStackResponse.getMessage());
            }
            return BaseResponse.success(paymentResult);
        }catch (Exception e){
            logger.error("@@@@@@  paystack ???????????? ",e);
            throw new BaseException(I18nEnum.PAYMENT_SYSTEM_FAIL.getMsg());
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
            throw new BaseException(I18nEnum.PAYMENT_SYSTEM_FAIL.getMsg());
        }
        return check;
    }
}
