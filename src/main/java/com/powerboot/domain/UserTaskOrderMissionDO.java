package com.powerboot.domain;

import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;



/**
 * 刷单任务完成记录表
 * 
 * @author system
 * @email system@cc.cc
 * @date 2021-07-25 14:31:40
 */
@Data
@TableName("user_task_order_mission")
public class UserTaskOrderMissionDO implements Serializable {
	private static final long serialVersionUID = 1L;
	
	//主键
	private Long id;
	//用户id
	private Long userId;
	//刷单数量
	private Integer taskOrderNum;
	//刷单总金额
	private BigDecimal taskOrderAmount;
	//创建时间
	private Date createTime;
	//更新时间
	private Date updateTime;
}
