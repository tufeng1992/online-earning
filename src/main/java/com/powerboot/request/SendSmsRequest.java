package com.powerboot.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotEmpty;

/**
 * Create  2020 - 07 - 16 10:45 下午
 */
@ApiModel("发送验证码")
public class SendSmsRequest extends BaseRequest{

    @ApiModelProperty("手机号")
    @NotEmpty(message = "please input tel number!")
    private String tel;

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }
}
