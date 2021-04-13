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
import com.powerboot.utils.RedisUtils;
import com.powerboot.utils.flutter.core.FlutterPayment;
import com.powerboot.utils.paystack.constants.PayStackConts;
import com.powerboot.utils.paystack.core.PaystackInline;
import com.powerboot.utils.paystack.domain.dto.PayStackResponse;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("FlutterWave")
@Slf4j
public class FlutterPayServiceImpl implements PaymentService {

    private static Logger logger = LoggerFactory.getLogger(FlutterPayServiceImpl.class);

    @Autowired
    private FlutterPayment flutterPayment;

    @Override
    public BaseResponse<PaymentResult> getPayInOrder(QueryPayInParam queryPayInParam) {
        log.info("getPayInfoOrder : {}", queryPayInParam);
        PaymentResult paymentResult = new PaymentResult();
        String localOrderNo = queryPayInParam.getLocalOrderNo();
        JSONObject jsonObject = null;//paystackInline.verifyTransactions(localOrderNo);
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
        return null;
    }

    @Override
    public BaseResponse<PaymentResult> payout(CreatePayOutOrder createPayOutOrder) {
        UserDO userDO = createPayOutOrder.getUserDO();
        PaymentResult result = new PaymentResult();
        String recipient = getUserTransferRecipient(userDO);
        JSONObject post = paystackInline.initiateTransfer(createPayOutOrder.getOrderNo(),
                createPayOutOrder.getAmount().toString(), "");
        if (check(post)) {
            result.setThirdOrderNo(post.getJSONObject("data").getString("transaction_id"));
            return BaseResponse.success(result);
        }
        return null;
    }

    /**
     * 获取用户转账接收代码
     * @param userDO
     * @return
     */
    public String getUserTransferRecipient(UserDO userDO) {

        return null;
    }

    public String createTransferRecipient(UserDO userDO) {
        JSONObject jsonObject = paystackInline.createTransferRecipient(userDO.getName(), userDO.getAccountNumber(), "032");
        if (check(jsonObject)) {

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
            JSONObject post = paystackInline.paystackStandard(payDO.getOrderNo(), payDO.getAmount().intValue(),
                    userDO.getEmail(), "", getPayCallBackURL());
            log.info("payIn third result : {}", post);
            if (check(post)) {
                PayStackResponse payStackResponse = com.alibaba.fastjson.JSONObject.parseObject(post.toString(), PayStackResponse.class);
                JSONObject data = (JSONObject) payStackResponse.getData();
                paymentResult.setThirdPayUrl(data.getString("authorization_url"));
                paymentResult.setStatus(PayEnums.PayStatusEnum.PAYING.getCode());
                paymentResult.setDescription(payStackResponse.getMessage());
            }
            return BaseResponse.success(paymentResult);
        }catch (Exception e){
            logger.error("@@@@@@  wegame 支付异常 ",e);
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
