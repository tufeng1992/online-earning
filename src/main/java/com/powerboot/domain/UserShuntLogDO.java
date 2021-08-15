package com.powerboot.domain;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;


/**
 * 用户分流信息记录
 * @author system
 * @email system@cc.cc
 * @date 2021-08-15 14:40:42
 */
@Data
@TableName(value = "user_shunt_log")
public class UserShuntLogDO implements Serializable {
	private static final long serialVersionUID = 1L;
	
	//主键
	@TableId
	private Long id;
	//销售人员id
	private Long saleId;
	//分流类型 1.系统分配、2.后台分配、3.用户邀请
	private Integer shuntType;
	//用户id
	private Long userId;
	//创建时间
	private Date createTime;
	//更新时间
	private Date updateTime;
}
