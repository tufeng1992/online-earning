package com.powerboot.utils.theteller.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class ThetellerCreatePay implements Serializable {

    private String status;

    private Integer code;

    private String token;

    private String reason;

    private String checkoutUrl;

    private String transactionId;
}
