package com.powerboot.enums;

public enum InviteUserStatusEnum {

    //1-已注册 2-完成首充
    REG(1,"已注册"),
    RECHARGE(2,"完成首充");

    private final Integer code;
    private final String msg;

    InviteUserStatusEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public static String getMsgByCode(Integer code){
        InviteUserStatusEnum[] numsarray = InviteUserStatusEnum.values();
        for(InviteUserStatusEnum inviteUserStatusEnum :numsarray){
            if(inviteUserStatusEnum.getCode().intValue()==code.intValue()){
                return inviteUserStatusEnum.getMsg();
            }
        }
        return null;
    }
}
