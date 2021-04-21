package com.powerboot.request.payment;

import com.fasterxml.jackson.annotation.JsonAlias;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.Map;

/**
 * @Date: 2021/4/14 14:31
 */
@Data
@ApiModel("flutter支付回调webhook")
public class FlutterPayInWebhook implements Serializable {

    private String event;

    @JsonAlias("event.type")
    private String eventType;

    private Map<String, Object> data;
}
