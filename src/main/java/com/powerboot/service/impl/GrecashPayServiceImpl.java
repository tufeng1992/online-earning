package com.powerboot.service.impl;

import com.powerboot.base.BaseResponse;
import com.powerboot.config.BaseException;
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
import com.powerboot.utils.grecash.contants.GrecashConst;
import com.powerboot.utils.grecash.core.GrecashClient;
import com.powerboot.utils.grecash.model.BaseGrecashRes;
import com.powerboot.utils.grecash.model.CreatePayOutRes;
import com.powerboot.utils.grecash.model.CreatePayRes;
import com.powerboot.utils.grecash.model.QueryPayRes;
import com.powerboot.utils.paystack.constants.PayStackConts;
import com.powerboot.utils.paystack.domain.dto.PayStackResponse;
import com.powerboot.utils.wallyt.constants.WallytConst;
import com.powerboot.utils.wallyt.core.WallytClient;
import com.powerboot.utils.wallyt.domain.dto.CreateTransferResponse;
import com.powerboot.utils.wallyt.domain.dto.QueryTransferResponse;
import com.powerboot.utils.wallyt.domain.dto.WallytResponse;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service("GrecashPay")
@Slf4j
public class GrecashPayServiceImpl implements PaymentService {

    private static Logger logger = LoggerFactory.getLogger(GrecashPayServiceImpl.class);

    @Autowired
    private GrecashClient grecashClient;

    @Override
    public BaseResponse<PaymentResult> getPayInOrder(QueryPayInParam queryPayInParam) {
        log.info("getPayInfoOrder : {}", queryPayInParam);
        PaymentResult paymentResult = new PaymentResult();
        BaseGrecashRes res = grecashClient.queryPay(queryPayInParam.getThirdOrderNo());
        log.info("getPayInfoOrder : jsonObject : {}", res);
        if (null != res && GrecashConst.SUCCESS_CODE == res.getCode()) {
            QueryPayRes queryPayRes = ((com.alibaba.fastjson.JSONObject) res.getPayorder()).toJavaObject(QueryPayRes.class);
            paymentResult.setDescription(queryPayRes.getMessage());
            paymentResult.setThirdOrderStatus(queryPayRes.getStatus() + "");
            if (GrecashConst.PAY_STATUS_1 == queryPayRes.getStatus()) {
                paymentResult.setStatus(PayEnums.PayStatusEnum.PAID.getCode());
            } else if (GrecashConst.PAY_STATUS_0 == queryPayRes.getStatus()) {
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
            BaseGrecashRes res = grecashClient.createPay(payDO.getOrderNo(), payDO.getAmount(),
                    userDO.getEmail(), userDO.getMobile(), userDO.getName());
            log.info("payIn third result : {}", res);
            if (null != res && "1000".equals(res.getErrorCode())) {
                CreatePayRes createPayRes = ((com.alibaba.fastjson.JSONObject) res.getPayorder()).toJavaObject(CreatePayRes.class);
                paymentResult.setThirdPayUrl(createPayRes.getCheckPageUrl());
                paymentResult.setThirdOrderNo(createPayRes.getId());
                paymentResult.setStatus(PayEnums.PayStatusEnum.PAYING.getCode());
            }
            return BaseResponse.success(paymentResult);
        }catch (Exception e){
            logger.error("@@@@@@  grecash 支付异常 ",e);
            throw new BaseException(I18nEnum.PAYMENT_SYSTEM_FAIL.getMsg());
        }
    }


    @Override
    public BaseResponse<PaymentResult> getPayoutOrder(QueryPayOutParam queryPayOutParam) {
        log.info("getPayoutOrder : queryPayOutParam:{}", queryPayOutParam);
        PaymentResult result = new PaymentResult();
        BaseGrecashRes res = grecashClient.queryTransfer(queryPayOutParam.getThirdOrderNo());
        log.info("getPayoutOrder : getPayoutOrder: post : {}", res);
        if (null != res && GrecashConst.SUCCESS_CODE == res.getCode()) {
            CreatePayOutRes createPayOutRes = ((com.alibaba.fastjson.JSONObject) res.getResult()).toJavaObject(CreatePayOutRes.class);
            result.setThirdOrderAmount(new BigDecimal(createPayOutRes.getAmount()));
            result.setDescription(res.getInfo());
            result.setThirdOrderStatus(createPayOutRes.getStatus() + "");
            if (GrecashConst.PAY_OUT_STATUS_0 == createPayOutRes.getStatus()
                    || GrecashConst.PAY_OUT_STATUS_3 == createPayOutRes.getStatus()
                    || GrecashConst.PAY_OUT_STATUS_4 == createPayOutRes.getStatus()) {
                result.setStatus(PayEnums.PayStatusEnum.PAYING.getCode());
            } else if (GrecashConst.PAY_OUT_STATUS_1 == createPayOutRes.getStatus()) {
                result.setStatus(PayEnums.PayStatusEnum.PAID.getCode());
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
        BaseGrecashRes res = grecashClient.createTransfer(createPayOutOrder.getOrderNo(), createPayOutOrder.getAmount(),
                userDO.getMobile(), userDO.getEmail(), userDO.getFirstName(), userDO.getAccountNumber(), userDO.getBankCode());
        log.info("payout : orderNo:{}: createPayOutOrder: post : {}", createPayOutOrder.getOrderNo(), res);
        if (null != res && GrecashConst.SUCCESS_CODE == res.getCode()) {
            result.setDescription(res.getInfo());
            CreatePayOutRes createPayOutRes = ((com.alibaba.fastjson.JSONObject) res.getResult()).toJavaObject(CreatePayOutRes.class);
            result.setThirdOrderNo(createPayOutRes.getId());
            if (GrecashConst.PAY_OUT_STATUS_0 != createPayOutRes.getStatus()
                    && GrecashConst.PAY_OUT_STATUS_1 != createPayOutRes.getStatus()
                    && GrecashConst.PAY_OUT_STATUS_3 != createPayOutRes.getStatus()
                    && GrecashConst.PAY_OUT_STATUS_4 != createPayOutRes.getStatus()) {
               result.setStatus(PayEnums.PayStatusEnum.FAIL.getCode());
            }
            return BaseResponse.success(result);
        }
        return BaseResponse.fail(result.getDescription());
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
