package com.powerboot.enums;


public enum PaymentChannelEnum {

    PAY_THREE("2", "rzp apk跳转支付"),
    PAY_FOUR("1", "跳转支付链接支付");
    private final String code;
    private final String beanName;

    PaymentChannelEnum(String code, String beanName) {
        this.code = code;
        this.beanName = beanName;
    }

    public String getCode() {
        return code;
    }

    public String getBeanName() {
        return beanName;
    }

    public static String getPayNameByCode(String code) {
        PaymentChannelEnum[] values = values();
        for (PaymentChannelEnum singEnum : values) {
            if (singEnum.getCode().equals(code)) {
                return singEnum.getBeanName();
            }
        }
        return "";
    }
}
