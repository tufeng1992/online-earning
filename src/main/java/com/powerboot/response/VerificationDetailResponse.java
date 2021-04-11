package com.powerboot.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.math.BigDecimal;

@ApiModel
public class VerificationDetailResponse {

    //名字
    @ApiModelProperty("名字")
    private String name;
    //性别 1-男 2-女
    @ApiModelProperty("性别 1-男 2-女")
    private Integer gender;
    //婚姻 1-单身 2-已婚
    @ApiModelProperty("婚姻 1-单身 2-已婚")
    private Integer marriage;
    //邮箱
    @ApiModelProperty("邮箱")
    private String email;
    //身份证id
    @ApiModelProperty("身份证id")
    private String panId;

    //照片地址
    @ApiModelProperty("照片地址")
    private String faceUrl;

    //金融系统码
    @ApiModelProperty("金融系统码")
    private String ifscCode;
    //银行卡账号
    @ApiModelProperty("银行卡账号")
    private String accountNumber;

    //借款金额
    @ApiModelProperty("借款金额")
    private BigDecimal amount;
    //借款期限
    @ApiModelProperty("借款期限")
    private Integer term;
    //利息
    @ApiModelProperty("利息")
    private BigDecimal interest;
    //认证费
    @ApiModelProperty("认证费")
    private BigDecimal securityDeposit;
    //税费
    @ApiModelProperty("税费")
    private BigDecimal tax;
    //偿还金额
    @ApiModelProperty("偿还金额")
    private BigDecimal repayment;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public Integer getMarriage() {
        return marriage;
    }

    public void setMarriage(Integer marriage) {
        this.marriage = marriage;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPanId() {
        return panId;
    }

    public void setPanId(String panId) {
        this.panId = panId;
    }

    public String getFaceUrl() {
        return faceUrl;
    }

    public void setFaceUrl(String faceUrl) {
        this.faceUrl = faceUrl;
    }

    public String getIfscCode() {
        return ifscCode;
    }

    public void setIfscCode(String ifscCode) {
        this.ifscCode = ifscCode;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Integer getTerm() {
        return term;
    }

    public void setTerm(Integer term) {
        this.term = term;
    }

    public BigDecimal getInterest() {
        return interest;
    }

    public void setInterest(BigDecimal interest) {
        this.interest = interest;
    }

    public BigDecimal getSecurityDeposit() {
        return securityDeposit;
    }

    public void setSecurityDeposit(BigDecimal securityDeposit) {
        this.securityDeposit = securityDeposit;
    }

    public BigDecimal getTax() {
        return tax;
    }

    public void setTax(BigDecimal tax) {
        this.tax = tax;
    }

    public BigDecimal getRepayment() {
        return repayment;
    }

    public void setRepayment(BigDecimal repayment) {
        this.repayment = repayment;
    }
}
