package com.powerboot.utils.grecash.model;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class PayOutNotifyReq implements Serializable {

    /**
     * 付款单详情记录ID
     */
    private String id;

    /**
     * 商户ID
     */
    private String merchantId;

    /**
     * 付款单ID
     */
    private String payoutId;

    /**
     * 商户侧代付单号
     */
    private String merchantPayoutId;

    /**
     * 国家
      */
    private String countryCode;

    /**
     * 币种
     */
    private String currency;

    /**
     * 收款账户
     */
    private String payeeAccount;

    /**
     * 收款人名字
     */
    private String payeeName;

    /**
     * 收款人手机号
     */
    private String phone;

    /**
     * 收款人邮箱
     */
    private String email;

    /**
     * 收款人身份证号
     */
    private String idCardNum;

    /**
     * 银行编码
     */
    private String payeeBankCode;

    /**
     * 电子钱包ID
     */
    private String walletId;

    /**
     * IFSC银行编码
     */
    private String ifsc;

    /**
     * 支付渠道ID
     */
    private String channelId;

    /**
     * 本笔支付金额
     */
    private String amount;

    /**
     * 状态，0等待处理中，1处理成功，2失败
     */
    private String status;

    /**
     * 签名
     */
    private String sign;

    /**
     * 商户侧自定义流水号
     */
    private String referenceID;

    /**
     * 备注
     */
    private String comment;

    /**
     * 创建时间
     */
    private String createTime;

    /**
     * 更新时间
     */
    private String updateTime;

}
