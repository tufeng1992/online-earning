package com.powerboot.request;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
@ApiModel("提交支付凭证")
public class SubmitPayCertReq extends BaseRequest implements Serializable {

    //第三方事务id
    @NotEmpty
    private String transctionId;
    //上传金额
    @NotNull
    private BigDecimal amount;
    //银行名称
    @NotEmpty
    private String bankName;
    //账户名称
    @NotEmpty
    private String accountName;
    //上传凭证url
    private String certUrl;
    //订单号
    @NotEmpty
    private String orderNo;
    //备注
    private String remark;


}
