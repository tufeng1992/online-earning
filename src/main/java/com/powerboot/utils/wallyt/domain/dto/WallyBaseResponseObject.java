package com.powerboot.utils.wallyt.domain.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class WallyBaseResponseObject implements Serializable {

    /**
     * Return Code
     */
    private String code;

    /**
     * Return Message
     */
    private String message;
}
