package com.powerboot.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@ApiModel
@Data
public class BankRequest extends BaseRequest{
    @ApiModelProperty(value = "姓")
    private String firstName;
    @ApiModelProperty(value = "中间名middle name")
    private String name;
    @ApiModelProperty(value = "尾名")
    private String lastName;
    @ApiModelProperty(value = "手机号 MPESA account")
    private String mobile;
    @ApiModelProperty(value = "邮箱 email")
    private String email;

    //银行卡账号
    @ApiModelProperty(value = "银行卡账号", required = true)
    @NotEmpty
    private String accountNumber;

    /**
     * 银行卡CVV
     */
    @ApiModelProperty(value = "银行卡CVV")
    private String accountCvv;

    /**
     * 银行卡过期日
     */
    @ApiModelProperty(value = "银行卡过期日")
    private String accountExpireDay;

    /**
     * 银行卡过期月
     */
    @ApiModelProperty(value = "银行卡过期月")
    private String accountExpireMonth;

    /**
     * 银行卡过期年
     */
    @ApiModelProperty(value = "银行卡过期年")
    private String accountExpireYear;

    /**
     * 银行名称
     */
    @ApiModelProperty(value = "银行名称", required = true)
    private String bankName;

    /**
     * 银行代码
     */
    @ApiModelProperty(value = "银行代码", required = true)
    @NotEmpty
    private String bankCode;
//    @ApiModelProperty(value = "地址")
//    private String address;
//    @ApiModelProperty(value = "交易密码")
//    private String fundPassword;



}
