package com.powerboot.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

@ApiModel(value = "成就任务返回")
public class NewMissionTaskResponse implements Serializable {
    @ApiModelProperty(value = "任务描述",example = "Invited 1 valid member")
    private String taskText;
    @ApiModelProperty(value = "任务奖励",example = "Task reward: 100 KSH")
    private String taskRewardText;
    @ApiModelProperty(value = "任务完成",example = "0/1")
    private String taskCompleteText;
    @ApiModelProperty(value = "任务完成状态",example = "false")
    private Boolean taskCompleteStatus;

    public String getTaskText() {
        return taskText;
    }

    public void setTaskText(String taskText) {
        this.taskText = taskText;
    }

    public String getTaskRewardText() {
        return taskRewardText;
    }

    public void setTaskRewardText(String taskRewardText) {
        this.taskRewardText = taskRewardText;
    }

    public String getTaskCompleteText() {
        return taskCompleteText;
    }

    public void setTaskCompleteText(String taskCompleteText) {
        this.taskCompleteText = taskCompleteText;
    }

    public Boolean getTaskCompleteStatus() {
        return taskCompleteStatus;
    }

    public void setTaskCompleteStatus(Boolean taskCompleteStatus) {
        this.taskCompleteStatus = taskCompleteStatus;
    }
}
