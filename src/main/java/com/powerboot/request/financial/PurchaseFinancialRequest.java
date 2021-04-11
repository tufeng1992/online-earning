package com.powerboot.request.financial;

import com.powerboot.request.BaseRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 购买理财产品请求
 */
@ApiModel
public class PurchaseFinancialRequest  extends BaseRequest {

    @ApiModelProperty("理财产品id")
    @NotNull(message = "Please select financial product")
    private Integer productId;

    @ApiModelProperty("金额")
    @NotNull(message = "please enter the transfer amount")
    private BigDecimal amount;

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
