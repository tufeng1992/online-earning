package com.powerboot.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * 功能简介表
 */
@ApiModel(value = "功能简介")
public class IncomeMethodsDO implements Serializable {
    //标题
    @ApiModelProperty(value = "标题")
    private String title;
    //内容
    @ApiModelProperty(value = "内容")
    private String content;
    //是否需要描红 0-不需要 1-需要
    @ApiModelProperty(value = "是否需要描红 0-不需要 1-需要")
    private Integer showRed;

    public Integer getShowRed() {
        return showRed;
    }

    public void setShowRed(Integer showRed) {
        this.showRed = showRed;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
