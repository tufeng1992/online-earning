package com.powerboot.service.impl;

import com.powerboot.base.BaseResponse;
import com.powerboot.config.BaseException;
import com.powerboot.consts.I18nEnum;
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
import com.powerboot.utils.StringUtils;
import com.powerboot.utils.momo.constants.MoMoConst;
import com.powerboot.utils.theteller.core.ThetellerClient;
import com.powerboot.utils.theteller.model.ThetellerCreatePay;
import com.powerboot.utils.theteller.model.ThetellerCreatePayOut;
import com.powerboot.utils.theteller.model.ThetellerQueryPay;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("ThetellerPay")
@Slf4j
public class ThetellerPayServiceImpl implements PaymentService {

    private static Logger logger = LoggerFactory.getLogger(ThetellerPayServiceImpl.class);

    @Autowired
    private ThetellerClient thetellerClient;

    @Override
    public BaseResponse<PaymentResult> getPayInOrder(QueryPayInParam queryPayInParam) {
        log.info("getPayInfoOrder : {}", queryPayInParam);
        PaymentResult paymentResult = new PaymentResult();
        if (StringUtils.isNotBlank(queryPayInParam.getThirdOrderNo())) {
            ThetellerQueryPay thetellerQueryPay = thetellerClient.queryPay(queryPayInParam.getThirdOrderNo());
            paymentResult.setThirdOrderStatus(thetellerQueryPay.getStatus());
            log.info("getPayInfoOrder : thetellerQueryPay : {}", thetellerQueryPay);
            if ("success".equalsIgnoreCase(thetellerQueryPay.getStatus())) {
                paymentResult.setStatus(PayEnums.PayStatusEnum.PAID.getCode());
            } else if ("approved".equalsIgnoreCase(paymentResult.getThirdOrderStatus())) {
                paymentResult.setStatus(PayEnums.PayStatusEnum.PAYING.getCode());
            } else {
                paymentResult.setStatus(PayEnums.PayStatusEnum.FAIL.getCode());
            }
        }
        return BaseResponse.success(paymentResult);
    }

    @Override
    public BaseResponse<PaymentResult> getPayoutOrder(QueryPayOutParam queryPayOutParam) {
        log.info("getPayoutOrder : queryPayOutParam:{}", queryPayOutParam);
//        PaymentResult result = new PaymentResult();
//        Transfer transfer = moMoClient.queryTransfer(queryPayOutParam.getThirdOrderNo());
//        log.info("getPayoutOrder : getPayoutOrder: post : {}", transfer);
//        result.setThirdOrderAmount(new BigDecimal(transfer.getAmount()));
//        result.setDescription(transfer.getPayerMessage());
//        result.setThirdOrderStatus(transfer.getStatus());
//        if (MoMoConst.PAY_OUT_SUCCESSFUL.equalsIgnoreCase(result.getThirdOrderStatus())) {
//            result.setStatus(PayEnums.PayStatusEnum.PAID.getCode());
//        } else if (MoMoConst.PAY_OUT_PENDING.equalsIgnoreCase(result.getThirdOrderStatus())) {
//            result.setStatus(PayEnums.PayStatusEnum.PAYING.getCode());
//        } else {
//            result.setStatus(PayEnums.PayStatusEnum.FAIL.getCode());
//        }
        return null;//BaseResponse.success(result);
    }

    @Override
    public BaseResponse<PaymentResult> payout(CreatePayOutOrder createPayOutOrder) {
        log.info("payout : createPayOutOrder:{}", createPayOutOrder);
        UserDO userDO = createPayOutOrder.getUserDO();
        PaymentResult result = new PaymentResult();
        ThetellerCreatePayOut thetellerCreatePayOut = thetellerClient.createTransfer(createPayOutOrder.getOrderNo(),
                createPayOutOrder.getAmount(), userDO.getAccountNumber(), userDO.getBankCode());
        log.info("payout : createPayOutOrder: thetellerCreatePayOut : {}", thetellerCreatePayOut);
        if ("successful".equalsIgnoreCase(thetellerCreatePayOut.getStatus())) {
            result.setThirdOrderNo(thetellerCreatePayOut.getReference_id());
            return BaseResponse.success(result);
        }
        return null;
    }

    @Override
    public BaseResponse<PaymentResult> payoutBatch(List<CreatePayOutOrder> createPayOutOrderList) {
        return null;
    }

    @Override
    public BaseResponse<PaymentResult> payIn(CreatePayInOrder createPayInOrder) {
        PayDO payDO = createPayInOrder.getPayDO();
        UserDO userDO = createPayInOrder.getUserDO();
        try {
            log.info("payIn : createPayInfoOrder:{}", createPayInOrder);
            PaymentResult paymentResult = new PaymentResult();
            ThetellerCreatePay thetellerCreatePay = thetellerClient.createPay(payDO.getAmount(),
                    userDO.getEmail());
            log.info("payIn third result : {}", thetellerCreatePay);
            if ("success".equalsIgnoreCase(thetellerCreatePay.getStatus())) {
                paymentResult.setThirdPayUrl(thetellerCreatePay.getCheckoutUrl());
                paymentResult.setThirdOrderNo(thetellerCreatePay.getTransactionId());
                paymentResult.setStatus(PayEnums.PayStatusEnum.PAYING.getCode());
            }
            return BaseResponse.success(paymentResult);
        } catch (Exception e) {
            logger.error("@@@@@@  theteller 支付异常 ",e);
            throw new BaseException(I18nEnum.PAYMENT_SYSTEM_FAIL.getMsg());
        }
    }

    @Override
    public List<WalletResult> wallet() {
        return null;
    }

    @Override
    public Boolean check(JSONObject post) {
        return false;
    }
}
