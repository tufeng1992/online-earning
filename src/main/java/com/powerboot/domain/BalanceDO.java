package com.powerboot.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.powerboot.utils.LocalDateUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;



/**
 * 余额流水表
 * 
 * @author system
 * @email system@163.com
 * @date 2020-10-31 08:31:27
 */
@Data
@ApiModel(value = "余额流水")
public class BalanceDO implements Serializable {
	private static final long serialVersionUID = 1L;
	
	//主键id，同时为流水订单id
	private Long id;
	//外部关联订单号
	@ApiModelProperty(value = "订单号")
	private String orderNo;
	//用户id
	private Long userId;
	//变动总金额，正数代表入账，负数代表出账
	@ApiModelProperty(value = "含税金额")
	private BigDecimal amount;
	//账目类型：1、刷单佣金 2、下级分佣 3、下级分佣首冲 4、理财赎回 5、刷单金额返还 6、充值 7、提现 8、刷单支付 9、购买理财
	private Long type;
	//提现金额
	@ApiModelProperty(value = "实际提现金额")
	private BigDecimal withdrawAmount;
	//提现手续费
	@ApiModelProperty(value = "提现手续费")
	private BigDecimal serviceFee;
	//1、等待 2、成功 3、失败
	@ApiModelProperty(value = "状态1、等待 2、成功 3、失败")
	private Integer status;
	@ApiModelProperty(value = "状态描述")
	private String statusDesc;
	//创建时间
	@ApiModelProperty(value = "创建时间")
	@JsonFormat(pattern = LocalDateUtil.SIMPLE_DATE_FORMAT)
	private Date createTime;
	//更新时间
	private Date updateTime;

	//销售id
	@ApiModelProperty(value = "销售id")
	private Long saleId;

	/**
	 * 与订单相关用户id
	 */
	private Long relationUserId;

	@ApiModelProperty(value = "三方请求返回")
	private String thirdResponse;

	//审批信息
	private String remark;

	public Long getSaleId() {
		return saleId;
	}

	public void setSaleId(Long saleId) {
		this.saleId = saleId;
	}

	public String getStatusDesc() {
		return statusDesc;
	}

	public void setStatusDesc(String statusDesc) {
		this.statusDesc = statusDesc;
	}

	/**
	 * 设置：主键id，同时为流水订单id
	 */
	public void setId(Long id) {
		this.id = id;
	}
	/**
	 * 获取：主键id，同时为流水订单id
	 */
	public Long getId() {
		return id;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	/**
	 * 设置：用户id
	 */
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	/**
	 * 获取：用户id
	 */
	public Long getUserId() {
		return userId;
	}
	/**
	 * 设置：变动总金额，正数代表入账，负数代表出账
	 */
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	/**
	 * 获取：变动总金额，正数代表入账，负数代表出账
	 */
	public BigDecimal getAmount() {
		return amount;
	}
	/**
	 * 设置：账目类型：1、注册奖励 2、刷单佣金 3、下级分佣 4、下级分佣首冲 5、理财赎回 6、刷单金额返还 7、充值 8、提现 9、刷单支付 10、购买理财
	 */
	public void setType(Long type) {
		this.type = type;
	}
	/**
	 * 获取：账目类型：1、注册奖励 2、刷单佣金 3、下级分佣 4、下级分佣首冲 5、理财赎回 6、刷单金额返还 7、充值 8、提现 9、刷单支付 10、购买理财
	 */
	public Long getType() {
		return type;
	}
	/**
	 * 设置：提现金额
	 */
	public void setWithdrawAmount(BigDecimal withdrawAmount) {
		this.withdrawAmount = withdrawAmount;
	}
	/**
	 * 获取：提现金额
	 */
	public BigDecimal getWithdrawAmount() {
		return withdrawAmount;
	}
	/**
	 * 设置：提现手续费
	 */
	public void setServiceFee(BigDecimal serviceFee) {
		this.serviceFee = serviceFee;
	}
	/**
	 * 获取：提现手续费
	 */
	public BigDecimal getServiceFee() {
		return serviceFee;
	}
	/**
	 * 设置：1、等待 2、成功 3、失败
	 */
	public void setStatus(Integer status) {
		this.status = status;
	}
	/**
	 * 获取：1、等待 2、成功 3、失败
	 */
	public Integer getStatus() {
		return status;
	}
	/**
	 * 设置：创建时间
	 */
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	/**
	 * 获取：创建时间
	 */
	public Date getCreateTime() {
		return createTime;
	}
	/**
	 * 设置：更新时间
	 */
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	/**
	 * 获取：更新时间
	 */
	public Date getUpdateTime() {
		return updateTime;
	}
}
