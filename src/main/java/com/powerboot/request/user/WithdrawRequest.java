package com.powerboot.request.user;

import com.powerboot.request.BaseRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@ApiModel(value = "提现入参")
public class WithdrawRequest extends BaseRequest {
    @ApiModelProperty(value = "提现金额")
    @NotNull(message = "please enter amount")
    private BigDecimal withdrawAmount;

    public BigDecimal getWithdrawAmount() {
        return withdrawAmount;
    }

    public void setWithdrawAmount(BigDecimal withdrawAmount) {
        this.withdrawAmount = withdrawAmount;
    }
}
