package com.powerboot.service.impl;

import com.powerboot.base.BaseResponse;
import com.powerboot.config.BaseException;
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

import java.util.List;

@Service("WallytPay")
@Slf4j
public class WallytPayServiceImpl implements PaymentService {

    private static Logger logger = LoggerFactory.getLogger(WallytPayServiceImpl.class);

    @Autowired
    private WallytClient wallytClient;

    @Override
    public BaseResponse<PaymentResult> getPayInOrder(QueryPayInParam queryPayInParam) {
        log.info("getPayInfoOrder : {}", queryPayInParam);

        return BaseResponse.fail();
    }

    @Override
    public BaseResponse<PaymentResult> getPayoutOrder(QueryPayOutParam queryPayOutParam) {
        log.info("getPayoutOrder : queryPayOutParam:{}", queryPayOutParam);
        PaymentResult result = new PaymentResult();
        WallytResponse<QueryTransferResponse> wallytResponse = wallytClient.queryTransfer(queryPayOutParam.getThirdOrderNo(), queryPayOutParam.getLocalOrderNo());
        log.info("getPayoutOrder : getPayoutOrder: post : {}", wallytResponse);
        if (doCheck(wallytResponse)) {
            result.setThirdOrderAmount(wallytResponse.getResponse().getTrxAmount());
            result.setDescription(wallytResponse.getResponse().getMessage());
            result.setThirdOrderStatus(wallytResponse.getResponse().getTrxStatus());
            if (WallytConst.TRANSFER_INIT.equalsIgnoreCase(result.getThirdOrderStatus())
                    || WallytConst.TRANSFER_PENDING.equalsIgnoreCase(result.getThirdOrderStatus()) ) {
                result.setStatus(PayEnums.PayStatusEnum.PAYING.getCode());
            } else if (WallytConst.TRANSFER_SUCCESS.equalsIgnoreCase(result.getThirdOrderStatus())) {
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
        WallytResponse<CreateTransferResponse> wallytResponse = wallytClient.createTransfer(createPayOutOrder.getOrderNo(), createPayOutOrder.getAmount(),
                "TaskP:" + createPayOutOrder.getOrderNo(), userDO.getFirstName(), userDO.getAccountNumber(), userDO.getBankName());
        log.info("payout : orderNo:{}: createPayOutOrder: post : {}", createPayOutOrder.getOrderNo(), wallytResponse);
        result.setDescription(wallytResponse.getResponse().getMessage());
        if (doCheck(wallytResponse)) {
            result.setThirdOrderNo(createPayOutOrder.getOrderNo() + "third");
            return BaseResponse.success(result);
        }
        return BaseResponse.fail(result.getDescription());
    }

    @Override
    public BaseResponse<PaymentResult> payIn(CreatePayInOrder createPayInOrder) {
        log.info("payIn : createPayInfoOrder:{}", createPayInOrder);
        return BaseResponse.fail();
    }

    @Override
    public List<WalletResult> wallet() {
        return null;
    }

    @Override
    public Boolean check(JSONObject post) {
        return false;
    }

    private Boolean doCheck(WallytResponse response) {
        Boolean check = false;
        if (WallytConst.CODE_SUCCESS.equalsIgnoreCase(response.getResponse().getCode())
                || WallytConst.CODE_PENDING.equalsIgnoreCase(response.getResponse().getCode())) {
            check = true;
        }
        return check;
    }
}
