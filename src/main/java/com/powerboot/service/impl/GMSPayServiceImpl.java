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
import com.powerboot.utils.gms.constants.GMSConst;
import com.powerboot.utils.gms.core.GMSClient;
import com.powerboot.utils.gms.model.GmsCreatePayOutRes;
import com.powerboot.utils.gms.model.GmsCreatePayRes;
import com.powerboot.utils.grecash.contants.GrecashConst;
import com.powerboot.utils.grecash.core.GrecashClient;
import com.powerboot.utils.grecash.model.BaseGrecashRes;
import com.powerboot.utils.grecash.model.CreatePayOutRes;
import com.powerboot.utils.grecash.model.CreatePayRes;
import com.powerboot.utils.grecash.model.QueryPayRes;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service("GmsPay")
@Slf4j
public class GMSPayServiceImpl implements PaymentService {

    private static Logger logger = LoggerFactory.getLogger(GMSPayServiceImpl.class);

    @Autowired
    private GMSClient gmsClient;

    @Override
    public BaseResponse<PaymentResult> getPayInOrder(QueryPayInParam queryPayInParam) {
        log.info("getPayInfoOrder : {}", queryPayInParam);
//        PaymentResult paymentResult = new PaymentResult();
//        BaseGrecashRes res = grecashClient.queryPay(queryPayInParam.getThirdOrderNo());
//        log.info("getPayInfoOrder : jsonObject : {}", res);
//        if (null != res && GrecashConst.SUCCESS_CODE == res.getCode()) {
//            QueryPayRes queryPayRes = ((com.alibaba.fastjson.JSONObject) res.getPayorder()).toJavaObject(QueryPayRes.class);
//            paymentResult.setDescription(queryPayRes.getMessage());
//            paymentResult.setThirdOrderStatus(queryPayRes.getStatus() + "");
//            if (GrecashConst.PAY_STATUS_1 == queryPayRes.getStatus()) {
//                paymentResult.setStatus(PayEnums.PayStatusEnum.PAID.getCode());
//            } else if (GrecashConst.PAY_STATUS_0 == queryPayRes.getStatus()) {
//                paymentResult.setStatus(PayEnums.PayStatusEnum.PAYING.getCode());
//            } else {
//                paymentResult.setStatus(PayEnums.PayStatusEnum.FAIL.getCode());
//            }
//        }
        return BaseResponse.fail(null);
    }

    @Override
    public BaseResponse<PaymentResult> payIn(CreatePayInOrder createPayInOrder) {
        PayDO payDO = createPayInOrder.getPayDO();
        UserDO userDO = createPayInOrder.getUserDO();
        try {
            log.info("payIn : createPayInfoOrder:{}", createPayInOrder);
            PaymentResult paymentResult = new PaymentResult();
            GmsCreatePayRes res = gmsClient.createPay(payDO.getOrderNo(), payDO.getAmount(),
                    userDO.getBankCode());
            log.info("payIn third result : {}", res);
            if (null != res && GMSConst.SUCCESS_CODE.equalsIgnoreCase(res.getRespCode())) {
                paymentResult.setThirdPayUrl(res.getPayInfo());
                paymentResult.setThirdOrderNo(res.getOrderNo());
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
        GmsCreatePayOutRes res = gmsClient.queryTransfer(queryPayOutParam.getLocalOrderNo());
        log.info("getPayoutOrder : getPayoutOrder: post : {}", res);
        if (null != res && GMSConst.SUCCESS_CODE.equalsIgnoreCase(res.getRespCode())) {
            result.setThirdOrderAmount(new BigDecimal(res.getTransferAmount()));
            result.setDescription(res.getErrorMsg());
            result.setThirdOrderStatus(res.getTradeResult());
            if (GMSConst.TRADE_RES_0.equalsIgnoreCase(res.getTradeResult())
                    || GMSConst.TRADE_RES_4.equalsIgnoreCase(res.getTradeResult())) {
                result.setStatus(PayEnums.PayStatusEnum.PAYING.getCode());
            } else if (GMSConst.TRADE_RES_1.equalsIgnoreCase(res.getTradeResult())) {
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
        GmsCreatePayOutRes res = gmsClient.createTransfer(createPayOutOrder.getOrderNo(), createPayOutOrder.getAmount(),
                userDO.getFirstName(), userDO.getAccountNumber(), userDO.getBankCode());
        log.info("payout : orderNo:{}: createPayOutOrder: post : {}", createPayOutOrder.getOrderNo(), res);
        if (null != res && GMSConst.SUCCESS_CODE.equalsIgnoreCase(res.getRespCode())) {
            result.setDescription(res.getErrorMsg());
            result.setThirdOrderNo(res.getTradeNo());
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
