package com.powerboot.enums;

/**
 * 充值提现状态枚举
 */
public enum StatusTypeEnum {

    TIMEOUT(-1,"หมดเวลา"),
    WAIT(1,"เดี๋ยวก่อน"),
    SUCCESS(2,"สำเร็จ"),
    FAIL(3,"ล้มเหลว");

    private final Integer code;
    private final String msg;

    StatusTypeEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public static String getENDescByCode(Integer code){
        for (StatusTypeEnum singleEnum: values()) {
            if (singleEnum.code.equals(code)){
                return singleEnum.msg;
            }
        }
        return "";
    }
}
