package com.powerboot.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("奖励列表请求")
@Data
public class PrizeListRequest extends BaseRequest{

    @ApiModelProperty("奖励状态 1：未使用、2：已使用、3：已过期")
    private String prizeStatus;

}
