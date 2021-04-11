package com.powerboot.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;

/**
 * Create  2020 - 11 - 06 3:12 下午
 */
@ApiModel(value = "成就返回")
public class MissionResponse {
    @ApiModelProperty(value = "下级数量")
    private Integer totalPeople;

    @ApiModelProperty(value = "完成人数")
    private Long totalComplete;

    @ApiModelProperty(value = "头部文案",example = "Upgrade task: users who have recharge are valid users")
    private String topTips;

    @ApiModelProperty(value = "任务奖励基数",example = "50")
    private BigDecimal taskAmount;

    public Integer getTotalPeople() {
        return totalPeople;
    }

    public void setTotalPeople(Integer totalPeople) {
        this.totalPeople = totalPeople;
    }

    public Long getTotalComplete() {
        return totalComplete;
    }

    public void setTotalComplete(Long totalComplete) {
        this.totalComplete = totalComplete;
    }

    public String getTopTips() {
        return topTips;
    }

    public void setTopTips(String topTips) {
        this.topTips = topTips;
    }

    public BigDecimal getTaskAmount() {
        return taskAmount;
    }

    public void setTaskAmount(BigDecimal taskAmount) {
        this.taskAmount = taskAmount;
    }
}
