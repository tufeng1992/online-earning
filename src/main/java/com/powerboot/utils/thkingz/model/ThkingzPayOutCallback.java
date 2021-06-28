package com.powerboot.utils.thkingz.model;

import lombok.Data;
import org.apache.poi.ss.formula.functions.T;

import java.io.Serializable;
import java.util.List;

@Data
public class ThkingzPayOutCallback implements Serializable {

    private String code;

    private String msg;

    private ThkingzCreatePayOutRes data;

}
