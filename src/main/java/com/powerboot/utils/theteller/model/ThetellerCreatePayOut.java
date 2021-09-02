package com.powerboot.utils.theteller.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class ThetellerCreatePayOut implements Serializable {

    private String status;

    private String code;

    private String reference_id;

    private String account_name;

    private String reason;

    private String description;

}
