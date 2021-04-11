package com.powerboot.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "成就返回")
public class NewMissionResponse {

    @ApiModelProperty(value = "下级数量",example = "0")
    private Integer totalPeople;

    @ApiModelProperty(value = "完成人数",example = "0")
    private Long totalComplete;

    @ApiModelProperty(value = "头提示",example = "Upgrade task: users who have recharge are valid users")
    private String TopTips;

    @ApiModelProperty(value = "成就任务返回")
    private NewMissionTaskResponse newMissionTaskResponse;

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
        return TopTips;
    }

    public void setTopTips(String topTips) {
        TopTips = topTips;
    }

    public NewMissionTaskResponse getNewMissionTaskResponse() {
        return newMissionTaskResponse;
    }

    public void setNewMissionTaskResponse(NewMissionTaskResponse newMissionTaskResponse) {
        this.newMissionTaskResponse = newMissionTaskResponse;
    }
}
