package com.powerboot.utils.thkingz.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class ThkingzCreatePayOutRes implements Serializable {

    /**
     * 回调地址
     */
    private String callback_url;

    /**
     * 交易金额
     */
    private String amount;

    /**
     * 平台单号
     */
    private String order_no;

    /**
     * 商户订单号
     */
    private String out_trade_no;

    /**
     * 订单状态 1 是完成；2是等待付款中；3是系统驳回
     */
    private Integer status;

    private String appid;

    private String account;

    private String bank_type;

    private String money;

    private String sign;


}
