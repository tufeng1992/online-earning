package com.powerboot.enums;

import com.powerboot.utils.MessageUtils;

/**
 * 充值提现状态枚举
 */
public enum StatusTypeEnum {

    TIMEOUT(-1,"STAUS_TYPE_ENUM_TIMEOUT"),
    WAIT(1,"STAUS_TYPE_ENUM_WAIT"),
    SUCCESS(2,"STAUS_TYPE_ENUM_SUCCESS"),
    FAIL(3,"STAUS_TYPE_ENUM_FAIL");

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
                return MessageUtils.message(singleEnum.msg);
            }
        }
        return "";
    }
}
