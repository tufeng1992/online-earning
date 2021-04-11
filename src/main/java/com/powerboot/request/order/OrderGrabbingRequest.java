package com.powerboot.request.order;

import com.powerboot.request.BaseRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;

@ApiModel
public class OrderGrabbingRequest  extends BaseRequest {


    @ApiModelProperty("订单号")
    private String orderNumber;
    @ApiModelProperty("商品id")
    private Long productId;
    @ApiModelProperty("商品金额")
    private BigDecimal totalOrderAmount;
    @ApiModelProperty("商品金额+佣金金额")
    private BigDecimal expectedReturn;
    @ApiModelProperty("佣金比列")
    private BigDecimal ratio;

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
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
}
