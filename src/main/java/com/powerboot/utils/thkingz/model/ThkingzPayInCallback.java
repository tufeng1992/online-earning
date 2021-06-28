package com.powerboot.utils.thkingz.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class ThkingzPayInCallback implements Serializable {

    /**
     * 状态值 ： CODE_SUCCESS 成功， CODE_FAILURE 失败
     */
    private String callbacks;

    /**
     * 您的商户唯一标识
     */
    private String appid;

    /**
     * 支付方式：ThreeScbCode(泰国扫码)
     */
    private String pay_type;

    /**
     * 成功返回地址
     */
    private String success_url;

    /**
     * 错误返回地址
     */
    private String error_url;

    /**
     * 商户订单号
     */
    private String out_trade_no;

    /**
     * 交易金额，订单真实金额
     */
    private String amount;

    /**
     * 实付金额，要求玩家实际支付金额
     */
    private String amount_true;

    /**
     * 用户网站的请求支付用户信息，可以是帐号也可以是数据库的ID
     */
    private String out_uid;

    /**
     * 签名算法
     */
    private String sign;

}
