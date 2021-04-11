package com.powerboot.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 */
public class SummaryTableDO implements Serializable {
    //id
    private Integer id;
    //生成日期
    private LocalDate generatedDate;
    //会员总数
    private Integer vipCount;
    //会员1总数
    private Integer vip1Count;
    //会员2总数
    private Integer vip2Count;
    //会员3总数
    private Integer vip3Count;
    //会员4总数
    private Integer vip4Count;
    //会员5总数
    private Integer vip5Count;
    //有效会员总数
    private Integer vipValidCount;
    //会员充值金额
    private BigDecimal rechargeAmount;
    //会员资产总数
    private BigDecimal vipBalanceCount;
    //刷单佣金
    private BigDecimal commissionsAmount;
    //理财收益
    private BigDecimal financialProfitAmount;
    //新增用户
    private Integer localUserCount;
    //充值笔数
    private Integer rechargeCount;
    //提现笔数
    private Integer withdrawCount;
    //提现金额
    private BigDecimal withdrawAmount;
    //购买vip单数
    private Integer vipPayCount;
    //购买vip金额
    private BigDecimal vipPayAmount;
    //首冲奖励
    private BigDecimal firstRechargeAmount;
    //理财转入
    private BigDecimal financialProfitInAmount;
    //理财转出
    private BigDecimal financialProfitOutAmount;
    //理财总金额
    private BigDecimal financialProfitCountAmount;
    //用户裂变
    private Integer userReferral;
    //客服推广
    private Integer saleReferral;

    public Integer getUserReferral() {
        return userReferral;
    }

    public void setUserReferral(Integer userReferral) {
        this.userReferral = userReferral;
    }

    public Integer getSaleReferral() {
        return saleReferral;
    }

    public void setSaleReferral(Integer saleReferral) {
        this.saleReferral = saleReferral;
    }

    public BigDecimal getFinancialProfitCountAmount() {
        return financialProfitCountAmount;
    }

    public void setFinancialProfitCountAmount(BigDecimal financialProfitCountAmount) {
        this.financialProfitCountAmount = financialProfitCountAmount;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LocalDate getGeneratedDate() {
        return generatedDate;
    }

    public void setGeneratedDate(LocalDate generatedDate) {
        this.generatedDate = generatedDate;
    }

    public Integer getVipCount() {
        return vipCount;
    }

    public void setVipCount(Integer vipCount) {
        this.vipCount = vipCount;
    }

    public Integer getVip1Count() {
        return vip1Count;
    }

    public void setVip1Count(Integer vip1Count) {
        this.vip1Count = vip1Count;
    }

    public Integer getVip2Count() {
        return vip2Count;
    }

    public void setVip2Count(Integer vip2Count) {
        this.vip2Count = vip2Count;
    }

    public Integer getVip3Count() {
        return vip3Count;
    }

    public void setVip3Count(Integer vip3Count) {
        this.vip3Count = vip3Count;
    }

    public Integer getVip4Count() {
        return vip4Count;
    }

    public void setVip4Count(Integer vip4Count) {
        this.vip4Count = vip4Count;
    }

    public Integer getVip5Count() {
        return vip5Count;
    }

    public void setVip5Count(Integer vip5Count) {
        this.vip5Count = vip5Count;
    }

    public Integer getVipValidCount() {
        return vipValidCount;
    }

    public void setVipValidCount(Integer vipValidCount) {
        this.vipValidCount = vipValidCount;
    }

    public BigDecimal getRechargeAmount() {
        return rechargeAmount;
    }

    public void setRechargeAmount(BigDecimal rechargeAmount) {
        this.rechargeAmount = rechargeAmount;
    }

    public BigDecimal getVipBalanceCount() {
        return vipBalanceCount;
    }

    public void setVipBalanceCount(BigDecimal vipBalanceCount) {
        this.vipBalanceCount = vipBalanceCount;
    }

    public BigDecimal getCommissionsAmount() {
        return commissionsAmount;
    }

    public void setCommissionsAmount(BigDecimal commissionsAmount) {
        this.commissionsAmount = commissionsAmount;
    }

    public BigDecimal getFinancialProfitAmount() {
        return financialProfitAmount;
    }

    public void setFinancialProfitAmount(BigDecimal financialProfitAmount) {
        this.financialProfitAmount = financialProfitAmount;
    }

    public Integer getLocalUserCount() {
        return localUserCount;
    }

    public void setLocalUserCount(Integer localUserCount) {
        this.localUserCount = localUserCount;
    }

    public Integer getRechargeCount() {
        return rechargeCount;
    }

    public void setRechargeCount(Integer rechargeCount) {
        this.rechargeCount = rechargeCount;
    }

    public Integer getWithdrawCount() {
        return withdrawCount;
    }

    public void setWithdrawCount(Integer withdrawCount) {
        this.withdrawCount = withdrawCount;
    }

    public BigDecimal getWithdrawAmount() {
        return withdrawAmount;
    }

    public void setWithdrawAmount(BigDecimal withdrawAmount) {
        this.withdrawAmount = withdrawAmount;
    }

    public Integer getVipPayCount() {
        return vipPayCount;
    }

    public void setVipPayCount(Integer vipPayCount) {
        this.vipPayCount = vipPayCount;
    }

    public BigDecimal getVipPayAmount() {
        return vipPayAmount;
    }

    public void setVipPayAmount(BigDecimal vipPayAmount) {
        this.vipPayAmount = vipPayAmount;
    }

    public BigDecimal getFirstRechargeAmount() {
        return firstRechargeAmount;
    }

    public void setFirstRechargeAmount(BigDecimal firstRechargeAmount) {
        this.firstRechargeAmount = firstRechargeAmount;
    }

    public BigDecimal getFinancialProfitInAmount() {
        return financialProfitInAmount;
    }

    public void setFinancialProfitInAmount(BigDecimal financialProfitInAmount) {
        this.financialProfitInAmount = financialProfitInAmount;
    }

    public BigDecimal getFinancialProfitOutAmount() {
        return financialProfitOutAmount;
    }

    public void setFinancialProfitOutAmount(BigDecimal financialProfitOutAmount) {
        this.financialProfitOutAmount = financialProfitOutAmount;
    }
}
