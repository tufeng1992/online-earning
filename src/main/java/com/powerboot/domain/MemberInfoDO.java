package com.powerboot.domain;

import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;



/**
 * 会员卡配置信息表
 * 
 * @author system
 * @email system@cc.cc
 * @date 2021-06-21 23:41:26
 */
@Data
@TableName("member_info")
public class MemberInfoDO implements Serializable {
	private static final long serialVersionUID = 1L;
	
	//主键id
	private Long id;
	//限额金额数文案
	private String limitAmount;
	//刷单上限文案
	private String orderTimes;
	//手续费文案
	private String feeStr;
	//提现次文案
	private String withdrawTimes;
	//会员充值金额
	private BigDecimal amount;
	//充值类型
	private Integer type;
	//vip描述
	private String vip;
	//图片地址
	private String picUrl;
	//等级描述
	private String vipDesc;
	//购买vip条件描述
	private String buyVipCondition;
	//创建时间
	private Date createTime;
	//更新时间
	private Date updateTime;

	/**
	 * 升级会员邀请人数限制
	 */
	private Integer upLimit;
}
