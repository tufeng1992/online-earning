package com.powerboot.utils.happylife.model;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class HappyLifeQueryPayDetailRes implements Serializable {

    /**
     * 商户号
     */
    private String identify;

    /**
     * 商户订单号
     */
    private String mer_order_no;

    /**
     * 订单金额
     */
    private BigDecimal order_amount;

    /**
     * 平台支付订单号
     */
    private String order_no;

    /**
     * 订单创建时间
     */
    private String order_time;

    /**
     * 支付时间
     */
    private String pay_time;

    /**
     * 订单状态 200:支付成功 0:未支付
     */
    private String status;

    /**
     * 签名
     */
    private String sign;

}
