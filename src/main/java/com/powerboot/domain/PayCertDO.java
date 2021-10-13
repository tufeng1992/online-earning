package com.powerboot.domain;

import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 支付凭证
 */
@Data
@TableName("pay_cert")
public class PayCertDO {

    //主键id
    private Long id;
    //用户id
    private Long userId;
    //第三方事务id
    private String transctionId;
    //上传金额
    private BigDecimal amount;
    //银行名称
    private String bankName;
    //账户名称
    private String accountName;
    //上传凭证url
    private String certUrl;
    //订单号
    private String orderNo;
    //备注
    private String remark;
    //创建时间
    private Date createTime;
    //更新时间
    private Date updateTime;

}
