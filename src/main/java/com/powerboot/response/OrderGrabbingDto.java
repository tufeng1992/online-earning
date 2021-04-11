package com.powerboot.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.powerboot.utils.LocalDateUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@ApiModel
public class OrderGrabbingDto {

    @ApiModelProperty("订单号")
    private String orderNumber;
    @ApiModelProperty("商品金额")
    private BigDecimal totalOrderAmount;
    @ApiModelProperty("商品金额+佣金金额")
    private BigDecimal expectedReturn;
    @ApiModelProperty("佣金比列")
    private BigDecimal ratio;
    @ApiModelProperty("获取时间")
    @JsonFormat(pattern = LocalDateUtil.SIMPLE_DATE_FORMAT)
    private Date capTureTime;

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public BigDecimal getTotalOrderAmount() {
        return totalOrderAmount;
    }

    public void setTotalOrderAmount(BigDecimal totalOrderAmount) {
        this.totalOrderAmount = totalOrderAmount;
    }

    public BigDecimal getExpectedReturn() {
        return expectedReturn;
    }

    public void setExpectedReturn(BigDecimal expectedReturn) {
        this.expectedReturn = expectedReturn;
    }

    public BigDecimal getRatio() {
        return ratio;
    }

    public void setRatio(BigDecimal ratio) {
        this.ratio = ratio;
    }

    public Date getCapTureTime() {
        return capTureTime;
    }

    public void setCapTureTime(Date capTureTime) {
        this.capTureTime = capTureTime;
    }
}
