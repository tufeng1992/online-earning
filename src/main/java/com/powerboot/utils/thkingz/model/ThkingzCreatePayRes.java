package com.powerboot.utils.thkingz.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class ThkingzCreatePayRes implements Serializable {

    private String qrcode;

    private String order_no;

}
