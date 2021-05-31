package com.powerboot.utils.grecash.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class QueryPayRes implements Serializable {

    /**
     * 通道返回支付单ID，与查询时传入ID相同
     */
    private String channelOrderID;

    /**
     * 状态，0处理中，1成功，2失败
     */
    private Integer status;

    /**
     * 状态信息
     */
    private String message;

}
