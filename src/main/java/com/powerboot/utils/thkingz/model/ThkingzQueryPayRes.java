package com.powerboot.utils.thkingz.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class ThkingzQueryPayRes implements Serializable {

    /**
     * 商户的订单号
     */
    private String out_trade_no;

    /**
     * 订单的金额
     */
    private String amount;

    /**
     * 订单状态 2：未支付 3：订单超时 4：支付完成
     */
    private Integer status;

    /**
     * 支付时间
     */
    private String pay_time;

    /**
     * 回调状态 1 未回调 2 已回调
     */
    private Integer callback_status;

    /**
     * 回调的地址
     */
    private String callback_url;
}
