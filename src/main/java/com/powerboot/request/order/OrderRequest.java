package com.powerboot.request.order;

import com.powerboot.request.BaseRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.util.Date;

@ApiModel
public class OrderRequest extends BaseRequest {


    @ApiModelProperty("商品id")
    private Long productId;
//    @ApiModelProperty("订单号")
//    private Long orderNumber;
//    @ApiModelProperty("用户id")
//    private Long userId;
//    @ApiModelProperty("商品金额")
//    private BigDecimal amount;
//    @ApiModelProperty("商品id")
//    private Long productId;
//    @ApiModelProperty("商品利率")
//    private Long productRation;
//    @ApiModelProperty("刷单佣金")
//    private BigDecimal productCommission;
//    @ApiModelProperty("顶级会员id")
//    private Long oneId;
//    @ApiModelProperty("顶级会员分润比例，如10% 则值为10")
//    private Long oneRatio;
//    @ApiModelProperty("顶级会员分润金额")
//    private BigDecimal oneAmount;
//    @ApiModelProperty("次顶级会员id")
//    private Long twoId;
//    @ApiModelProperty("次顶级会员分润比例")
//    private Long twoRatio;
//    @ApiModelProperty("次顶级会员分润金额")
//    private BigDecimal twoAmount;
//    @ApiModelProperty("上级会员id")
//    private Long threeId;
//    @ApiModelProperty("上级会员分润比例")
//    private Long threeRatio;
//    @ApiModelProperty("上级会员分润金额")
//    private BigDecimal threeAmount;
//    @ApiModelProperty("创建时间")
//    private Date createTime;
//    @ApiModelProperty("更新时间")
//    private Date updateTime;

    @ApiModelProperty("当前页数")
    private Integer page;
    @ApiModelProperty("页数大小")
    private Integer pageSize;

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
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
}
