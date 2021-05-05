package com.powerboot.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;


@AllArgsConstructor
public enum  ErrorEnums {

    MOBILE_REGISTERED("1001", "This phone number has been registered,please try again!"),
    ;

    @Getter
    private String code;

    @Getter
    private String msg;


}
