package com.powerboot.utils.happylife.model;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class HappyLifePayOutRes implements Serializable {

    private String msg;

    /**
     * 0：响应成功
     */
    private String code;

    private HappyLifePayOutDetailRes data;
}
