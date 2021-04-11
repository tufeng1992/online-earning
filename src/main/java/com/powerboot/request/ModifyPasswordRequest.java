package com.powerboot.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotEmpty;

/**
 * Create  2020 - 10 - 29 4:39 下午
 */
@ApiModel(value = "修改密码入参")
public class ModifyPasswordRequest extends BaseRequest{
    @ApiModelProperty(value = "手机号")
    @NotEmpty(message = "please enter your phone number")
    private String mobile;
    @ApiModelProperty(value = "验证码")
    @NotEmpty(message = "please enter your verification code")
    private String verificationCode;
    @ApiModelProperty(value = "登陆密码")
    @NotEmpty(message = "please enter your password")
    private String password;

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getVerificationCode() {
        return verificationCode;
    }

    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
