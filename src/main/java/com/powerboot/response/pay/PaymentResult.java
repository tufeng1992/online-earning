package com.powerboot.response.pay;

import java.math.BigDecimal;

/**
 * Create  2021 - 01 - 08 11:33 上午
 */
public class PaymentResult {
    //支付状态转换值 1-待支付 2-已支付 3-支付失败
    private Integer status;
    //三方支付状态真实值
    private String ThirdOrderStatus;
    //三方订单号
    private String ThirdOrderNo;
    //三方支付链接
    private String ThirdPayUrl;
    //三方支付状态真实值
    private BigDecimal ThirdOrderAmount;
    //订单描述
    private String description;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getThirdOrderStatus() {
        return ThirdOrderStatus;
    }

    public void setThirdOrderStatus(String thirdOrderStatus) {
        ThirdOrderStatus = thirdOrderStatus;
    }

    public String getThirdOrderNo() {
        return ThirdOrderNo;
    }

    public void setThirdOrderNo(String thirdOrderNo) {
        ThirdOrderNo = thirdOrderNo;
    }

    public String getThirdPayUrl() {
        return ThirdPayUrl;
    }

    public void setThirdPayUrl(String thirdPayUrl) {
        ThirdPayUrl = thirdPayUrl;
    }

    public BigDecimal getThirdOrderAmount() {
        return ThirdOrderAmount;
    }

    public void setThirdOrderAmount(BigDecimal thirdOrderAmount) {
        ThirdOrderAmount = thirdOrderAmount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
