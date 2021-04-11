package com.powerboot.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * 登录日志表
 */
public class LoginLogDO implements Serializable {
	private static final long serialVersionUID = 1L;
	
	//主键id
	private Long id;
	//用户id
	private Long userId;
	//手机号
	private String mobile;
	//APP id
	private String appId;
	//是否第一次登录 1-第一次登录 2-非第一次
	private Integer firstLogin;
	//IP
	private String ip;
	//设备号
	private String deviceNumber;
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
	 * 设置：APP id
	 */
	public void setAppId(String appId) {
		this.appId = appId;
	}
	/**
	 * 获取：APP id
	 */
	public String getAppId() {
		return appId;
	}
	/**
	 * 设置：是否第一次登录 1-第一次登录 2-非第一次
	 */
	public void setFirstLogin(Integer firstLogin) {
		this.firstLogin = firstLogin;
	}
	/**
	 * 获取：是否第一次登录 1-第一次登录 2-非第一次
	 */
	public Integer getFirstLogin() {
		return firstLogin;
	}
	/**
	 * 设置：IP
	 */
	public void setIp(String ip) {
		this.ip = ip;
	}
	/**
	 * 获取：IP
	 */
	public String getIp() {
		return ip;
	}
	/**
	 * 设置：设备号
	 */
	public void setDeviceNumber(String deviceNumber) {
		this.deviceNumber = deviceNumber;
	}
	/**
	 * 获取：设备号
	 */
	public String getDeviceNumber() {
		return deviceNumber;
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
