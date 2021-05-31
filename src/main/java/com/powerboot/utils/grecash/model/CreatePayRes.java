package com.powerboot.utils.grecash.model;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class CreatePayRes implements Serializable {

    /**
     * 支付通道支付单ID
     */
    private String channelOrderId;

    /**
     * 支付金额
     */
    private String amount;

    /**
     * 商户用户邮箱
     */
    private String merchantUserEmail;

    /**
     * 收银台地址
     */
    private String checkPageUrl;

    /**
     * 商户订单ID
     */
    private String merchantOrderId;

    /**
     * 支付费率
     */
    private BigDecimal paytypeRate;

    /**
     * 商户结算金额
     */
    private String merchantSettlementAmount;

    /**
     * 商户ID
     */
    private String merchantId;

    /**
     * 创建时间（时间戳）
     */
    private Long createTime;

    /**
     * 国籍码
     */
    private String countryCode;

    /**
     * true表示有收银台，false没有收银台
     */
    private Boolean hadCheckPage;

    /**
     * 订单ID
     */
    private String orederId;

    /**
     * 币种代码
     */
    private String currency;

    /**
     * 支付单ID
     */
    private String id;

    /**
     * 用户手机号
     */
    private String merchantUserPhone;

    /**
     * 0处理中，1成功
     */
    private Integer status;

}
