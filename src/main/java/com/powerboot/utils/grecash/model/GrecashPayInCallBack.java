package com.powerboot.utils.grecash.model;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

/**
 * @Date: 2021/4/14 14:31
 */
@Data
@ApiModel("grecash支付回调")
public class GrecashPayInCallBack implements Serializable {

    /**
     * 平台订单号
     */
    private String orderId;

    /**
     * 平台支付单号
     */
    private String payorderId;

    /**
     * 商户订单号
     */
    private String merchantOrderId;


    /**
     * 状态
     * 0处理中，1成功，2失败
     */
    private Integer status;

    /**
     * 签名
     */
    private String sign;

}
