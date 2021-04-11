package com.powerboot.utils;

import java.math.BigDecimal;

public class BigDecimalUtils {

    public static BigDecimal convertNegative(BigDecimal amount){
        return amount.multiply(new BigDecimal("-1"));
    }

}
