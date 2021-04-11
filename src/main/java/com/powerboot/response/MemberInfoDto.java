package com.powerboot.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;

@ApiModel
public class MemberInfoDto {
    @ApiModelProperty("限额金额")
    private BigDecimal limitAmount;
    @ApiModelProperty("刷单上限")
    private Integer OrderTimes;
    @ApiModelProperty("手续费")
    private String feeStr;
    @ApiModelProperty("提现次数")
    private Integer withdrawTimes;
    @ApiModelProperty("会员充值金额")
    private BigDecimal Amount;
    @ApiModelProperty("充值类型")
    private Integer type;
    @ApiModelProperty("vip描述")
    private String vip;
    @ApiModelProperty("图片地址")
    private String picUrl;
    @ApiModelProperty("等级描述")
    private String vipDesc;

    public String getVipDesc() {
        return vipDesc;
    }

    public void setVipDesc(String vipDesc) {
        this.vipDesc = vipDesc;
    }

    public String getVip() {
        return vip;
    }

    public void setVip(String vip) {
        this.vip = vip;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public BigDecimal getLimitAmount() {
        return limitAmount;
    }

    public void setLimitAmount(BigDecimal limitAmount) {
        this.limitAmount = limitAmount;
    }

    public Integer getOrderTimes() {
        return OrderTimes;
    }

    public void setOrderTimes(Integer orderTimes) {
        OrderTimes = orderTimes;
    }

    public String getFeeStr() {
        return feeStr;
    }

    public void setFeeStr(String feeStr) {
        this.feeStr = feeStr;
    }

    public Integer getWithdrawTimes() {
        return withdrawTimes;
    }

    public void setWithdrawTimes(Integer withdrawTimes) {
        this.withdrawTimes = withdrawTimes;
    }

    public BigDecimal getAmount() {
        return Amount;
    }

    public void setAmount(BigDecimal amount) {
        Amount = amount;
    }
}
