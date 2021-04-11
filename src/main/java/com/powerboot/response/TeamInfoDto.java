package com.powerboot.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.util.List;

@ApiModel
public class TeamInfoDto {

    @ApiModelProperty("团队人数")
    private Integer teamSize;
    @ApiModelProperty("团队总贡献金额")
    private BigDecimal totalContribution;
    @ApiModelProperty("分销团队详情")
    private List<TeamDetailListDto> teamDetailListDtos;


    public Integer getTeamSize() {
        return teamSize;
    }

    public void setTeamSize(Integer teamSize) {
        this.teamSize = teamSize;
    }

    public BigDecimal getTotalContribution() {
        return totalContribution;
    }

    public void setTotalContribution(BigDecimal totalContribution) {
        this.totalContribution = totalContribution;
    }

    public List<TeamDetailListDto> getTeamDetailListDtos() {
        return teamDetailListDtos;
    }

    public void setTeamDetailListDtos(List<TeamDetailListDto> teamDetailListDtos) {
        this.teamDetailListDtos = teamDetailListDtos;
    }
}
