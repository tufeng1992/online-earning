package com.powerboot.utils.paystack.domain.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * paystack 统一返回
 */
@Data
public class PayStackResponse implements Serializable {

    private Boolean status;

    private Object data;

    private String message;

}
