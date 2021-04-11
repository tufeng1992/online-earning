package com.powerboot.response;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Create  2020 - 12 - 24 10:28 上午
 */
public class PayVO implements Serializable {
    //总数
    private Integer count = 0;
    //总金额
    private BigDecimal amount = BigDecimal.ZERO;

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
