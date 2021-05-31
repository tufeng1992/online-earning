package com.powerboot.utils.grecash.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class GetAccessTokenRes implements Serializable {

    /**
     * 接口访问S2S token
     */
    private String access_token;

    /**
     * token类型
     */
    private String token_type;

    /**
     * 有效时间
     */
    private Integer expires_in;

    /**
     * 作用域
     */
    private String scope;

    /**
     * tokenID
     */
    private String jti;

}
