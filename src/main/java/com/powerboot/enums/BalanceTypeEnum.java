package com.powerboot.enums;
/**
 * 余额账目类型枚举
 */
public enum BalanceTypeEnum {

    A(1L,"刷单佣金"),
    B(2L,"下级分佣"),
    C(3L,"下级分佣首冲"),
    D(4L,"理财赎回"),
    E(5L,"刷单金额返还"),
    F(6L,"充值"),
    G(7L,"提现"),
    H(8L,"刷单支付"),
    I(9L,"购买理财"),
    J(10L,"提现失败金额返还"),
    K(11L,"系统人工增加余额"),
    L(12L,"系统人工减少余额"),
    M(13L,"注册活动赠送"),
    N(14L,"下级注册活动赠送");

    private final Long code;
    private final String msg;

    BalanceTypeEnum(Long code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Long getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
