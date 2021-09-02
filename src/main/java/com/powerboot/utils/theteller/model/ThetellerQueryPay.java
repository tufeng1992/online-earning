package com.powerboot.utils.theteller.model;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class ThetellerQueryPay implements Serializable {

    private String status;

    private String code;

    private String token;

    private String reason;

    private String transaction_id;

    private String r_switch;

    private String subscriber_number;

    private BigDecimal amount;

}
