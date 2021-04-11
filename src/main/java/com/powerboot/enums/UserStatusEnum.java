package com.powerboot.enums;

public enum UserStatusEnum {

    NEW(1,"新建"),
    FACE(2,"人脸完成"),
    BASIC(3,"基础信息完成"),
    BANK(4,"银行信息完成"),
    PAY(5,"支付成功"),
    ;

    private final Integer code;
    private final String msg;

    UserStatusEnum(Integer code, String msg) {
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
