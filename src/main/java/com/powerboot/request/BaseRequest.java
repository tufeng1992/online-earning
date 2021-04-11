package com.powerboot.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotEmpty;

/**
 * @date : Created in 2020/7/15 17:02
 **/
@ApiModel
public class BaseRequest {

    @ApiModelProperty(value = "APP唯一标识符",required = true)
    @NotEmpty(message = "app id not found!")
    private String appId;

    @ApiModelProperty("用户认证标识符")
    private String sign;

    @ApiModelProperty("设备号")
    private String deviceNumber;

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getDeviceNumber() {
        return deviceNumber;
    }

    public void setDeviceNumber(String deviceNumber) {
        this.deviceNumber = deviceNumber;
    }

    @Override
    public String toString() {
        return "BaseRequest{" +
                "appId='" + appId + '\'' +
                ", sign='" + sign + '\'' +
                ", deviceNumber='" + deviceNumber + '\'' +
                '}';
    }
}
