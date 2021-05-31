package com.powerboot.utils.wallyt.domain.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 转账响应
 */
@Data
public class CreateTransferResponse extends WallyBaseResponseObject implements Serializable {

    /**
     * 订单号
     */
    private String msgId;

    /**
     * 交易金额
     */
    private BigDecimal trxAmount;

    /**
     * 商户id
     */
    private String partnerId;

    /**
     * 交易时间
     */
    private String trxDate;

    /**
     * 备注
     */
    private String attach;



}
