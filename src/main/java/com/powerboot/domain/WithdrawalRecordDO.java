package com.powerboot.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 */
@ApiModel(value = "提现信息")
public class WithdrawalRecordDO implements Serializable {
    @ApiModelProperty(value = "id")
    private Integer id;
    @ApiModelProperty(value = "用户id,-1为系统")
    private Long userId;
    @ApiModelProperty(value = "当日系统限制提现总金额")
    private BigDecimal totalAmount;
    @ApiModelProperty(value = "当日累计提现金额")
    private BigDecimal changeAmount;
    @ApiModelProperty(value = "当日累计税金")
    private BigDecimal changeTax;
    @ApiModelProperty(value = "生效时间")
    private LocalDate effectiveDate;
    @ApiModelProperty(value = "当日累计提现次数")
    private Integer count;
    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;
    @ApiModelProperty(value = "更新时间")
    private LocalDateTime updateTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getChangeAmount() {
        return changeAmount;
    }

    public void setChangeAmount(BigDecimal changeAmount) {
        this.changeAmount = changeAmount;
    }

    public BigDecimal getChangeTax() {
        return changeTax;
    }

    public void setChangeTax(BigDecimal changeTax) {
        this.changeTax = changeTax;
    }

    public LocalDate getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(LocalDate effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }
}
