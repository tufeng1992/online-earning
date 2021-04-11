package com.powerboot.request.financial;

import com.powerboot.request.BaseRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;

/**
 * 理财订单赎回请求
 */
@ApiModel
public class TakeOutFinancialRequest extends BaseRequest{

    @ApiModelProperty("理财订单id")
    @NotNull(message = "Please select record")
    private Integer orderId;

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }
}
