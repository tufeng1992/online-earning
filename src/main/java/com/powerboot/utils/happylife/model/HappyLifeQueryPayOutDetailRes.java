package com.powerboot.utils.happylife.model;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class HappyLifeQueryPayOutDetailRes implements Serializable {

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
    private BigDecimal transfer_money;

    /**
     * 手续费
     */
    private BigDecimal fee_money;

    /**
     * 平台支付订单号
     */
    private String platform_no;

    /**
     * 订单创建时间
     */
    private String createtime;

    /**
     * 200:代付成功
     */
    private String status;

    /**
     * 签名
     */
    private String sign;
}
