package com.powerboot.enums;

/**
 * @author benny
 * Create  2020 - 10 - 29 4:19 下午
 */
public enum UserRoleEnum {
    SALE(1,"销售"),
    PUBLIC(2,"普通用户");

    private final Integer code;
    private final String msg;

    UserRoleEnum(Integer code, String msg) {
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
