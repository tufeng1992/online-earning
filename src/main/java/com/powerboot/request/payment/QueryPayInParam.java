package com.powerboot.request.payment;

/**
 * Create  2021 - 01 - 08 11:24 上午
 */
public class QueryPayInParam {
    //本地订单号
    private String localOrderNo;
    //三方订单号
    private String ThirdOrderNo;

    public String getLocalOrderNo() {
        return localOrderNo;
    }

    public void setLocalOrderNo(String localOrderNo) {
        this.localOrderNo = localOrderNo;
    }

    public String getThirdOrderNo() {
        return ThirdOrderNo;
    }

    public void setThirdOrderNo(String thirdOrderNo) {
        ThirdOrderNo = thirdOrderNo;
    }
}
