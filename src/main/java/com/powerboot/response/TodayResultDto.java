package com.powerboot.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;

@ApiModel
public class TodayResultDto {
    @ApiModelProperty("总余额")
    private BigDecimal totalAssets;
    @ApiModelProperty("今日订单数")
    private String totalOrder;
    @ApiModelProperty("昨日收益")
    private BigDecimal yesterdayEarnings;
    @ApiModelProperty("今日收益")
    private BigDecimal totalEarnings;
    @ApiModelProperty("昨天团队收益")
    private BigDecimal yesterdayTeamEarnings;
    @ApiModelProperty("团队今日收益")
    private BigDecimal teamBenefitsEarnings;

    public BigDecimal getTotalAssets() {
        return totalAssets;
    }

    public void setTotalAssets(BigDecimal totalAssets) {
        this.totalAssets = totalAssets;
    }

    public String getTotalOrder() {
        return totalOrder;
    }

    public void setTotalOrder(String totalOrder) {
        this.totalOrder = totalOrder;
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

    public BigDecimal getYesterdayTeamEarnings() {
        return yesterdayTeamEarnings;
    }

    public void setYesterdayTeamEarnings(BigDecimal yesterdayTeamEarnings) {
        this.yesterdayTeamEarnings = yesterdayTeamEarnings;
    }

    public BigDecimal getTeamBenefitsEarnings() {
        return teamBenefitsEarnings;
    }

    public void setTeamBenefitsEarnings(BigDecimal teamBenefitsEarnings) {
        this.teamBenefitsEarnings = teamBenefitsEarnings;
    }
}
