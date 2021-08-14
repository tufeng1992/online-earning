package com.powerboot.utils.happylife.model;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class HappyLifeQueryPayRes implements Serializable {

    /**
     * 响应状态 0：响应成功
     */
    private String code;

    /**
     * 响应信息
     */
    private String msg;

    private HappyLifeQueryPayDetailRes data;

}
