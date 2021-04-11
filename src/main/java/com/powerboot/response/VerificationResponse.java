package com.powerboot.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel
public class VerificationResponse {

    @ApiModelProperty("脸部是否认证 true 已认证 false 未认证")
    private Boolean face;
    @ApiModelProperty("基础信息是否认证 true 已认证 false 未认证")
    private Boolean basic;
    @ApiModelProperty("银行是否认证 true 已认证 false 未认证")
    private Boolean bank;
    @ApiModelProperty("是否已支付 true 已支付 false 未支付")
    private Boolean pay;

    public Boolean getPay() {
        return pay;
    }

    public void setPay(Boolean pay) {
        this.pay = pay;
    }

    public Boolean getFace() {
        return face;
    }

    public void setFace(Boolean face) {
        this.face = face;
    }

    public Boolean getBasic() {
        return basic;
    }

    public void setBasic(Boolean basic) {
        this.basic = basic;
    }

    public Boolean getBank() {
        return bank;
    }

    public void setBank(Boolean bank) {
        this.bank = bank;
    }
}
