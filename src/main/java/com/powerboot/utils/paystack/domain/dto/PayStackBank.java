package com.powerboot.utils.paystack.domain.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 银行信息
 */
@Data
public class PayStackBank implements Serializable {

    private String country;

    private String code;

    private Boolean pay_with_bank;

    private String longcode;

    private Boolean active;

    private String type;

    private String name;

    private String currency;

    private Integer id;

    private String slug;

}
