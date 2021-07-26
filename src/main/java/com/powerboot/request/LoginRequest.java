package com.powerboot.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@ApiModel
@Data
public class LoginRequest extends BaseRequest{

    @ApiModelProperty("手机号")
    private String mobile;

    @ApiModelProperty("密码")
    private String password;

    @ApiModelProperty("谷歌adid")
    private String adid;

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
