package com.powerboot.utils.opay;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

/**
 * CreatedDate: 2020/12/15
 */
public enum PayResult {
    INITIAL("INITIAL"),
    PENDING("PENDING"),
    SUCCESS("SUCCESS"),
    FAIL("FAIL"),
    INPUT_PIN("INPUT-PIN"),
    INPUT_OTP("INPUT-OTP"),
    INPUT_PHONE("INPUT-PHONE"),
    WEB_AUTH("3DSECURE"),
    CLOSE("CLOSE");
    private String state;

    PayResult(String state) {
        this.state = state;
    }

    public static PayResult find(String source) {
        return Arrays.stream(PayResult.values())
                .filter(it -> StringUtils.equals(source, it.state))
                .findFirst().orElse(null);
    }
}
