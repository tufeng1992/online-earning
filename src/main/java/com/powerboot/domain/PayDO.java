package com.powerboot.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.powerboot.request.order.LipaParam;
import com.powerboot.utils.DateUtils;
import com.powerboot.utils.LocalDateUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 支付结果
 */
@ApiModel(value = "支付类")
public class PayDO implements Serializable {
	
	//主键id
	@ApiModelProperty(value = "id")
	private Long id;
	//用户id
	@ApiModelProperty(value = "用户id")
	private Long userId;

	@ApiModelProperty(value = "类型:1-充值 2-购买VIP2 3-购买VIP3 99-提现")
	private Integer type;

	@ApiModelProperty(value = "类型描述:1-充值 2-购买VIP2 3-购买VIP3 99-提现")
	private String typeDesc;
	//支付金额
	@ApiModelProperty(value = "支付金额")
	private BigDecimal amount;
	//支付状态 1-支付中 2-已支付 3-支付失败
	@ApiModelProperty(value = "1-支付中 2-已支付 3-支付失败")
	private Integer status;

	@ApiModelProperty(value = "支付状态:1-充值 2-购买VIP2 3-购买VIP3 99-提现")
	private String statusDesc;

	@ApiModelProperty(value = "支付渠道:1-四方 2-三方")
	private String payChannel;

	@ApiModelProperty(value = "四方支付渠道分支:1-wegame 2-paystax")
	private String payChannelBranch;

	@ApiModelProperty(value = "支付跳转url 只有当渠道为1时才存在")
	private String thirdUrl;
	//第三方支付流水号
	@ApiModelProperty(value = "第三方支付流水号")
	private String thirdNo;
	//第三方支付返回状态
	@ApiModelProperty(value = "第三方支付返回状态",hidden = true)
	private String thirdStatus;
	//第三方支付回调时间
	@ApiModelProperty(value = "第三方支付回调时间",hidden = true)
	@JsonFormat(pattern = LocalDateUtil.SIMPLE_DATE_FORMAT)
	private Date thirdCallbackTime;
	//订单号
	@ApiModelProperty(value = "订单号")
	private String orderNo;
	//创建时间
	@ApiModelProperty(value = "创建时间")
	@JsonFormat(pattern = LocalDateUtil.SIMPLE_DATE_FORMAT)
	private Date createTime;
	//更新时间
	@ApiModelProperty(value = "更新时间")
	@JsonFormat(pattern = LocalDateUtil.SIMPLE_DATE_FORMAT)
	private Date updateTime;

	@ApiModelProperty(value = "key_id")
	private String keyId;

	@ApiModelProperty(value = "线下支付凭证")
	private String refNo;

	//销售id
	@ApiModelProperty(value = "销售id")
	private Long saleId;

	@ApiModelProperty(value = "请求参数")
	private LipaParam lipaParam;

	@ApiModelProperty(value = "请求时间")
	private String requestTime = DateUtils.getFormatNow(DateUtils.SIMPLE_DATEFORMAT_YMDHMS);

	@ApiModelProperty(value = "三方请求返回")
	private String thirdResponse;

	//审批状态
	private Integer applyStatus;
	//审批信息
	private String remark;

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Integer getApplyStatus() {
		return applyStatus;
	}

	public void setApplyStatus(Integer applyStatus) {
		this.applyStatus = applyStatus;
	}

	@ApiModelProperty(value = "请求时间")
	public String getRequestTime() {
		return requestTime;
	}

	public String getThirdResponse() {
		return thirdResponse;
	}

	public void setThirdResponse(String thirdResponse) {
		this.thirdResponse = thirdResponse;
	}

	public void setRequestTime(String requestTime) {
		this.requestTime = requestTime;
	}

	public LipaParam getLipaParam() {
		return lipaParam;
	}

	public void setLipaParam(LipaParam lipaParam) {
		this.lipaParam = lipaParam;
	}

	public Long getSaleId() {
		return saleId;
	}

	public void setSaleId(Long saleId) {
		this.saleId = saleId;
	}

	public String getPayChannelBranch() {
		return payChannelBranch;
	}

	public void setPayChannelBranch(String payChannelBranch) {
		this.payChannelBranch = payChannelBranch;
	}

	public String getPayChannel() {
		return payChannel;
	}

	public void setPayChannel(String payChannel) {
		this.payChannel = payChannel;
	}

	public String getThirdUrl() {
		return thirdUrl;
	}

	public void setThirdUrl(String thirdUrl) {
		this.thirdUrl = thirdUrl;
	}

	/**
	 * 设置：主键id
	 */
	public void setId(Long id) {
		this.id = id;
	}
	/**
	 * 获取：主键id
	 */
	public Long getId() {
		return id;
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

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	/**
	 * 设置：支付金额
	 */
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	/**
	 * 获取：支付金额
	 */
	public BigDecimal getAmount() {
		return amount;
	}
	/**
	 * 设置：支付状态 1-待支付 2-已支付 3-支付失败
	 */
	public void setStatus(Integer status) {
		this.status = status;
	}
	/**
	 * 获取：支付状态 1-待支付 2-已支付 3-支付失败
	 */
	public Integer getStatus() {
		return status;
	}
	/**
	 * 设置：第三方支付流水号
	 */
	public void setThirdNo(String thirdNo) {
		this.thirdNo = thirdNo;
	}
	/**
	 * 获取：第三方支付流水号
	 */
	public String getThirdNo() {
		return thirdNo;
	}
	/**
	 * 设置：第三方支付返回状态
	 */
	public void setThirdStatus(String thirdStatus) {
		this.thirdStatus = thirdStatus;
	}
	/**
	 * 获取：第三方支付返回状态
	 */
	public String getThirdStatus() {
		return thirdStatus;
	}
	/**
	 * 设置：第三方支付回调时间
	 */
	public void setThirdCallbackTime(Date thirdCallbackTime) {
		this.thirdCallbackTime = thirdCallbackTime;
	}
	/**
	 * 获取：第三方支付回调时间
	 */
	public Date getThirdCallbackTime() {
		return thirdCallbackTime;
	}
	/**
	 * 设置：订单号
	 */
	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}
	/**
	 * 获取：订单号
	 */
	public String getOrderNo() {
		return orderNo;
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

	public String getKeyId() {
		return keyId;
	}

	public void setKeyId(String keyId) {
		this.keyId = keyId;
	}

	public String getTypeDesc() {
		return typeDesc;
	}

	public void setTypeDesc(String typeDesc) {
		this.typeDesc = typeDesc;
	}

	public String getStatusDesc() {
		return statusDesc;
	}

	public void setStatusDesc(String statusDesc) {
		this.statusDesc = statusDesc;
	}

	public String getRefNo() {
		return refNo;
	}

	public void setRefNo(String refNo) {
		this.refNo = refNo;
	}
}
