package com.powerboot.domain;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;


/**
 * 留言板
 * @date 2021-05-03 15:56:09
 */
@Data
public class GuestbookDO implements Serializable {
	private static final long serialVersionUID = 1L;
	
	//主键
	private Long id;
	//留言内容
	private String guestbookContent;
	//留言人id
	private Long guestbookUserId;
	//留言对象id
	private Long guestbookTargetId;
	//留言对象手机号
	private String guestbookTargetMobile;
	//创建时间
	private Date createTime;
	//更新时间
	private Date updateTime;

	/**
	 * 是否已读
	 */
	private Boolean readed;

	/**
	 * 设置：主键
	 */
	public void setId(Long id) {
		this.id = id;
	}
	/**
	 * 获取：主键
	 */
	public Long getId() {
		return id;
	}
	/**
	 * 设置：留言内容
	 */
	public void setGuestbookContent(String guestbookContent) {
		this.guestbookContent = guestbookContent;
	}
	/**
	 * 获取：留言内容
	 */
	public String getGuestbookContent() {
		return guestbookContent;
	}
	/**
	 * 设置：留言人id
	 */
	public void setGuestbookUserId(Long guestbookUserId) {
		this.guestbookUserId = guestbookUserId;
	}
	/**
	 * 获取：留言人id
	 */
	public Long getGuestbookUserId() {
		return guestbookUserId;
	}
	/**
	 * 设置：留言对象id
	 */
	public void setGuestbookTargetId(Long guestbookTargetId) {
		this.guestbookTargetId = guestbookTargetId;
	}
	/**
	 * 获取：留言对象id
	 */
	public Long getGuestbookTargetId() {
		return guestbookTargetId;
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
