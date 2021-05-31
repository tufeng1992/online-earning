package com.powerboot.utils.gms.model;

import lombok.Data;

import java.io.Serializable;

/**
 * gms 转账返回
 */
@Data
public class GmsCreatePayOutRes implements Serializable {

    /**
     * 响应状态 SUCCESS：响应成功
     *     FAIL:响应失败
     */
    private String respCode;

    /**
     * 响应失败原因
     */
    private String errorMsg;

    /**
     * 签名方式
     */
    private String signType;

    /**
     * 签名
     */
    private String sign;

    /**
     * 商户代码
     */
    private String mchId;

    /**
     * 商家转账单号
     */
    private String merTransferId;

    /**
     * 转账金额
     */
    private String transferAmount;

    /**
     * 订单时间
     */
    private String applyDate;

    /**
     * 平台转账单号
     */
    private String tradeNo;

    /**
     * 是否转账成功状态
     */
    private String tradeResult;

}
