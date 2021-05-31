package com.powerboot.utils.gms.model;

import lombok.Data;

import java.io.Serializable;

/**
 * gms支付回调
 */
@Data
public class GmsPayInNotify implements Serializable {

    /**
     * 订单状态  1：支付成功
     */
    private String tradeResult;

    /**
     * 实际金额
     */
    private String oriAmount;

    /**
     * 订单金额
     */
    private String amount;

    /**
     * 商户号
     */
    private String mchId;

    /**
     * 平台支付订单号
     */
    private String orderNo;

    /**
     * 商家订单号
     */
    private String mchOrderNo;

    /**
     * 签名
     */
    private String sign;

    /**
     * 签名方式
     */
    private String signType;

    /**
     * 订单时间
     */
    private String orderDate;

}
