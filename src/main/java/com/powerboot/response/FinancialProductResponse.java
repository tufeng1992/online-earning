package com.powerboot.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;

/**
 * 理财产品返回值
 */
@ApiModel
public class FinancialProductResponse {
    @ApiModelProperty("理财产品id")
    private Integer id;
    //理财产品名称
    @ApiModelProperty("理财产品名称")
    private String name;
    //日利率
    @ApiModelProperty("日利率")
    private BigDecimal dayRate;
    //年利率
    @ApiModelProperty("年利率")
    private BigDecimal yearRate;
    //锁定天数
    @ApiModelProperty("锁定天数")
    private Integer lockDays;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getDayRate() {
        return dayRate;
    }

    public void setDayRate(BigDecimal dayRate) {
        this.dayRate = dayRate;
    }

    public BigDecimal getYearRate() {
        return yearRate;
    }

    public void setYearRate(BigDecimal yearRate) {
        this.yearRate = yearRate;
    }

    public Integer getLockDays() {
        return lockDays;
    }

    public void setLockDays(Integer lockDays) {
        this.lockDays = lockDays;
    }
}
