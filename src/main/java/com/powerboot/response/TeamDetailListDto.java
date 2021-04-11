package com.powerboot.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.models.auth.In;

import java.math.BigDecimal;
import java.util.List;

@ApiModel
public class TeamDetailListDto {
    @ApiModelProperty("lv等级")
    private Integer lv;
    @ApiModelProperty("人数")
    private Integer size;
    @ApiModelProperty("贡献金额")
    private BigDecimal bonus;

    public Integer getLv() {
        return lv;
    }

    public void setLv(Integer lv) {
        this.lv = lv;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public BigDecimal getBonus() {
        return bonus;
    }

    public void setBonus(BigDecimal bonus) {
        this.bonus = bonus;
    }
}
