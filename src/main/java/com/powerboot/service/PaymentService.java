package com.powerboot.service;

import com.powerboot.base.BaseResponse;
import com.powerboot.request.payment.CreatePayInOrder;
import com.powerboot.request.payment.CreatePayOutOrder;
import com.powerboot.request.payment.QueryPayInParam;
import com.powerboot.request.payment.QueryPayOutParam;
import com.powerboot.response.pay.PaymentResult;
import com.powerboot.response.pay.WalletResult;
import org.json.JSONObject;

import java.util.List;


public interface PaymentService {
    /**
     * 查看四方充值订单状态
     * @param queryPayInParam
     * @return
     */
    public BaseResponse<PaymentResult> getPayInOrder(QueryPayInParam queryPayInParam);

    /**
     * 查询 提现 订单状态
     * @param queryPayOutParam
     * @return
     */
    public BaseResponse<PaymentResult> getPayoutOrder(QueryPayOutParam queryPayOutParam);

    /**
     * 提现
     * @param createPayOutOrder
     * @return
     */
    public BaseResponse<PaymentResult> payout(CreatePayOutOrder createPayOutOrder);

    /**
     * 充值/购买VIP
     * @param createPayInOrder
     * @return
     */
    public BaseResponse<PaymentResult> payIn(CreatePayInOrder createPayInOrder);

    /**
     * 获取三方钱包余额
     * @return
     */
    public List<WalletResult> wallet();

    /**
     * 三方返回结果校验
     * @param post
     * @return
     */
    public Boolean check(JSONObject post);
}
