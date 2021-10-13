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
import com.powerboot.utils.happylife.contants.HappyLifeConst;
import com.powerboot.utils.happylife.core.HappyLifeClient;
import com.powerboot.utils.happylife.model.HappyLifePayOutRes;
import com.powerboot.utils.happylife.model.HappyLifePayRes;
import com.powerboot.utils.happylife.model.HappyLifeQueryPayOutRes;
import com.powerboot.utils.happylife.model.HappyLifeQueryPayRes;
import com.powerboot.utils.payful.core.PayfulClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("PayfulPay")
@Slf4j
public class PayfulServiceImpl implements PaymentService {

    private static Logger logger = LoggerFactory.getLogger(PayfulServiceImpl.class);

    @Autowired
    private PayfulClient payfulClient;

    @Override
    public BaseResponse<PaymentResult> getPayInOrder(QueryPayInParam queryPayInParam) {
        log.info("getPayInfoOrder : {}", queryPayInParam);
        return BaseResponse.success(null);
    }

    @Override
    public BaseResponse<PaymentResult> payIn(CreatePayInOrder createPayInOrder) {
        PayDO payDO = createPayInOrder.getPayDO();
        UserDO userDO = createPayInOrder.getUserDO();
        try {
            log.info("payIn : createPayInfoOrder:{}", createPayInOrder);
            PaymentResult paymentResult = new PaymentResult();
            String res = payfulClient.createPay(payDO.getOrderNo(), payDO.getAmount());
            log.info("payIn third result : {}", res);
            if (StringUtils.isNotBlank(res)) {
                paymentResult.setThirdPayUrl(res);
                paymentResult.setStatus(PayEnums.PayStatusEnum.PAYING.getCode());
            }
            return BaseResponse.success(paymentResult);
        }catch (Exception e){
            logger.error("@@@@@@  payful 支付异常 ",e);
            throw new BaseException(I18nEnum.PAYMENT_SYSTEM_FAIL.getMsg());
        }
    }


    @Override
    public BaseResponse<PaymentResult> getPayoutOrder(QueryPayOutParam queryPayOutParam) {
        log.info("getPayoutOrder : queryPayOutParam:{}", queryPayOutParam);
        return null;
    }

    @Override
    public BaseResponse<PaymentResult> payout(CreatePayOutOrder createPayOutOrder) {
        log.info("payout : orderNo:{}: createPayOutOrder:{}", createPayOutOrder.getOrderNo(), createPayOutOrder);
        return BaseResponse.fail(null);
    }

    @Override
    public BaseResponse<PaymentResult> payoutBatch(List<CreatePayOutOrder> createPayOutOrderList) {
        return null;
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
