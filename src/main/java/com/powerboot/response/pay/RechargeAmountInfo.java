package com.powerboot.response.pay;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

@ApiModel("充值金额显示")
public class RechargeAmountInfo implements Serializable {
    @ApiModelProperty("金额展示文本")
    private String text;
    @ApiModelProperty("金额")
    private String amount;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }
}
