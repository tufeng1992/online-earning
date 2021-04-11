package com.powerboot.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.powerboot.utils.LocalDateUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.util.Date;

@ApiModel
public class TeamMemberDto {
    @ApiModelProperty("昵称")
    private String name;
    @ApiModelProperty("创建时间")
    @JsonFormat(pattern = LocalDateUtil.SIMPLE_DATE_FORMAT)
    private Date createTime;
    @ApiModelProperty("充值金额")
    private BigDecimal recharge;
    @ApiModelProperty("推荐人数")
    private Integer number;
    @ApiModelProperty("贡献金额")
    private BigDecimal contribution;
    @ApiModelProperty("提现金额")
    private BigDecimal withdraw;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public BigDecimal getRecharge() {
        return recharge;
    }

    public void setRecharge(BigDecimal recharge) {
        this.recharge = recharge;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public BigDecimal getContribution() {
        return contribution;
    }

    public void setContribution(BigDecimal contribution) {
        this.contribution = contribution;
    }

    public BigDecimal getWithdraw() {
        return withdraw;
    }

    public void setWithdraw(BigDecimal withdraw) {
        this.withdraw = withdraw;
    }
}
