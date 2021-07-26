package com.powerboot.enums;

public enum PrizeStatusEnum {
    //奖励来源 1：下级用户首充、2：邀请好友、3：完成任务
    CHARGE(1,"下级用户首充"),
    INVITE(2,"邀请好友"),
    MISSION(3,"完成任务"),
    ;

    private final Integer code;
    private final String msg;

    PrizeStatusEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
