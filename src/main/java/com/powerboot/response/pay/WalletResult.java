package com.powerboot.response.pay;

import java.math.BigDecimal;

/**
 * Create  2021 - 01 - 26 2:33 下午
 */
public class WalletResult {
    //余额
    private BigDecimal balance;
    //账号
    private String accountNumber;
    //是否生效
    private Boolean active;
    //打印信息
    private String printDesc;

    public String getPrintDesc() {
        return printDesc;
    }

    public void setPrintDesc(String printDesc) {
        this.printDesc = printDesc;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
