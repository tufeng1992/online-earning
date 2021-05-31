package com.powerboot.utils.wallyt.domain.dto;

import lombok.Data;
import org.apache.poi.ss.formula.functions.T;

import java.io.Serializable;
import java.util.List;

/**
 * 回调请求
 */
@Data
public class WallytCallbackReq<T extends WallyBaseResponseObject> implements Serializable {

    private List<T> request;

    private List<String> signature;

}
