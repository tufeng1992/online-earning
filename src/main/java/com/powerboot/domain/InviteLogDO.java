package com.powerboot.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;


/**
 * 邀请记录表
 *
 */
public class InviteLogDO implements Serializable {

    private static final long serialVersionUID = 1L;

    //id
    private Integer id;
    //邀请人id
    private Integer inviteUserId;
    //被邀请人id
    private Integer newUserId;
    //状态 1-已注册 2-完成首充
    private Integer newUserStatus;
    //邀请人获取奖励
    private BigDecimal inviteAmount;
    //被邀请人注册时间
    private Date regDate;
    //被邀请人首充时间
    private Date firstRechargeDate;
    //创建时间
    private Date createTime;
    //更新时间
    private Date updateTime;

    /**
     * 获取：id
     */
    public Integer getId() {
        return id;
    }

    /**
     * 设置：id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取：邀请人id
     */
    public Integer getInviteUserId() {
        return inviteUserId;
    }

    /**
     * 设置：邀请人id
     */
    public void setInviteUserId(Integer inviteUserId) {
        this.inviteUserId = inviteUserId;
    }

    /**
     * 获取：被邀请人id
     */
    public Integer getNewUserId() {
        return newUserId;
    }

    /**
     * 设置：被邀请人id
     */
    public void setNewUserId(Integer newUserId) {
        this.newUserId = newUserId;
    }

    /**
     * 获取：状态 1-已注册 2-完成首充
     */
    public Integer getNewUserStatus() {
        return newUserStatus;
    }

    /**
     * 设置：状态 1-已注册 2-完成首充
     */
    public void setNewUserStatus(Integer newUserStatus) {
        this.newUserStatus = newUserStatus;
    }

    /**
     * 获取：邀请人获取奖励
     */
    public BigDecimal getInviteAmount() {
        return inviteAmount;
    }

    /**
     * 设置：邀请人获取奖励
     */
    public void setInviteAmount(BigDecimal inviteAmount) {
        this.inviteAmount = inviteAmount;
    }

    /**
     * 获取：被邀请人注册时间
     */
    public Date getRegDate() {
        return regDate;
    }

    /**
     * 设置：被邀请人注册时间
     */
    public void setRegDate(Date regDate) {
        this.regDate = regDate;
    }

    /**
     * 获取：被邀请人首充时间
     */
    public Date getFirstRechargeDate() {
        return firstRechargeDate;
    }

    /**
     * 设置：被邀请人首充时间
     */
    public void setFirstRechargeDate(Date firstRechargeDate) {
        this.firstRechargeDate = firstRechargeDate;
    }

    /**
     * 获取：创建时间
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * 设置：创建时间
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * 获取：更新时间
     */
    public Date getUpdateTime() {
        return updateTime;
    }

    /**
     * 设置：更新时间
     */
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}
