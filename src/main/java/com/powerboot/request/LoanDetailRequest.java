package com.powerboot.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@ApiModel
public class LoanDetailRequest extends BaseRequest{

    @ApiModelProperty("需要支付金额")
    @NotNull(message = "please input payment amount")
    private BigDecimal payAmount;
    @ApiModelProperty(value = "类型:1-充值 2-购买VIP2 3-购买VIP3")
    private Integer type;
    @ApiModelProperty(value = "用户ID",hidden = true)
    private Long userId;
    @ApiModelProperty(value = "线下支付凭证")
    private String refNo;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public BigDecimal getPayAmount() {
        return payAmount;
    }

    public void setPayAmount(BigDecimal payAmount) {
        this.payAmount = payAmount;
    }

    public String getRefNo() {
        return refNo;
    }

    public void setRefNo(String refNo) {
        this.refNo = refNo;
    }

    @Override
    public String toString() {
        return "LoanDetailRequest{" +
                "payAmount=" + payAmount +
                ", type=" + type +
                ", userId=" + userId +
                ", refNo='" + refNo + '\'' +
                '}';
    }
}
