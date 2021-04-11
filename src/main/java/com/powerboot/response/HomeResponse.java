package com.powerboot.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.List;

@ApiModel
public class HomeResponse {

    @ApiModelProperty("消息列表")
    private List<String> successMessageList;

    @ApiModelProperty(value = "文案颜色", example = "0xff9D9D9D")
    private String color;

    public List<String> getSuccessMessageList() {
        return successMessageList;
    }

    public void setSuccessMessageList(List<String> successMessageList) {
        this.successMessageList = successMessageList;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
