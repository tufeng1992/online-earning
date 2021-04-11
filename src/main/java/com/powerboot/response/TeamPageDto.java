package com.powerboot.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.util.List;

@ApiModel
public class TeamPageDto {

    @ApiModelProperty("当前页数")
    private Integer page;
    @ApiModelProperty("总页数")
    private Integer totalPage;
    @ApiModelProperty("页数大小")
    private Integer pageSize;
    @ApiModelProperty("刷单列表")
    private List<TeamMemberDto> teamMemberDtoList;

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(Integer totalPage) {
        this.totalPage = totalPage;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public List<TeamMemberDto> getTeamMemberDtoList() {
        return teamMemberDtoList;
    }

    public void setTeamMemberDtoList(List<TeamMemberDto> teamMemberDtoList) {
        this.teamMemberDtoList = teamMemberDtoList;
    }
}
