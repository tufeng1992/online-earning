package com.powerboot.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * 数据配置表
 */
public class DictDO implements Serializable {
	private static final long serialVersionUID = 1L;
	
	//主键id
	private Long id;
	//索引词
	private String key;
	//结果
	private String value;
	//权重
	private Integer weight;
	//创建时间
	private Date createTime;
	//更新时间
	private Date updateTime;

	/**
	 * 设置：主键id
	 */
	public void setId(Long id) {
		this.id = id;
	}
	/**
	 * 获取：主键id
	 */
	public Long getId() {
		return id;
	}
	/**
	 * 设置：索引词
	 */
	public void setKey(String key) {
		this.key = key;
	}
	/**
	 * 获取：索引词
	 */
	public String getKey() {
		return key;
	}
	/**
	 * 设置：结果
	 */
	public void setValue(String value) {
		this.value = value;
	}
	/**
	 * 获取：结果
	 */
	public String getValue() {
		return value;
	}
	/**
	 * 设置：权重
	 */
	public void setWeight(Integer weight) {
		this.weight = weight;
	}
	/**
	 * 获取：权重
	 */
	public Integer getWeight() {
		return weight;
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
