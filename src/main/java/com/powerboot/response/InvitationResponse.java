package com.powerboot.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * Create  2020 - 10 - 30 11:40 上午
 */
@ApiModel(value = "邀请码返回")
public class InvitationResponse implements Serializable {
    @ApiModelProperty(value = "邀请码")
    private String referralCode;
    @ApiModelProperty(value = "邀请前缀链接URL")
    private String invitationPreUrl;
    @ApiModelProperty(value = "等级比例 , 隔开")
    private String levelRatio;

    public String getReferralCode() {
        return referralCode;
    }

    public void setReferralCode(String referralCode) {
        this.referralCode = referralCode;
    }

    public String getInvitationPreUrl() {
        return invitationPreUrl;
    }

    public void setInvitationPreUrl(String invitationPreUrl) {
        this.invitationPreUrl = invitationPreUrl;
    }

    public String getLevelRatio() {
        return levelRatio;
    }

    public void setLevelRatio(String levelRatio) {
        this.levelRatio = levelRatio;
    }
}
