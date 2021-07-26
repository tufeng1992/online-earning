package com.powerboot.utils.qeapay.domain;

import lombok.Data;

import java.io.Serializable;

/**
 * qeapay支付返回信息
 */
@Data
public class QeaPayCreatePayRes implements Serializable {

    /**
     * 响应状态 SUCCESS：响应成功
     * FAIL:响应失败
     */
    private String respCode;

    /**
     * 响应失败原因 响应 成功时为   request
     * success
     */
    private String tradeMsg;

    /**
     * 下单状态  1下单成功，其他失败
     */
    private String tradeResult;

    /**
     * 实际金额
     */
    private String oriAmount;

    /**
     * 订单金额
     */
    private String tradeAmount;

    /**
     * 商户号
     */
    private String mchId;

    /**
     * 平台转账单号
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

    /**
     * 付款链接
     */
    private String payInfo;

}
