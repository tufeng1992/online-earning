package com.powerboot.utils.thkingz.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class ThkingzQueryPayOutRes implements Serializable {

    /**
     * 新账户余额
     */
    private String new_amount;

    /**
     * 子账户号
     */
    private String account_number;

    /**
     * 户主名字
     */
    private String account_name;

    /**
     * 原账户余额
     */
    private String old_amount;

    /**
     * 回调的地址
     */
    private String callback_url;

    /**
     * 订单申请时间
     */
    private String apply_time;

    /**
     * 订单处理时间
     */
    private String deal_time;

    /**
     * 银行编码
     */
    private Integer bank_id;

    /**
     * 订单状态
     */
    private Integer status;

    /**
     * 支付类型
     */
    private String bank_type;

}
