package com.powerboot.request.payment;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

/**
 * @Date: 2021/4/14 14:31
 */
@Data
@ApiModel("flutter支付回调")
public class FlutterPayInCallBack implements Serializable {

    @NotEmpty(message = "tx_ref cloud not empty")
    private String tx_ref;

    @NotEmpty(message = "transaction_id cloud not empty")
    private String transaction_id;

    @NotEmpty(message = "status cloud not empty")
    private String status;
}
