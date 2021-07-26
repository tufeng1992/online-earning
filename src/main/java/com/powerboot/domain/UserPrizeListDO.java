package com.powerboot.domain;

import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;



/**
 * 用户奖励信息表
 * 
 * @author system
 * @email system@cc.cc
 * @date 2021-07-25 14:31:40
 */
@Data
@TableName("user_prize_list")
public class UserPrizeListDO implements Serializable {
	private static final long serialVersionUID = 1L;
	
	//主键
	private Long id;
	//用户id
	private Long userId;
	//奖励来源 1：下级用户首充、2：邀请好友、3：完成任务
	private Integer prizeSource;
	//奖励金额基数
	private BigDecimal prizeBaseAmount;
	//奖励状态 1：未使用、2：已使用、3：已过期
	private Integer prizeStatus;
	//奖励比例区间
	private String prizeRatio;
	//奖励截止日期
	private Date prizeEndTime;
	//奖励获得金额
	private BigDecimal prizeGetAmount;
	//奖励获得时间
	private Date prizeGetTime;
	//创建时间
	private Date createTime;
	//更新时间
	private Date updateTime;
}
