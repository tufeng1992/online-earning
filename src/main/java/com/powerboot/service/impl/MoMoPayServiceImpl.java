package com.powerboot.service.impl;

import ci.bamba.regis.models.RequestToPay;
import ci.bamba.regis.models.Transfer;
import com.powerboot.base.BaseResponse;
import com.powerboot.config.BaseException;
import com.powerboot.consts.DictConsts;
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
import com.powerboot.utils.RedisUtils;
import com.powerboot.utils.StringUtils;
import com.powerboot.utils.flutter.constants.FlutterConts;
import com.powerboot.utils.flutter.core.FlutterPayment;
import com.powerboot.utils.momo.constants.MoMoConst;
import com.powerboot.utils.momo.core.MoMoClient;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service("MoMoPay")
@Slf4j
public class MoMoPayServiceImpl implements PaymentService {

    private static Logger logger = LoggerFactory.getLogger(MoMoPayServiceImpl.class);

    @Autowired
    private MoMoClient moMoClient;

    @Override
    public BaseResponse<PaymentResult> getPayInOrder(QueryPayInParam queryPayInParam) {
        log.info("getPayInfoOrder : {}", queryPayInParam);
        PaymentResult paymentResult = new PaymentResult();
        if (StringUtils.isNotBlank(queryPayInParam.getThirdOrderNo())) {
            RequestToPay requestToPay = moMoClient.queryPay(queryPayInParam.getThirdOrderNo());
            paymentResult.setThirdOrderStatus(requestToPay.getStatus());
            log.info("getPayInfoOrder : requestToPay : {}", requestToPay);
            if (MoMoConst.PAY_SUCCESSFUL.equalsIgnoreCase(paymentResult.getThirdOrderStatus())) {
                paymentResult.setStatus(PayEnums.PayStatusEnum.PAID.getCode());
            } else if (MoMoConst.PAY_PENDING.equalsIgnoreCase(paymentResult.getThirdOrderStatus())) {
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
        PaymentResult result = new PaymentResult();
        Transfer transfer = moMoClient.queryTransfer(queryPayOutParam.getThirdOrderNo());
        log.info("getPayoutOrder : getPayoutOrder: post : {}", transfer);
        result.setThirdOrderAmount(new BigDecimal(transfer.getAmount()));
        result.setDescription(transfer.getPayerMessage());
        result.setThirdOrderStatus(transfer.getStatus());
        if (MoMoConst.PAY_OUT_SUCCESSFUL.equalsIgnoreCase(result.getThirdOrderStatus())) {
            result.setStatus(PayEnums.PayStatusEnum.PAID.getCode());
        } else if (MoMoConst.PAY_OUT_PENDING.equalsIgnoreCase(result.getThirdOrderStatus())) {
            result.setStatus(PayEnums.PayStatusEnum.PAYING.getCode());
        } else {
            result.setStatus(PayEnums.PayStatusEnum.FAIL.getCode());
        }
        return BaseResponse.success(result);
    }

    @Override
    public BaseResponse<PaymentResult> payout(CreatePayOutOrder createPayOutOrder) {
        log.info("payout : createPayOutOrder:{}", createPayOutOrder);
        UserDO userDO = createPayOutOrder.getUserDO();
        PaymentResult result = new PaymentResult();
        String referenceId = moMoClient.createTransfer(createPayOutOrder.getOrderNo(), userDO.getMobile(),
                createPayOutOrder.getAmount());
        log.info("payout : createPayOutOrder: referenceId : {}", referenceId);
        if (StringUtils.isNotBlank(referenceId)) {
            result.setThirdOrderNo(referenceId);
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
            String referenceId = moMoClient.createPay(payDO.getOrderNo(), payDO.getAmount(),
                    userDO.getMobile());
            log.info("payIn third result : {}", referenceId);
            if (StringUtils.isNotBlank(referenceId)) {
                paymentResult.setThirdPayUrl(RedisUtils.getString(DictConsts.MOMO_COLLECTION_REDIRECT_URL));
                paymentResult.setThirdOrderNo(referenceId);
                paymentResult.setStatus(PayEnums.PayStatusEnum.PAYING.getCode());
            }
            return BaseResponse.success(paymentResult);
        } catch (Exception e) {
            logger.error("@@@@@@  momo 支付异常 ",e);
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
