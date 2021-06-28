package com.powerboot.utils.sepro.model;

import lombok.Data;

import java.io.Serializable;

/**
 * sepro代付回调
 */
@Data
public class SeproPayOutNotify implements Serializable {

    /**
     * 订单状态  1：代付成功 2：代付失败
     */
    private String tradeResult;

    /**
     * 回调状态 默认SUCCESS
     */
    private String respCode;

    /**
     * 代付金额
     */
    private String transferAmount;

    /**
     * 商户代码
     */
    private String merNo;

    /**
     * 平台订单号
     */
    private String tradeNo;

    /**
     * 商家转账单号
     */
    private String merTransferId;

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
    private String applyDate;

    /**
     * 版本号
     */
    private String version;

}
