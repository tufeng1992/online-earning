package com.powerboot.enums;


/**
 * 服务地区枚举
 */
public enum ServerAreaEnum {

    THAILAND("thailand","泰国"),
    NIGERIA("nigeria","尼日利亚"),
    SOUTH_AFRICA("south_africa","南非"),
    GHANA("ghana","加纳"),

    ;
    private final String code;
    private final String msg;

    ServerAreaEnum(String code, String msg) {
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
