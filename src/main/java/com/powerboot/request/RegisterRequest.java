package com.powerboot.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * Create  2020 - 10 - 28 5:21 下午
 */
@Data
@ApiModel(value = "注册入参")
public class RegisterRequest extends BaseRequest{
    @ApiModelProperty(value = "手机号")
    @NotEmpty(message = "please enter your phone number")
    private String mobile;
    @ApiModelProperty(value = "验证码")
    @NotEmpty(message = "please enter your verification code")
    private String verificationCode;
    @ApiModelProperty(value = "邀请码")
    private String referralCode;
    @ApiModelProperty(value = "登陆密码")
    @NotEmpty(message = "please enter your password")
    private String password;
    @ApiModelProperty(value = "邮箱")
    @NotEmpty(message = "please enter your email")
    private String email;

    @ApiModelProperty("注册sdk类型")
    private String sdkType;

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

    public String getReferralCode() {
        return referralCode;
    }

    public void setReferralCode(String referralCode) {
        this.referralCode = referralCode;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
