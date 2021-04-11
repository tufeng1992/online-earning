package com.powerboot.response;

import io.swagger.annotations.ApiModelProperty;
import java.util.List;

public class InviteFriendResponse {

    @ApiModelProperty(value = "顶部第一模块描述")
    private List<String> topOneList;

    @ApiModelProperty(value = "顶部第二模块描述")
    private List<String> topTwoList;

    @ApiModelProperty(value = "表格描述，每行一个字符串，字符串中列用|分隔")
    private List<String> vipTable;

    @ApiModelProperty(value = "邀请码")
    private String referralCode;

    @ApiModelProperty(value = "邀请链接URL")
    private String invitationUrl;

    @ApiModelProperty(value = "底部模块描述")
    private List<String> bottomList;

    public List<String> getTopOneList() {
        return topOneList;
    }

    public void setTopOneList(List<String> topOneList) {
        this.topOneList = topOneList;
    }

    public List<String> getTopTwoList() {
        return topTwoList;
    }

    public void setTopTwoList(List<String> topTwoList) {
        this.topTwoList = topTwoList;
    }

    public List<String> getVipTable() {
        return vipTable;
    }

    public void setVipTable(List<String> vipTable) {
        this.vipTable = vipTable;
    }

    public String getReferralCode() {
        return referralCode;
    }

    public void setReferralCode(String referralCode) {
        this.referralCode = referralCode;
    }

    public String getInvitationUrl() {
        return invitationUrl;
    }

    public void setInvitationUrl(String invitationUrl) {
        this.invitationUrl = invitationUrl;
    }

    public List<String> getBottomList() {
        return bottomList;
    }

    public void setBottomList(List<String> bottomList) {
        this.bottomList = bottomList;
    }
}
