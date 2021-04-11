package com.powerboot.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 理财产品表
 *
 * @author auto_generator
 * @date 2020-10-30 23:10:02
 */
public class FinancialProductDO implements Serializable {
	private static final long serialVersionUID = 1L;
	
	//
	private Integer id;
	//理财产品名称
	private String name;
	//日利率
	private BigDecimal dayRate;
	//年利率
	private BigDecimal yearRate;
	//锁定天数
	private Integer lockDays;
	//起购金额
	private BigDecimal startAmount;
	private BigDecimal topAmount;
	private BigDecimal totalAmount;
	private Integer startVip;
	//提前赎回手续费比例
	private BigDecimal calledRate;
	//优先级
	private Integer level;
	//创建时间
	private Date createTime;
	//更新时间
	private Date updateTime;

	public Integer getStartVip() {
		return startVip;
	}

	public void setStartVip(Integer startVip) {
		this.startVip = startVip;
	}

	public BigDecimal getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}

	public BigDecimal getTopAmount() {
		return topAmount;
	}

	public void setTopAmount(BigDecimal topAmount) {
		this.topAmount = topAmount;
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
	 * 设置：理财产品名称
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * 获取：理财产品名称
	 */
	public String getName() {
		return name;
	}
	/**
	 * 设置：日利率
	 */
	public void setDayRate(BigDecimal dayRate) {
		this.dayRate = dayRate;
	}
	/**
	 * 获取：日利率
	 */
	public BigDecimal getDayRate() {
		return dayRate;
	}
	/**
	 * 设置：年利率
	 */
	public void setYearRate(BigDecimal yearRate) {
		this.yearRate = yearRate;
	}
	/**
	 * 获取：年利率
	 */
	public BigDecimal getYearRate() {
		return yearRate;
	}
	/**
	 * 设置：锁定天数
	 */
	public void setLockDays(Integer lockDays) {
		this.lockDays = lockDays;
	}
	/**
	 * 获取：锁定天数
	 */
	public Integer getLockDays() {
		return lockDays;
	}
	/**
	 * 设置：起购金额
	 */
	public void setStartAmount(BigDecimal startAmount) {
		this.startAmount = startAmount;
	}
	/**
	 * 获取：起购金额
	 */
	public BigDecimal getStartAmount() {
		return startAmount;
	}
	/**
	 * 设置：提前赎回手续费比例
	 */
	public void setCalledRate(BigDecimal calledRate) {
		this.calledRate = calledRate;
	}
	/**
	 * 获取：提前赎回手续费比例
	 */
	public BigDecimal getCalledRate() {
		return calledRate;
	}
	/**
	 * 设置：优先级
	 */
	public void setLevel(Integer level) {
		this.level = level;
	}
	/**
	 * 获取：优先级
	 */
	public Integer getLevel() {
		return level;
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
