package com.powerboot.request.order;

import java.io.Serializable;

/**
 * @create 2021-03-16 18:19
 */
public class ApplyRequest implements Serializable {
    private String orderNo;
    private String remark;

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
