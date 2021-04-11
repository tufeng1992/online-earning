package com.powerboot.request.payment;

import com.powerboot.domain.PayDO;
import com.powerboot.domain.UserDO;

/**
 * Create  2021 - 01 - 08 11:24 上午
 */
public class CreatePayInOrder {
    //订单信息
    private PayDO payDO;
    //用户信息
    private UserDO userDO;
    //支付凭证
    private String refNo;

    public PayDO getPayDO() {
        return payDO;
    }

    public void setPayDO(PayDO payDO) {
        this.payDO = payDO;
    }

    public UserDO getUserDO() {
        return userDO;
    }

    public void setUserDO(UserDO userDO) {
        this.userDO = userDO;
    }

    public String getRefNo() {
        return refNo;
    }

    public void setRefNo(String refNo) {
        this.refNo = refNo;
    }
}
