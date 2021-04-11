package com.powerboot.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.math.BigDecimal;

/**
 * 理财订单返回
 */
@ApiModel
public class FinancialOrderResponse {
    @ApiModelProperty("订单id")
    private Integer orderId;
    @ApiModelProperty("理财产品名称")
    private String productName;
    @ApiModelProperty("日利率")
    private BigDecimal dayRate;
    @ApiModelProperty("锁定天数")
    private Integer lockDays;
    @ApiModelProperty("状态 1-未到期 2-已到期 3-提前赎回")
    private Integer orderStatus;
    @ApiModelProperty("本金")
    private BigDecimal amount;
    @ApiModelProperty("利息")
    private BigDecimal totalInterest;
    @ApiModelProperty("本息合计")
    private BigDecimal totalAmount;
    @ApiModelProperty("购买时间")
    private String buyDate;
    @ApiModelProperty("到期时间")
    private String lastDate;
    @ApiModelProperty("提前赎回手续费")
    private BigDecimal calledAmount;
    @ApiModelProperty("提前赎回时间")
    private String calledTime;
    @ApiModelProperty("提前赎回到账余额")
    private BigDecimal calledBalance;

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public BigDecimal getDayRate() {
        return dayRate;
    }

    public void setDayRate(BigDecimal dayRate) {
        this.dayRate = dayRate;
    }

    public Integer getLockDays() {
        return lockDays;
    }

    public void setLockDays(Integer lockDays) {
        this.lockDays = lockDays;
    }

    public Integer getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(Integer orderStatus) {
        this.orderStatus = orderStatus;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getTotalInterest() {
        return totalInterest;
    }

    public void setTotalInterest(BigDecimal totalInterest) {
        this.totalInterest = totalInterest;
    }

    public String getBuyDate() {
        return buyDate;
    }

    public void setBuyDate(String buyDate) {
        this.buyDate = buyDate;
    }

    public String getLastDate() {
        return lastDate;
    }

    public void setLastDate(String lastDate) {
        this.lastDate = lastDate;
    }

    public BigDecimal getCalledAmount() {
        return calledAmount;
    }

    public void setCalledAmount(BigDecimal calledAmount) {
        this.calledAmount = calledAmount;
    }

    public String getCalledTime() {
        return calledTime;
    }

    public void setCalledTime(String calledTime) {
        this.calledTime = calledTime;
    }

    public BigDecimal getCalledBalance() {
        return calledBalance;
    }

    public void setCalledBalance(BigDecimal calledBalance) {
        this.calledBalance = calledBalance;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }
}
