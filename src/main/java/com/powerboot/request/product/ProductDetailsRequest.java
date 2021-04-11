package com.powerboot.request.product;

import com.powerboot.request.BaseRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel
public class ProductDetailsRequest extends BaseRequest {

    @ApiModelProperty("类型 1、首页商品页  2、刷单商品页")
    private Long productId;

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }
}
