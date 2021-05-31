package com.powerboot.enums;

/**
 * Create  2021 - 01 - 11 7:42 下午
 */
public enum  PaymentServiceEnum {

    WE_GAME_PAY("1","WeGamePay"),
    PAYS_TAX_PAY("2","PaystaxPay"),
    BILL_CENTER_PAY("3","BillCenterPay"),
    RUNNING_PAY("4","RunningPay"),
    J_PAY("5","JPay"),
    EASE_BUZZ("6","Easebuzz"),
    DARAJA("10","Daraja"),
    PAY_STACK("11", "PayStack"),
    OPAY("12", "OPay"),
    FLUTTER_WAVE("13", "FlutterWave"),
    WALLYT("14", "WallytPay"),
    GRECASH("15", "GrecashPay"),
    GMS("16", "GmsPay"),
    ;

    private final String code;
    private final String beanName;

    PaymentServiceEnum(String code, String beanName) {
        this.code = code;
        this.beanName = beanName;
    }

    public String getCode() {
        return code;
    }

    public String getBeanName() {
        return beanName;
    }

    public static String getBeanNameByCode(String code){
        PaymentServiceEnum[] values = values();
        for (PaymentServiceEnum singEnum: values) {
            if (singEnum.getCode().equals(code)){
                return singEnum.getBeanName();
            }
        }
        return "";
    }

    public static PaymentServiceEnum getEnumByCode(String code){
        PaymentServiceEnum[] values = values();
        for (PaymentServiceEnum singEnum: values) {
            if (singEnum.getCode().equals(code)){
                return singEnum;
            }
        }
        return null;
    }
}
