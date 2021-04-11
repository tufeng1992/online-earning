package com.powerboot.request.payment;

import com.powerboot.domain.PayDO;
import com.powerboot.domain.UserDO;

import java.math.BigDecimal;

/**
 * Create  2021 - 01 - 08 11:25 上午
 */
public class CreatePayOutOrder {
    //本地订单号
    private String orderNo;
    //支付金额
    private BigDecimal amount;
    //用户信息
    private UserDO userDO;

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }


    public UserDO getUserDO() {
        return userDO;
    }

    public void setUserDO(UserDO userDO) {
        this.userDO = userDO;
    }
}
