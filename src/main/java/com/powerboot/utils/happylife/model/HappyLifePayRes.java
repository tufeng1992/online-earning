package com.powerboot.utils.happylife.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class HappyLifePayRes implements Serializable {

    private String msg;

    private String data;

    private String code;

}
