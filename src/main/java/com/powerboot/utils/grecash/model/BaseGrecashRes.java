package com.powerboot.utils.grecash.model;

import lombok.Data;
import org.apache.poi.ss.formula.functions.T;

import java.io.Serializable;

/**
 * 公共返回参数
 * @param <T>
 */
@Data
public class BaseGrecashRes<T> implements Serializable {

    /**
     * 数据返回码（1000表示成功）
     */
    private String errorCode;

    /**
     * 支付数据
     */
    private String sign;

    /**
     * 签名，payorder数据的签名
     */
    private T payorder;

    /**
     * 返回码，公共返回参数
     */
    private Integer code;

    /**
     * 返回信息，公共返回参数
     */
    private String info;

    /**
     * 返回数据，对象
     */
    private T result;

}
