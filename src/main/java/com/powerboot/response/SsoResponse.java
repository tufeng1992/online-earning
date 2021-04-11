package com.powerboot.response;

import com.powerboot.domain.UserDO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel
public class SsoResponse {
    @ApiModelProperty("用户信息")
    private UserDO user;
    @ApiModelProperty("认证签名")
    private String sign;
    @ApiModelProperty("是否新用户 true-新用户 false-老用户")
    private Boolean newUserFlag;

    public UserDO getUser() {
        return user;
    }

    public void setUser(UserDO user) {
        this.user = user;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public Boolean getNewUserFlag() {
        return newUserFlag;
    }

    public void setNewUserFlag(Boolean newUserFlag) {
        this.newUserFlag = newUserFlag;
    }
}
