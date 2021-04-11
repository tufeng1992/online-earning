package com.powerboot.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;

/**
 * 理财收益汇总
 */
@ApiModel
public class FinancialAssetsResponse {
    @ApiModelProperty("余额")
    private BigDecimal balance;
    @ApiModelProperty("今日收益")
    private BigDecimal interest;
    @ApiModelProperty("昨天收益")
    private BigDecimal yesterdayEarning;
    @ApiModelProperty("总收益")
    private BigDecimal totalRevenue;

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public BigDecimal getInterest() {
        return interest;
    }

    public void setInterest(BigDecimal interest) {
        this.interest = interest;
    }

    public BigDecimal getYesterdayEarning() {
        return yesterdayEarning;
    }

    public void setYesterdayEarning(BigDecimal yesterdayEarning) {
        this.yesterdayEarning = yesterdayEarning;
    }

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }
}
