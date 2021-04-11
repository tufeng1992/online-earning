package com.powerboot.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;

@ApiModel
public class HomeDetailsDto {
    @ApiModelProperty("余额")
    private BigDecimal totalAssets;
    @ApiModelProperty("虚拟余额")
    private BigDecimal virtualCurrency;
    @ApiModelProperty("昵称")
    private String nickName;
    @ApiModelProperty("vip等级")
    private String vipLevel;
    @ApiModelProperty("昨日收益")
    private BigDecimal yesterdayEarnings;
    @ApiModelProperty("今日收益")
    private BigDecimal totalEarnings;
    @ApiModelProperty("累计收益")
    private BigDecimal cumulativeIncome;

    public BigDecimal getTotalAssets() {
        return totalAssets;
    }

    public void setTotalAssets(BigDecimal totalAssets) {
        this.totalAssets = totalAssets;
    }

    public BigDecimal getVirtualCurrency() {
        return virtualCurrency;
    }

    public void setVirtualCurrency(BigDecimal virtualCurrency) {
        this.virtualCurrency = virtualCurrency;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getVipLevel() {
        return vipLevel;
    }

    public void setVipLevel(String vipLevel) {
        this.vipLevel = vipLevel;
    }

    public BigDecimal getYesterdayEarnings() {
        return yesterdayEarnings;
    }

    public void setYesterdayEarnings(BigDecimal yesterdayEarnings) {
        this.yesterdayEarnings = yesterdayEarnings;
    }

    public BigDecimal getTotalEarnings() {
        return totalEarnings;
    }

    public void setTotalEarnings(BigDecimal totalEarnings) {
        this.totalEarnings = totalEarnings;
    }

    public BigDecimal getCumulativeIncome() {
        return cumulativeIncome;
    }

    public void setCumulativeIncome(BigDecimal cumulativeIncome) {
        this.cumulativeIncome = cumulativeIncome;
    }
}
