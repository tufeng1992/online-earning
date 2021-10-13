package com.powerboot.domain;

import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;


/**
 * 支付回调通知记录
 * 
 * @author system
 * @email system@cc.cc
 * @date 2021-09-19 16:20:43
 */
@Data
@TableName("pay_notify")
public class PayNotifyDO implements Serializable {
	private static final long serialVersionUID = 1L;
	
	//主键id
	private Long id;
	//第三方事务id
	private String transctionId;
	//上传金额
	private BigDecimal amount;
	//支付通知状态 0：待验证、1：已验证、2：已超时、3：验证失败、4：解析格式失败
	private Integer notifyStatus;
	//支付通知信息
	private String payNotifyStr;
	//验证次数
	private int verifyCount;
	//创建时间
	private Date createTime;
	//更新时间
	private Date updateTime;
	private String phone;
}
