package com.powerboot.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;



/**
 * 刷单商品表
 * 
 * @author system
 * @email system@163.com
 * @date 2020-10-31 11:42:23
 */
public class ProductDO implements Serializable {
	private static final long serialVersionUID = 1L;
	
	//
	private Long id;
	//商品名称
	private String name;
	//商品图片
	private String picture;
	//商品价格
	private BigDecimal price;
	//商品价格隐藏价格（后台判断用）
	private BigDecimal originPrice;
	//商品描述
	private String describtion;
	//商品评价
	private String comment;
	//返现价格（佣金+商品价格）
	private BigDecimal returnPrice;
	//余额等级（对应 1-2-3-4）
	private Integer level;
	//排序优先级 1开始，倒序排序
	private Integer sort;
	//类型 1、首页商品  2、刷单商品
	private Integer type;
	//状态 0、无效  1、有效
	private Integer status;
	//创建时间
	private Date createTime;
	//更新时间
	private Date updateTime;

	public BigDecimal getOriginPrice() {
		return originPrice;
	}

	public void setOriginPrice(BigDecimal originPrice) {
		this.originPrice = originPrice;
	}

	/**
	 * 设置：
	 */
	public void setId(Long id) {
		this.id = id;
	}
	/**
	 * 获取：
	 */
	public Long getId() {
		return id;
	}
	/**
	 * 设置：商品名称
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * 获取：商品名称
	 */
	public String getName() {
		return name;
	}
	/**
	 * 设置：商品图片
	 */
	public void setPicture(String picture) {
		this.picture = picture;
	}
	/**
	 * 获取：商品图片
	 */
	public String getPicture() {
		return picture;
	}
	/**
	 * 设置：商品价格
	 */
	public void setPrice(BigDecimal price) {
		this.price = price;
	}
	/**
	 * 获取：商品价格
	 */
	public BigDecimal getPrice() {
		return price;
	}
	/**
	 * 设置：商品描述
	 */
	public void setDescribtion(String describtion) {
		this.describtion = describtion;
	}
	/**
	 * 获取：商品描述
	 */
	public String getDescribtion() {
		return describtion;
	}
	/**
	 * 设置：商品评价
	 */
	public void setComment(String comment) {
		this.comment = comment;
	}
	/**
	 * 获取：商品评价
	 */
	public String getComment() {
		return comment;
	}
	/**
	 * 设置：返现价格（佣金+商品价格）
	 */
	public void setReturnPrice(BigDecimal returnPrice) {
		this.returnPrice = returnPrice;
	}
	/**
	 * 获取：返现价格（佣金+商品价格）
	 */
	public BigDecimal getReturnPrice() {
		return returnPrice;
	}
	/**
	 * 设置：余额等级（对应 1-2-3-4）
	 */
	public void setLevel(Integer level) {
		this.level = level;
	}
	/**
	 * 获取：余额等级（对应 1-2-3-4）
	 */
	public Integer getLevel() {
		return level;
	}
	/**
	 * 设置：排序优先级 1开始，倒序排序
	 */
	public void setSort(Integer sort) {
		this.sort = sort;
	}
	/**
	 * 获取：排序优先级 1开始，倒序排序
	 */
	public Integer getSort() {
		return sort;
	}
	/**
	 * 设置：类型 1、首页商品  2、刷单商品
	 */
	public void setType(Integer type) {
		this.type = type;
	}
	/**
	 * 获取：类型 1、首页商品  2、刷单商品
	 */
	public Integer getType() {
		return type;
	}
	/**
	 * 设置：状态 0、无效  1、有效
	 */
	public void setStatus(Integer status) {
		this.status = status;
	}
	/**
	 * 获取：状态 0、无效  1、有效
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
