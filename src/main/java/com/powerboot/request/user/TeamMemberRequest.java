package com.powerboot.request.user;

import com.powerboot.request.BaseRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotEmpty;


@ApiModel(value = "团队成员详情")
public class TeamMemberRequest extends BaseRequest {

    @ApiModelProperty("lv等级")
    private Integer lv;
    @ApiModelProperty("当前页数")
    private Integer page;
    @ApiModelProperty("页数大小")
    private Integer pageSize;

    public Integer getLv() {
        return lv;
    }

    public void setLv(Integer lv) {
        this.lv = lv;
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
