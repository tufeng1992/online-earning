package com.powerboot.utils.thkingz.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class ThkingzBaseRes<T> implements Serializable {

    private String code;

    private String msg;

    private T data;

    private String url;

    private String key;

}
