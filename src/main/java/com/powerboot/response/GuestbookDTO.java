package com.powerboot.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@ApiModel("留言信息")
public class GuestbookDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    //主键
    @ApiModelProperty("主键")
    private Long id;
    //留言内容
    @ApiModelProperty("留言内容")
    private String guestbookContent;
    //留言人id
    @ApiModelProperty("留言人id")
    private Long guestbookUserId;
    //留言对象id
    @ApiModelProperty("留言对象id")
    private Long guestbookTargetId;
    //创建时间
    @ApiModelProperty("创建时间")
    private Date createTime;
    //更新时间
    @ApiModelProperty("更新时间")
    private Date updateTime;

    /**
     * 是否已读
     */
    @ApiModelProperty("是否已读")
    private Boolean readed;

}
