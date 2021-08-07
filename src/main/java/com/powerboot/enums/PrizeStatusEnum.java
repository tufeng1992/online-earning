package com.powerboot.enums;

public enum PrizeStatusEnum {
    //奖励来源 1：下级用户首充、2：邀请好友、3：完成任务
    CHARGE(1,"下级用户首充"),
    INVITE(2,"邀请好友"),
    MISSION(3,"完成任务"),
    //奖励状态 1：未使用、2：已使用、3：已过期
    STATUS_INIT(1, "未使用"),
    STATUS_USED(2, "已使用"),
    STATUS_EXPIRE(3, "已过期"),
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
