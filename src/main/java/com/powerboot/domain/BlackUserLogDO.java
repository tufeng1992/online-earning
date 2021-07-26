package com.powerboot.domain;

import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;


/**
 * 用户拉黑记录表
 * 
 * @author system
 * @email system@cc.cc
 * @date 2021-07-16 21:28:49
 */
@Data
@TableName("black_user_log")
public class BlackUserLogDO implements Serializable {
	private static final long serialVersionUID = 1L;
	
	//主键id
	private Long id;
	//拉黑用户id
	private Long blackUserId;
	//拉黑原因
	private String blackReason;
	//操作人
	private String operUser;
	//创建时间
	private Date createTime;
	//更新时间
	private Date updateTime;
	//销售人员id
	private Long saleId;

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
	 * 设置：拉黑用户id
	 */
	public void setBlackUserId(Long blackUserId) {
		this.blackUserId = blackUserId;
	}
	/**
	 * 获取：拉黑用户id
	 */
	public Long getBlackUserId() {
		return blackUserId;
	}
	/**
	 * 设置：拉黑原因
	 */
	public void setBlackReason(String blackReason) {
		this.blackReason = blackReason;
	}
	/**
	 * 获取：拉黑原因
	 */
	public String getBlackReason() {
		return blackReason;
	}
	/**
	 * 设置：
	 */
	public void setOperUser(String operUser) {
		this.operUser = operUser;
	}
	/**
	 * 获取：
	 */
	public String getOperUser() {
		return operUser;
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
