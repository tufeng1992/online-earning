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
import com.powerboot.utils.StringUtils;
import com.powerboot.utils.flutter.constants.FlutterConts;
import com.powerboot.utils.flutter.core.FlutterPayment;
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
        if (StringUtils.isBlank(queryPayInParam.getThirdOrderNo())) {
            com.alibaba.fastjson.JSONObject jsonObject = flutterPayment.getTransactions(null, null, queryPayInParam.getLocalOrderNo(), null);
            log.info("getPayInfoOrder : jsonObject : {}", jsonObject);
            if (doCheck(jsonObject)) {
                com.alibaba.fastjson.JSONArray data = jsonObject.getJSONArray("data");
                if (null != data) {
                    data.forEach(d -> {
                        com.alibaba.fastjson.JSONObject obj = (com.alibaba.fastjson.JSONObject) d;
                        if (StringUtils.isNotBlank(paymentResult.getThirdOrderStatus())
                                && FlutterConts.PAY_STATUS_SUCCESS.equalsIgnoreCase(paymentResult.getThirdOrderStatus())) {
                            return;
                        }
                        paymentResult.setThirdOrderAmount(obj.getBigDecimal("amount"));
                        paymentResult.setDescription(jsonObject.getString("message"));
                        paymentResult.setThirdOrderStatus(obj.getString("status"));
                        if (FlutterConts.PAY_STATUS_SUCCESS.equalsIgnoreCase(paymentResult.getThirdOrderStatus())) {
                            paymentResult.setStatus(PayEnums.PayStatusEnum.PAID.getCode());
                        } else if (FlutterConts.PAY_STATUS_PENDING.equalsIgnoreCase(paymentResult.getThirdOrderStatus())) {
                            paymentResult.setStatus(PayEnums.PayStatusEnum.PAYING.getCode());
                        } else {
                            paymentResult.setStatus(PayEnums.PayStatusEnum.FAIL.getCode());
                        }
                    });
                }
            }
        } else {
            com.alibaba.fastjson.JSONObject jsonObject = flutterPayment.verityPayment(queryPayInParam.getThirdOrderNo());
            log.info("getPayInfoOrder : jsonObject : {}", jsonObject);
            if (doCheck(jsonObject)) {
                com.alibaba.fastjson.JSONObject data = jsonObject.getJSONObject("data");
                paymentResult.setThirdOrderAmount(data.getBigDecimal("amount"));
                paymentResult.setDescription(jsonObject.getString("message"));
                paymentResult.setThirdOrderStatus(data.getString("status"));
                if (FlutterConts.PAY_STATUS_SUCCESS.equalsIgnoreCase(paymentResult.getThirdOrderStatus())) {
                    paymentResult.setStatus(PayEnums.PayStatusEnum.PAID.getCode());
                } else if (FlutterConts.PAY_STATUS_PENDING.equalsIgnoreCase(paymentResult.getThirdOrderStatus())) {
                    paymentResult.setStatus(PayEnums.PayStatusEnum.PAYING.getCode());
                } else {
                    paymentResult.setStatus(PayEnums.PayStatusEnum.FAIL.getCode());
                }
            }
        }
        return BaseResponse.success(paymentResult);
    }

    @Override
    public BaseResponse<PaymentResult> getPayoutOrder(QueryPayOutParam queryPayOutParam) {
        log.info("getPayoutOrder : queryPayOutParam:{}", queryPayOutParam);
        PaymentResult result = new PaymentResult();
        com.alibaba.fastjson.JSONObject post = flutterPayment.verityTransfer(queryPayOutParam.getThirdOrderNo());
        log.info("getPayoutOrder : getPayoutOrder: post : {}", post);
        if (doCheck(post)) {
            com.alibaba.fastjson.JSONObject data = post.getJSONObject("data");
            result.setThirdOrderAmount(data.getBigDecimal("amount"));
            result.setDescription(data.getString("complete_message"));
            result.setThirdOrderStatus(data.getString("status"));
            if (FlutterConts.PAY_STATUS_SUCCESS.equalsIgnoreCase(result.getThirdOrderStatus())) {
                result.setStatus(PayEnums.PayStatusEnum.PAID.getCode());
            } else if (FlutterConts.PAY_STATUS_PENDING.equalsIgnoreCase(result.getThirdOrderStatus())) {
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
        log.info("payout : createPayOutOrder:{}", createPayOutOrder);
        UserDO userDO = createPayOutOrder.getUserDO();
        PaymentResult result = new PaymentResult();
        com.alibaba.fastjson.JSONObject post = flutterPayment.createTransfer(createPayOutOrder.getOrderNo(), createPayOutOrder.getAmount().toString(),
                userDO.getAccountNumber(), userDO.getName(), userDO.getBankCode(), "payout");
        log.info("payout : createPayOutOrder: post : {}", post);
        if (doCheck(post)) {
            com.alibaba.fastjson.JSONObject data = post.getJSONObject("data");
            if (FlutterConts.PAYOUT_STATUS_NEW.equalsIgnoreCase(data.getString("status"))) {
                result.setThirdOrderNo(data.getString("id"));
                return BaseResponse.success(result);
            }
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
            com.alibaba.fastjson.JSONObject jsonObject = flutterPayment.createPayment(payDO.getOrderNo(), payDO.getAmount().toString(),
                    userDO.getEmail(), userDO.getMobile(), userDO.getName());
            log.info("payIn third result : {}", jsonObject);
            if (doCheck(jsonObject)) {
                com.alibaba.fastjson.JSONObject data = jsonObject.getJSONObject("data");
                paymentResult.setThirdPayUrl(data.getString("link"));
                paymentResult.setStatus(PayEnums.PayStatusEnum.PAYING.getCode());
                paymentResult.setDescription(jsonObject.getString("message"));
            }
            return BaseResponse.success(paymentResult);
        } catch (Exception e) {
            logger.error("@@@@@@  flutter 支付异常 ",e);
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

    private Boolean doCheck(com.alibaba.fastjson.JSONObject jsonObject) {
        Boolean check = false;
        String status = jsonObject.getString("status");
        if ("success".equalsIgnoreCase(status)) {
            check = true;
        } else {
            throw new BaseException(I18nEnum.PAYMENT_SYSTEM_FAIL.getMsg());
        }
        return check;
    }
}
