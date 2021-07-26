package com.powerboot.domain;

import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;



/**
 * 用户奖励关联信息表
 * 
 * @author system
 * @email system@cc.cc
 * @date 2021-07-25 14:31:40
 */
@Data
@TableName("user_prize_source")
public class UserPrizeSourceDO implements Serializable {
	private static final long serialVersionUID = 1L;
	
	//主键
	private Long id;
	//用户id
	private Long userId;
	//奖励关联用户id
	private Long prizeSourceUserId;
	//用户奖励id
	private Long userPrizeId;
	//创建时间
	private Date createTime;
	//更新时间
	private Date updateTime;
}
