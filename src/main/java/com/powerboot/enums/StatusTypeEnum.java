package com.powerboot.enums;

/**
 * 充值提现状态枚举
 */
public enum StatusTypeEnum {

    TIMEOUT(-1,"超时"),
    WAIT(1,"等待"),
    SUCCESS(2,"成功"),
    FAIL(3,"失败");

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
                return singleEnum.name();
            }
        }
        return "";
    }
}
