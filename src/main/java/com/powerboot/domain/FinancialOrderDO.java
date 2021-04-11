package com.powerboot.domain;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 理财订单表
 *
 * @author auto_generator
 * @date 2020-10-30 23:10:01
 */
public class FinancialOrderDO implements Serializable {
	private static final long serialVersionUID = 1L;
	
	//
	private Integer id;
	//用户id
	private Integer userId;
	//理财产品id
	private Integer productId;
	//状态 1-未到期 2-已到期 3-提前赎回
	private Integer orderStatus;
	//本金
	private BigDecimal amount;
	//购买日期
	private Date buyDate;
	//理财结束时间
	private Date lastDate;
	//每日利息
	private BigDecimal dayInterest;
	//总利息
	private BigDecimal totalInterest;
	//提前退出手续费
	private BigDecimal calledAmount;
	//提前退出时间
	private Date calledTime;
	//创建时间
	private Date createTime;
	//更新时间
	private Date updateTime;

	private String productName;

	private BigDecimal dayRate;

	private BigDecimal yearRate;

	private Integer lockDays;

	private BigDecimal calledRate;

	//销售id
	@ApiModelProperty(value = "销售id")
	private Long saleId;

	public Long getSaleId() {
		return saleId;
	}

	public void setSaleId(Long saleId) {
		this.saleId = saleId;
	}

	/**
	 * 设置：
	 */
	public void setId(Integer id) {
		this.id = id;
	}
	/**
	 * 获取：
	 */
	public Integer getId() {
		return id;
	}
	/**
	 * 设置：用户id
	 */
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	/**
	 * 获取：用户id
	 */
	public Integer getUserId() {
		return userId;
	}
	/**
	 * 设置：理财产品id
	 */
	public void setProductId(Integer productId) {
		this.productId = productId;
	}
	/**
	 * 获取：理财产品id
	 */
	public Integer getProductId() {
		return productId;
	}
	/**
	 * 设置：状态 1-未到期 2-已到期 3-提前赎回
	 */
	public void setOrderStatus(Integer orderStatus) {
		this.orderStatus = orderStatus;
	}
	/**
	 * 获取：状态 1-未到期 2-已到期 3-提前赎回
	 */
	public Integer getOrderStatus() {
		return orderStatus;
	}
	/**
	 * 设置：本金
	 */
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	/**
	 * 获取：本金
	 */
	public BigDecimal getAmount() {
		return amount;
	}
	/**
	 * 设置：购买日期
	 */
	public void setBuyDate(Date buyDate) {
		this.buyDate = buyDate;
	}
	/**
	 * 获取：购买日期
	 */
	public Date getBuyDate() {
		return buyDate;
	}
	/**
	 * 设置：理财结束时间
	 */
	public void setLastDate(Date lastDate) {
		this.lastDate = lastDate;
	}
	/**
	 * 获取：理财结束时间
	 */
	public Date getLastDate() {
		return lastDate;
	}
	/**
	 * 设置：每日利息
	 */
	public void setDayInterest(BigDecimal dayInterest) {
		this.dayInterest = dayInterest;
	}
	/**
	 * 获取：每日利息
	 */
	public BigDecimal getDayInterest() {
		return dayInterest;
	}
	/**
	 * 设置：总利息
	 */
	public void setTotalInterest(BigDecimal totalInterest) {
		this.totalInterest = totalInterest;
	}
	/**
	 * 获取：总利息
	 */
	public BigDecimal getTotalInterest() {
		return totalInterest;
	}
	/**
	 * 设置：提前退出手续费
	 */
	public void setCalledAmount(BigDecimal calledAmount) {
		this.calledAmount = calledAmount;
	}
	/**
	 * 获取：提前退出手续费
	 */
	public BigDecimal getCalledAmount() {
		return calledAmount;
	}
	/**
	 * 设置：提前退出时间
	 */
	public void setCalledTime(Date calledTime) {
		this.calledTime = calledTime;
	}
	/**
	 * 获取：提前退出时间
	 */
	public Date getCalledTime() {
		return calledTime;
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

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public BigDecimal getDayRate() {
		return dayRate;
	}

	public void setDayRate(BigDecimal dayRate) {
		this.dayRate = dayRate;
	}

	public BigDecimal getYearRate() {
		return yearRate;
	}

	public void setYearRate(BigDecimal yearRate) {
		this.yearRate = yearRate;
	}

	public Integer getLockDays() {
		return lockDays;
	}

	public void setLockDays(Integer lockDays) {
		this.lockDays = lockDays;
	}

	public BigDecimal getCalledRate() {
		return calledRate;
	}

	public void setCalledRate(BigDecimal calledRate) {
		this.calledRate = calledRate;
	}
}
