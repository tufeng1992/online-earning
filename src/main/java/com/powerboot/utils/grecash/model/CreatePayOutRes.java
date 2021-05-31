package com.powerboot.utils.grecash.model;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class CreatePayOutRes implements Serializable {

    /**
     * 订单ID
     */
    private String id;

    /**
     * 商户ID
     */
    private String merchantId;

    /**
     * 商户订单号
     */
    private String merchantPayoutId;

    /**
     * 业务类型，1提现，2代付
      */
    private Integer bizType;

    /**
     * 金额
     */
    private String amount;

    /**
     * 通道ID
     */
    private String channelId;

    /**
     * 国家码
     */
    private String countryCode;

    /**
     * 货币代码
     */
    private String currency;

    /**
     * 付款方式，1电子钱包付款，2银行付款方式
     */
    private Integer payType;

    /**
     * 费率
     */
    private Double rate;

    /**
     * 单笔费用
     */
    private Integer singleCharge;

    /**
     * 扣费方式，0内扣，1外扣
     */
    private Integer deductionMethod;

    /**
     * 状态，0审批中，1支付完成，2审核不通过，3审核通过，4支付处理中，5支付处理失败
     */
    private Integer status;

    /**
     * 审核人
     */
    private String approver;

    /**
     * 创建时间
      */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

}
