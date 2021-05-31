package com.powerboot.utils.wallyt.domain.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class QueryTransferResponse extends WallyBaseResponseObject implements Serializable {


    /**
     * 订单号
     */
    private String msgId;

    /**
     * 外部系统订单号
     */
    private String originalMsgId;

    /**
     * 商户id
     */
    private String partnerId;

    /**
     * 交易金额
     */
    private BigDecimal trxAmount;

    /**
     * 交易时间
     */
    private String trxDate;

    /**
     * 交易状态
     */
    private String trxStatus;

    /**
     * 货币类型
     */
    private String currency;

    /**
     * 备注
     */
    private String attach;

}
