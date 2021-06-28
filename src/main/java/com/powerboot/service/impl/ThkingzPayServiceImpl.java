package com.powerboot.service.impl;

import com.alibaba.fastjson.JSONArray;
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
import com.powerboot.utils.grecash.model.BaseGrecashRes;
import com.powerboot.utils.grecash.model.CreatePayRes;
import com.powerboot.utils.grecash.model.QueryPayRes;
import com.powerboot.utils.sepro.constants.SeproConst;
import com.powerboot.utils.sepro.core.SeproClient;
import com.powerboot.utils.sepro.model.SeproCreatePayOutRes;
import com.powerboot.utils.sepro.model.SeproCreatePayRes;
import com.powerboot.utils.thkingz.core.ThkingzClient;
import com.powerboot.utils.thkingz.model.ThkingzBaseRes;
import com.powerboot.utils.thkingz.model.ThkingzCreatePayOutRes;
import com.powerboot.utils.thkingz.model.ThkingzCreatePayRes;
import com.powerboot.utils.thkingz.model.ThkingzQueryPayRes;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service("ThkingzPay")
@Slf4j
public class ThkingzPayServiceImpl implements PaymentService {

    private static Logger logger = LoggerFactory.getLogger(ThkingzPayServiceImpl.class);

    @Autowired
    private ThkingzClient thkingzClient;

    @Override
    public BaseResponse<PaymentResult> getPayInOrder(QueryPayInParam queryPayInParam) {
        log.info("getPayInfoOrder : {}", queryPayInParam);
        PaymentResult paymentResult = new PaymentResult();
        ThkingzBaseRes res = thkingzClient.queryPay(queryPayInParam.getLocalOrderNo());
        log.info("getPayInfoOrder : jsonObject : {}", res);
        if (null != res && "200".equalsIgnoreCase(res.getCode())) {
            JSONArray jsonArray = (JSONArray) res.getData();
            ThkingzQueryPayRes queryPayRes = ((com.alibaba.fastjson.JSONObject)jsonArray.get(0)).toJavaObject(ThkingzQueryPayRes.class);
            paymentResult.setDescription(res.getMsg());
            paymentResult.setThirdOrderStatus(queryPayRes.getStatus() + "");
            if (4 == queryPayRes.getStatus()) {
                paymentResult.setStatus(PayEnums.PayStatusEnum.PAID.getCode());
            } else if (2 == queryPayRes.getStatus()) {
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
            ThkingzBaseRes res = thkingzClient.createPay(payDO.getOrderNo(), payDO.getAmount(),
                    userDO.getId().toString());
            log.info("payIn third result : {}", res);
            if (null != res && "200".equalsIgnoreCase(res.getCode())) {
                ThkingzCreatePayRes data = ((com.alibaba.fastjson.JSONObject) res.getData()).toJavaObject(ThkingzCreatePayRes.class);
                paymentResult.setThirdPayUrl(res.getUrl());
                paymentResult.setThirdOrderNo(data.getOrder_no());
                paymentResult.setStatus(PayEnums.PayStatusEnum.PAYING.getCode());
            }
            return BaseResponse.success(paymentResult);
        }catch (Exception e){
            logger.error("@@@@@@  gms 支付异常 ",e);
            throw new BaseException(I18nEnum.PAYMENT_SYSTEM_FAIL.getMsg());
        }
    }


    @Override
    public BaseResponse<PaymentResult> getPayoutOrder(QueryPayOutParam queryPayOutParam) {
        log.info("getPayoutOrder : queryPayOutParam:{}", queryPayOutParam);
        PaymentResult result = new PaymentResult();
        ThkingzBaseRes res = thkingzClient.queryTransfer(queryPayOutParam.getLocalOrderNo());
        log.info("getPayoutOrder : getPayoutOrder: post : {}", res);
        if (null != res && "1".equalsIgnoreCase(res.getCode())) {
            ThkingzQueryPayRes data = ((com.alibaba.fastjson.JSONObject) res.getData()).toJavaObject(ThkingzQueryPayRes.class);
            result.setDescription(res.getMsg());
            result.setThirdOrderAmount(new BigDecimal(data.getAmount()));
            result.setThirdOrderStatus(data.getStatus() + "");
            if (2 == data.getStatus()) {
                result.setStatus(PayEnums.PayStatusEnum.PAYING.getCode());
            } else if (1 == data.getStatus()) {
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
        ThkingzBaseRes res = thkingzClient.createTransfer(createPayOutOrder.getOrderNo(), createPayOutOrder.getAmount(),
                userDO.getFirstName(), userDO.getAccountNumber(), userDO.getBankCode());
        log.info("payout : orderNo:{}: createPayOutOrder: post : {}", createPayOutOrder.getOrderNo(), res);
        if (null != res && "1".equalsIgnoreCase(res.getCode())) {
            ThkingzCreatePayOutRes data = ((com.alibaba.fastjson.JSONObject) res.getData()).toJavaObject(ThkingzCreatePayOutRes.class);
            result.setDescription(res.getMsg());
            result.setThirdOrderNo(data.getOrder_no());
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
