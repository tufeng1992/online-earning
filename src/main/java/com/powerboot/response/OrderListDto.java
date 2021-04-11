package com.powerboot.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.util.List;

@ApiModel
public class OrderListDto {

    @ApiModelProperty("总收益")
    private BigDecimal remainingAvailableAssets;
    @ApiModelProperty("今日刷单数量/总数")
    private String todayOrder;
    @ApiModelProperty("当前页数")
    private Integer page;
    @ApiModelProperty("总页数")
    private Integer totalPage;
    @ApiModelProperty("页数大小")
    private Integer pageSize;
    @ApiModelProperty("刷单列表")
    private List<OrderInfoDto> orderList;

    public Integer getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(Integer totalPage) {
        this.totalPage = totalPage;
    }

    public BigDecimal getRemainingAvailableAssets() {
        return remainingAvailableAssets;
    }

    public void setRemainingAvailableAssets(BigDecimal remainingAvailableAssets) {
        this.remainingAvailableAssets = remainingAvailableAssets;
    }

    public String getTodayOrder() {
        return todayOrder;
    }

    public void setTodayOrder(String todayOrder) {
        this.todayOrder = todayOrder;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public List<OrderInfoDto> getOrderList() {
        return orderList;
    }

    public void setOrderList(List<OrderInfoDto> orderList) {
        this.orderList = orderList;
    }
}
