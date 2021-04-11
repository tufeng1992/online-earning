package com.powerboot.enums;


public enum ResultEnum {
    SYSTEM_EXCEPTION("000002","system_error"),
    UNKNOWN_EXCEPTION("000003","unknown_exception");
    private final String code;
    private final String msg;

    ResultEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
