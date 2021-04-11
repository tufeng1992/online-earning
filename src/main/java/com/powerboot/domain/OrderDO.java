package com.powerboot.domain;

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
public class OrderDO implements Serializable {
    private static final long serialVersionUID = 1L;

    //主键id，同时为订单id
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

    /**
     * 设置：主键id，同时为订单id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 获取：主键id，同时为订单id
     */
    public Long getId() {
        return id;
    }

    /**
     * 设置：订单号
     */
    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    /**
     * 获取：订单号
     */
    public String getOrderNumber() {
        return orderNumber;
    }

    /**
     * 设置：用户id
     */
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    /**
     * 获取：用户id
     */
    public Long getUserId() {
        return userId;
    }

    /**
     * 设置：商品金额
     */
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    /**
     * 获取：商品金额
     */
    public BigDecimal getAmount() {
        return amount;
    }

    /**
     * 设置：商品id
     */
    public void setProductId(Long productId) {
        this.productId = productId;
    }

    /**
     * 获取：商品id
     */
    public Long getProductId() {
        return productId;
    }

    public Long getProductRation() {
        return productRation;
    }

    public void setProductRation(Long productRation) {
        this.productRation = productRation;
    }

    /**
     * 设置：刷单佣金
     */
    public void setProductCommission(BigDecimal productCommission) {
        this.productCommission = productCommission;
    }

    /**
     * 获取：刷单佣金
     */
    public BigDecimal getProductCommission() {
        return productCommission;
    }

    /**
     * 设置：顶级会员id
     */
    public void setOneId(Long oneId) {
        this.oneId = oneId;
    }

    /**
     * 获取：顶级会员id
     */
    public Long getOneId() {
        return oneId;
    }

    /**
     * 设置：顶级会员分润比例，如10% 则值为10
     */
    public void setOneRatio(Long oneRatio) {
        this.oneRatio = oneRatio;
    }

    /**
     * 获取：顶级会员分润比例，如10% 则值为10
     */
    public Long getOneRatio() {
        return oneRatio;
    }

    /**
     * 设置：顶级会员分润金额
     */
    public void setOneAmount(BigDecimal oneAmount) {
        this.oneAmount = oneAmount;
    }

    /**
     * 获取：顶级会员分润金额
     */
    public BigDecimal getOneAmount() {
        return oneAmount;
    }

    /**
     * 设置：次顶级会员id
     */
    public void setTwoId(Long twoId) {
        this.twoId = twoId;
    }

    /**
     * 获取：次顶级会员id
     */
    public Long getTwoId() {
        return twoId;
    }

    /**
     * 设置：次顶级会员分润比例
     */
    public void setTwoRatio(Long twoRatio) {
        this.twoRatio = twoRatio;
    }

    /**
     * 获取：次顶级会员分润比例
     */
    public Long getTwoRatio() {
        return twoRatio;
    }

    /**
     * 设置：次顶级会员分润金额
     */
    public void setTwoAmount(BigDecimal twoAmount) {
        this.twoAmount = twoAmount;
    }

    /**
     * 获取：次顶级会员分润金额
     */
    public BigDecimal getTwoAmount() {
        return twoAmount;
    }

    /**
     * 设置：上级会员id
     */
    public void setThreeId(Long threeId) {
        this.threeId = threeId;
    }

    /**
     * 获取：上级会员id
     */
    public Long getThreeId() {
        return threeId;
    }

    /**
     * 设置：上级会员分润比例
     */
    public void setThreeRatio(Long threeRatio) {
        this.threeRatio = threeRatio;
    }

    /**
     * 获取：上级会员分润比例
     */
    public Long getThreeRatio() {
        return threeRatio;
    }

    /**
     * 设置：上级会员分润金额
     */
    public void setThreeAmount(BigDecimal threeAmount) {
        this.threeAmount = threeAmount;
    }

    /**
     * 获取：上级会员分润金额
     */
    public BigDecimal getThreeAmount() {
        return threeAmount;
    }

    /**
     * 设置：创建时间
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * 获取：创建时间
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * 设置：更新时间
     */
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    /**
     * 获取：更新时间
     */
    public Date getUpdateTime() {
        return updateTime;
    }





    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OrderDO orderDO = (OrderDO) o;

        return id.equals(orderDO.id);
    }

    @Override
    public int hashCode() {
        int result = id.intValue();
        result = 31 * result + id.hashCode();
        result = 31 * result + orderNumber.hashCode();
        return result;
    }
}
