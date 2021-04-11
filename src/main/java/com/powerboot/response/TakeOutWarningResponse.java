package com.powerboot.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.math.BigDecimal;

/**
 * 理财提前赎回警告返回
 */
@ApiModel
public class TakeOutWarningResponse {
    @ApiModelProperty("提前赎回手续费比例")
    private BigDecimal calledRate;
    @ApiModelProperty("提前赎回手续费")
    private BigDecimal calledAmount;

    public BigDecimal getCalledRate() {
        return calledRate;
    }

    public void setCalledRate(BigDecimal calledRate) {
        this.calledRate = calledRate;
    }

    public BigDecimal getCalledAmount() {
        return calledAmount;
    }

    public void setCalledAmount(BigDecimal calledAmount) {
        this.calledAmount = calledAmount;
    }
}
