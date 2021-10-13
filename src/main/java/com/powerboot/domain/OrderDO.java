package com.powerboot.domain;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;


/**
 * 刷单任务订单表
 *
 * @author system
 * @email system@163.com
 * @date 2020-10-29 17:28:02
 */
@Data
@TableName("task_order")
public class OrderDO implements Serializable {
    private static final long serialVersionUID = 1L;

    //主键id，同时为订单id
    @TableId
    private Long id;
    //订单号
    private String orderNumber;
    //用户id
    private Long userId;
    //商品金额
    private BigDecimal amount;
    //商品id
    private Long productId;
    //商品利率
    private Long productRation;
    //刷单佣金
    private BigDecimal productCommission;
    //顶级会员id
    private Long oneId;
    //顶级会员分润比例，如10% 则值为10
    private Long oneRatio;
    //顶级会员分润金额
    private BigDecimal oneAmount;
    //次顶级会员id
    private Long twoId;
    //次顶级会员分润比例
    private Long twoRatio;
    //次顶级会员分润金额
    private BigDecimal twoAmount;
    //上级会员id
    private Long threeId;
    //上级会员分润比例
    private Long threeRatio;
    //上级会员分润金额
    private BigDecimal threeAmount;
    //创建时间
    private Date createTime;
    //更新时间
    private Date updateTime;

    private Long saleId;
}
