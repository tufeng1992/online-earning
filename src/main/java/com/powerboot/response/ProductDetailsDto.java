package com.powerboot.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;

@ApiModel
public class ProductDetailsDto {
    @ApiModelProperty("产品详情")
    private ProductListDto productListDto;

    @ApiModelProperty("今日收益详情")
    private TodayResultDto todayResultDto;

    public ProductListDto getProductListDto() {
        return productListDto;
    }

    public void setProductListDto(ProductListDto productListDto) {
        this.productListDto = productListDto;
    }

    public TodayResultDto getTodayResultDto() {
        return todayResultDto;
    }

    public void setTodayResultDto(TodayResultDto todayResultDto) {
        this.todayResultDto = todayResultDto;
    }
}
