package com.powerboot.utils.wallyt.domain.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * wallyt返回内容
 * @param <T>
 */
@Data
public class WallytResponse<T extends WallyBaseResponseObject> implements Serializable {

    private String signature;

    private T response;

}
