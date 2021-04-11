package com.powerboot.response;

import com.powerboot.base.BaseResponse;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel
public class BuyMemberInfoResponse<T> extends BaseResponse {
    //标题
    @ApiModelProperty("标题")
    private String title;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
