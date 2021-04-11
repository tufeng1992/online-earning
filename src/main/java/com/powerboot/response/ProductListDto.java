package com.powerboot.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;

@ApiModel
public class ProductListDto {
    @ApiModelProperty("商品id")
    private Long productId;

    @ApiModelProperty("商品图片地址")
    private String picture;
    @ApiModelProperty("商品描述")
    private String describtion;
    @ApiModelProperty("商品评价")
    private String comment;
    @ApiModelProperty("商品需要的余额等级")
    private String level;

    @ApiModelProperty("商品刷单佣金本金总和")
    private BigDecimal returnFund;

    @ApiModelProperty("商品金额")
    private BigDecimal price;

    @ApiModelProperty("返利比列")
    private BigDecimal ratio;
    @ApiModelProperty("能否点击 true：可以  false：不可以")
    private Boolean clickButton;
    @ApiModelProperty("图片简介")
    private String introduction;
    @ApiModelProperty("展示金额")
    private BigDecimal descAmount;

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public BigDecimal getDescAmount() {
        return descAmount;
    }

    public void setDescAmount(BigDecimal descAmount) {
        this.descAmount = descAmount;
    }

    public BigDecimal getReturnFund() {
        return returnFund;
    }

    public String getDescribtion() {
        return describtion;
    }

    public void setDescribtion(String describtion) {
        this.describtion = describtion;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setReturnFund(BigDecimal returnFund) {
        this.returnFund = returnFund;
    }

    public Long getProductId() {
        return productId;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }


    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getRatio() {
        return ratio;
    }

    public void setRatio(BigDecimal ratio) {
        this.ratio = ratio;
    }

    public Boolean getClickButton() {
        return clickButton;
    }

    public void setClickButton(Boolean clickButton) {
        this.clickButton = clickButton;
    }
}
