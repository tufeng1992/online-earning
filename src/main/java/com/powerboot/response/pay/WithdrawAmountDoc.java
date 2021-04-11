package com.powerboot.response.pay;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.math.BigDecimal;

@ApiModel("提现文案")
public class WithdrawAmountDoc implements Serializable {
    @ApiModelProperty(value = "提现输入框文案",example = "Enter 600 and above")
    private  String withdrawInputText;
    @ApiModelProperty(value = "提现输入框文案颜色",example = "0xff9D9D9D")
    private  String withdrawInputTextColor;
    @ApiModelProperty(value = "最低提现金额",example = "600")
    private BigDecimal minimumWithdrawAmount;
    @ApiModelProperty(value = "提示语",example = "hello")
    private String tips;

    /*public WithdrawAmountDoc(){
        Integer amount = 600;
        this.withdrawInputText = "Enter "+amount+" and above";
        this.withdrawInputTextColor = "0xff9D9D9D";
        this.minimumWithdrawAmount = new BigDecimal(amount);
        this.tips = "";
    }*/

    public String getWithdrawInputText() {
        return withdrawInputText;
    }

    public void setWithdrawInputText(String withdrawInputText) {
        this.withdrawInputText = withdrawInputText;
    }

    public String getWithdrawInputTextColor() {
        return withdrawInputTextColor;
    }

    public void setWithdrawInputTextColor(String withdrawInputTextColor) {
        this.withdrawInputTextColor = withdrawInputTextColor;
    }

    public BigDecimal getMinimumWithdrawAmount() {
        return minimumWithdrawAmount;
    }

    public void setMinimumWithdrawAmount(BigDecimal minimumWithdrawAmount) {
        this.minimumWithdrawAmount = minimumWithdrawAmount;
    }

    public String getTips() {
        return tips;
    }

    public void setTips(String tips) {
        this.tips = tips;
    }
}
