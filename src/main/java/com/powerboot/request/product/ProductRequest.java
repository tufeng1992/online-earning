package com.powerboot.request.product;

import com.powerboot.request.BaseRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.models.auth.In;

@ApiModel
public class ProductRequest extends BaseRequest {
    @ApiModelProperty("类型 1、首页商品页  2、刷单商品页")
    private Integer type;

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}
