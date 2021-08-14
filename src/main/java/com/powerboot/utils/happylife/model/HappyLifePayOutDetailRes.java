package com.powerboot.utils.happylife.model;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class HappyLifePayOutDetailRes implements Serializable {

    /**
     * 商户号
     */
    private String identify;

    /**
     * 商户订单号
     */
    private String order_no;

    /**
     * 订单金额
     */
    private BigDecimal money;

    /**
     * 平台支付订单号
     */
    private String platform_no;

    /**
     * 订单创建时间
     */
    private String add_time;

    /**
     * 订单状态 pending:处理中
     */
    private String status;

    /**
     * 签名
     */
    private String sign;
}
