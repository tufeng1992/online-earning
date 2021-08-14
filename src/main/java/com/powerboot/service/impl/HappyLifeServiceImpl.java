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
import com.powerboot.utils.grecash.contants.GrecashConst;
import com.powerboot.utils.grecash.core.GrecashClient;
import com.powerboot.utils.grecash.model.BaseGrecashRes;
import com.powerboot.utils.grecash.model.CreatePayOutRes;
import com.powerboot.utils.grecash.model.CreatePayRes;
import com.powerboot.utils.grecash.model.QueryPayRes;
import com.powerboot.utils.happylife.contants.HappyLifeConst;
import com.powerboot.utils.happylife.core.HappyLifeClient;
import com.powerboot.utils.happylife.model.HappyLifePayOutRes;
import com.powerboot.utils.happylife.model.HappyLifePayRes;
import com.powerboot.utils.happylife.model.HappyLifeQueryPayOutRes;
import com.powerboot.utils.happylife.model.HappyLifeQueryPayRes;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service("HappyLifePay")
@Slf4j
public class HappyLifeServiceImpl implements PaymentService {

    private static Logger logger = LoggerFactory.getLogger(HappyLifeServiceImpl.class);

    @Autowired
    private HappyLifeClient happyLifeClient;

    @Override
    public BaseResponse<PaymentResult> getPayInOrder(QueryPayInParam queryPayInParam) {
        log.info("getPayInfoOrder : {}", queryPayInParam);
        PaymentResult paymentResult = new PaymentResult();
        HappyLifeQueryPayRes res = happyLifeClient.queryPay(queryPayInParam.getLocalOrderNo());
        log.info("getPayInfoOrder : HappyLifeQueryPayRes : {}", res);
        if (null != res && HappyLifeConst.SUCCESS_CODE.equals(res.getCode())) {
            paymentResult.setDescription(res.getMsg());
            paymentResult.setThirdOrderStatus(res.getData().getStatus() + "");
            if (HappyLifeConst.PAY_STATUS_200.equals(res.getData().getStatus())) {
                paymentResult.setStatus(PayEnums.PayStatusEnum.PAID.getCode());
            } else if (HappyLifeConst.PAY_STATUS_0.equals(res.getData().getStatus())) {
                paymentResult.setStatus(PayEnums.PayStatusEnum.PAYING.getCode());
            } else {
                paymentResult.setStatus(PayEnums.PayStatusEnum.FAIL.getCode());
            }
        }
        return BaseResponse.success(paymentResult);
    }

    @Override
    public BaseResponse<PaymentResult> payIn(CreatePayInOrder createPayInOrder) {
        PayDO payDO = createPayInOrder.getPayDO();
        UserDO userDO = createPayInOrder.getUserDO();
        try {
            log.info("payIn : createPayInfoOrder:{}", createPayInOrder);
            PaymentResult paymentResult = new PaymentResult();
            HappyLifePayRes res = happyLifeClient.createPay(payDO.getOrderNo(), payDO.getAmount());
            log.info("payIn third result : {}", res);
            if (null != res && HappyLifeConst.SUCCESS_CODE.equals(res.getCode())) {
                paymentResult.setThirdPayUrl(res.getData());
                paymentResult.setStatus(PayEnums.PayStatusEnum.PAYING.getCode());
            }
            return BaseResponse.success(paymentResult);
        }catch (Exception e){
            logger.error("@@@@@@  happyLife 支付异常 ",e);
            throw new BaseException(I18nEnum.PAYMENT_SYSTEM_FAIL.getMsg());
        }
    }


    @Override
    public BaseResponse<PaymentResult> getPayoutOrder(QueryPayOutParam queryPayOutParam) {
        log.info("getPayoutOrder : queryPayOutParam:{}", queryPayOutParam);
        PaymentResult result = new PaymentResult();
        HappyLifeQueryPayOutRes res = happyLifeClient.queryTransfer(queryPayOutParam.getLocalOrderNo());
        log.info("getPayoutOrder : getPayoutOrder: post : {}", res);
        if (null != res  && HappyLifeConst.SUCCESS_CODE.equals(res.getCode())) {
            result.setThirdOrderAmount(res.getData().getTransfer_money());
            result.setDescription(res.getMsg());
            result.setThirdOrderStatus(res.getData().getStatus());
            if (HappyLifeConst.PAY_OUT_STATUS_200.equals(res.getData().getStatus())) {
                result.setStatus(PayEnums.PayStatusEnum.PAID.getCode());
            } else if (HappyLifeConst.PAY_OUT_STATUS_PENDING.equals(res.getData().getStatus())) {
                result.setStatus(PayEnums.PayStatusEnum.PAYING.getCode());
            } else {
                result.setStatus(PayEnums.PayStatusEnum.FAIL.getCode());
            }
            return BaseResponse.success(result);
        }
        return null;
    }

    @Override
    public BaseResponse<PaymentResult> payout(CreatePayOutOrder createPayOutOrder) {
        log.info("payout : orderNo:{}: createPayOutOrder:{}", createPayOutOrder.getOrderNo(), createPayOutOrder);
        UserDO userDO = createPayOutOrder.getUserDO();
        PaymentResult result = new PaymentResult();
        HappyLifePayOutRes res = happyLifeClient.createTransfer(createPayOutOrder.getOrderNo(), createPayOutOrder.getAmount(),
                userDO.getFirstName(), userDO.getAccountNumber(), userDO.getBankCode(), userDO.getId());
        log.info("payout : orderNo:{}: createPayOutOrder: post : {}", createPayOutOrder.getOrderNo(), res);
        if (null != res  && HappyLifeConst.SUCCESS_CODE.equals(res.getCode())) {
            result.setDescription(res.getMsg());
            result.setThirdOrderNo(res.getData().getPlatform_no());
            if (!HappyLifeConst.PAY_OUT_STATUS_PENDING.equals(res.getData().getStatus())) {
                result.setStatus(PayEnums.PayStatusEnum.FAIL.getCode());
            }
            return BaseResponse.success(result);
        }
        return BaseResponse.fail(result.getDescription());
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
