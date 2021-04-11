package com.powerboot.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * 短信验证码表
 */
public class SmsDO implements Serializable {
	private static final long serialVersionUID = 1L;
	
	//主键id
	private Long id;
	//ip
	private String ip;
	//appid
	private String appId;
	//手机号
	private String mobile;
	//验证码
	private String verCode;
	//发送结果
	private String sendResult;
	//创建时间
	private Date createTime;
	//更新时间
	private Date updateTime;

	//提交短信时平台返回的msgid
	private String msgid;
	//DELIVRD—成功 其他失败
	private String status;

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

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	/**
	 * 设置：手机号
	 */
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	/**
	 * 获取：手机号
	 */
	public String getMobile() {
		return mobile;
	}
	/**
	 * 设置：验证码
	 */
	public void setVerCode(String verCode) {
		this.verCode = verCode;
	}
	/**
	 * 获取：验证码
	 */
	public String getVerCode() {
		return verCode;
	}
	/**
	 * 设置：发送结果
	 */
	public void setSendResult(String sendResult) {
		this.sendResult = sendResult;
	}
	/**
	 * 获取：发送结果
	 */
	public String getSendResult() {
		return sendResult;
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

	public String getMsgid() {
		return msgid;
	}

	public void setMsgid(String msgid) {
		this.msgid = msgid;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}
