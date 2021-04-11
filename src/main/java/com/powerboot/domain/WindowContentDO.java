package com.powerboot.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * 弹框配置表
 * Create  2020 - 10 - 29 7:42 下午
 */
@ApiModel(value = "弹窗信息")
public class WindowContentDO implements Serializable {
    //标题
    @ApiModelProperty(value = "标题")
    private String title;
    //内容
    @ApiModelProperty(value = "内容")
    private String content;

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
