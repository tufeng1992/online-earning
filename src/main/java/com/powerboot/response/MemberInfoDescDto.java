package com.powerboot.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@ApiModel
@Data
public class MemberInfoDescDto {
    @ApiModelProperty("限额金额数文案 第一行展示")
    private String limitAmount;
    @ApiModelProperty("刷单上限文案 第二行展示")
    private String OrderTimes;
    @ApiModelProperty("手续费文案 第三行展示")
    private String feeStr;
    @ApiModelProperty("提现次文案 垫底展示")
    private String withdrawTimes;
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
    @ApiModelProperty("购买vip条件描述")
    private String buyVipCondition;

    public String getLimitAmount() {
        return limitAmount;
    }

    public void setLimitAmount(String limitAmount) {
        this.limitAmount = limitAmount;
    }

    public String getOrderTimes() {
        return OrderTimes;
    }

    public void setOrderTimes(String orderTimes) {
        OrderTimes = orderTimes;
    }

    public String getFeeStr() {
        return feeStr;
    }

    public void setFeeStr(String feeStr) {
        this.feeStr = feeStr;
    }

    public String getWithdrawTimes() {
        return withdrawTimes;
    }

    public void setWithdrawTimes(String withdrawTimes) {
        this.withdrawTimes = withdrawTimes;
    }

    public BigDecimal getAmount() {
        return Amount;
    }

    public void setAmount(BigDecimal amount) {
        Amount = amount;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
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

    public String getVipDesc() {
        return vipDesc;
    }

    public void setVipDesc(String vipDesc) {
        this.vipDesc = vipDesc;
    }
}
