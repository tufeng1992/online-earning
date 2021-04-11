package com.powerboot.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.util.List;

/**
 * 我的账户信息
 */
@ApiModel
public class MyAccountDto {

    @ApiModelProperty("余额")
    private BigDecimal yourTotalAssets;
    @ApiModelProperty("虚拟余额")
    private BigDecimal virtualCurrency;
    @ApiModelProperty("昨日收益")
    private BigDecimal yesterdayEarning;
    @ApiModelProperty("今日收益")
    private BigDecimal todayEarning;
    @ApiModelProperty("累计收益")
    private BigDecimal cumulativeEarning;
    @ApiModelProperty("刷单列表")
    private List<OrderInfoDto> orderList;

}
