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
import com.powerboot.utils.gms.constants.GMSConst;
import com.powerboot.utils.gms.core.GMSClient;
import com.powerboot.utils.gms.model.GmsCreatePayOutRes;
import com.powerboot.utils.gms.model.GmsCreatePayRes;
import com.powerboot.utils.grecash.model.CreatePayOutRes;
import com.powerboot.utils.qeapay.contants.QeaPayConst;
import com.powerboot.utils.qeapay.core.QeaPayClient;
import com.powerboot.utils.qeapay.domain.QeaPayCreatePayOutRes;
import com.powerboot.utils.qeapay.domain.QeaPayCreatePayRes;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service("qeaPay")
@Slf4j
public class QeaPayServiceImpl implements PaymentService {

    private static Logger logger = LoggerFactory.getLogger(QeaPayServiceImpl.class);

    @Autowired
    private QeaPayClient qeaPayClient;

    @Override
    public BaseResponse<PaymentResult> getPayInOrder(QueryPayInParam queryPayInParam) {
        log.info("getPayInfoOrder : {}", queryPayInParam);
        return BaseResponse.fail(null);
    }

    @Override
    public BaseResponse<PaymentResult> payIn(CreatePayInOrder createPayInOrder) {
        PayDO payDO = createPayInOrder.getPayDO();
        UserDO userDO = createPayInOrder.getUserDO();
        try {
            log.info("payIn : createPayInfoOrder:{}", createPayInOrder);
            PaymentResult paymentResult = new PaymentResult();
            QeaPayCreatePayRes res = qeaPayClient.createPay(payDO.getOrderNo(), payDO.getAmount(),
                    userDO.getBankCode());
            log.info("payIn third result : {}", res);
            if (null != res && QeaPayConst.SUCCESS_CODE.equalsIgnoreCase(res.getRespCode())) {
                paymentResult.setThirdPayUrl(res.getPayInfo());
                paymentResult.setThirdOrderNo(res.getOrderNo());
                paymentResult.setStatus(PayEnums.PayStatusEnum.PAYING.getCode());
            }
            return BaseResponse.success(paymentResult);
        }catch (Exception e){
            logger.error("@@@@@@  qea 支付异常 ",e);
            throw new BaseException(I18nEnum.PAYMENT_SYSTEM_FAIL.getMsg());
        }
    }


    @Override
    public BaseResponse<PaymentResult> getPayoutOrder(QueryPayOutParam queryPayOutParam) {
        log.info("getPayoutOrder : queryPayOutParam:{}", queryPayOutParam);
        PaymentResult result = new PaymentResult();
        QeaPayCreatePayOutRes res = qeaPayClient.queryTransfer(queryPayOutParam.getLocalOrderNo());
        log.info("getPayoutOrder : getPayoutOrder: post : {}", res);
        if (null != res && QeaPayConst.SUCCESS_CODE.equalsIgnoreCase(res.getRespCode())) {
            result.setThirdOrderAmount(new BigDecimal(res.getTransferAmount()));
            result.setDescription(res.getErrorMsg());
            result.setThirdOrderStatus(res.getTradeResult());
            if (QeaPayConst.TRADE_RES_0.equalsIgnoreCase(res.getTradeResult())
                    || QeaPayConst.TRADE_RES_4.equalsIgnoreCase(res.getTradeResult())) {
                result.setStatus(PayEnums.PayStatusEnum.PAYING.getCode());
            } else if (QeaPayConst.TRADE_RES_1.equalsIgnoreCase(res.getTradeResult())) {
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
        QeaPayCreatePayOutRes res = qeaPayClient.createTransfer(createPayOutOrder.getOrderNo(), createPayOutOrder.getAmount(),
                createPayOutOrder.getUserDO().getMobile(),
                userDO.getFirstName(), userDO.getAccountNumber(), userDO.getBankCode());
        log.info("payout : orderNo:{}: createPayOutOrder: post : {}", createPayOutOrder.getOrderNo(), res);
        if (null != res && QeaPayConst.SUCCESS_CODE.equalsIgnoreCase(res.getRespCode())) {
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
