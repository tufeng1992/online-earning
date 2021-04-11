package com.powerboot.response.pay;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@ApiModel("充值文案")
public class RechargeAmountDoc implements Serializable {
    @ApiModelProperty(value = "充值金额",example = "500")
    private  List<Integer> rechargeAmount = new ArrayList<>();
    @ApiModelProperty(value = "充值金额显示文案",example = "500KSH")
    private  List<String> rechargeAmountText = new ArrayList<>();
    @ApiModelProperty(value = "充值输入框文案",example = "Enter 500 and above")
    private  String rechargeInputText;
    @ApiModelProperty(value = "充值输入框文案颜色",example = "0xff9D9D9D")
    private  String rechargeInputTextColor;
    @ApiModelProperty(value = "最低充值金额",example = "300")
    private  BigDecimal minimumRechargeAmount;
    @ApiModelProperty(value = "提示语",example = "hello")
    private String tips;
    /*public RechargeAmountDoc(){
        Integer amount = 300;
        this.rechargeAmount.add(amount);
        this.rechargeAmount.add(500);
        this.rechargeAmount.add(1000);
        this.rechargeAmount.add(3000);
        this.rechargeAmount.add(5000);
        this.rechargeAmount.add(10000);
        this.rechargeAmount.add(30000);
        this.rechargeAmount.add(50000);

        this.rechargeAmountText.add(amount + "KSH");
        this.rechargeAmountText.add(500 + "KSH");
        this.rechargeAmountText.add(1000 + "KSH");
        this.rechargeAmountText.add(3000 + "KSH");
        this.rechargeAmountText.add(5000 + "KSH");
        this.rechargeAmountText.add(10000 + "KSH");
        this.rechargeAmountText.add(30000 + "KSH");
        this.rechargeAmountText.add(50000 + "KSH");

        this.rechargeInputText = "Enter "+amount+" and above";
        this.rechargeInputTextColor = "0xff9D9D9D";
        this.minimumRechargeAmount = new BigDecimal(amount);
        this.tips = "";
    }*/

    public List<Integer> getRechargeAmount() {
        return rechargeAmount;
    }

    public void setRechargeAmount(List<Integer> rechargeAmount) {
        this.rechargeAmount = rechargeAmount;
    }

    public List<String> getRechargeAmountText() {
        return rechargeAmountText;
    }

    public void setRechargeAmountText(List<String> rechargeAmountText) {
        this.rechargeAmountText = rechargeAmountText;
    }

    public String getRechargeInputText() {
        return rechargeInputText;
    }

    public void setRechargeInputText(String rechargeInputText) {
        this.rechargeInputText = rechargeInputText;
    }

    public String getRechargeInputTextColor() {
        return rechargeInputTextColor;
    }

    public void setRechargeInputTextColor(String rechargeInputTextColor) {
        this.rechargeInputTextColor = rechargeInputTextColor;
    }

    public BigDecimal getMinimumRechargeAmount() {
        return minimumRechargeAmount;
    }

    public void setMinimumRechargeAmount(BigDecimal minimumRechargeAmount) {
        this.minimumRechargeAmount = minimumRechargeAmount;
    }

    public String getTips() {
        return tips;
    }

    public void setTips(String tips) {
        this.tips = tips;
    }

    /*@Override
    public String toString() {
        return "RechargeAmountDoc{" +
                "rechargeAmount=" + rechargeAmount +
                ", rechargeAmountText=" + rechargeAmountText +
                ", rechargeInputText='" + rechargeInputText + '\'' +
                ", rechargeInputTextColor='" + rechargeInputTextColor + '\'' +
                ", minimumRechargeAmount=" + minimumRechargeAmount +
                ", tips='" + tips + '\'' +
                '}';
    }*/
}
