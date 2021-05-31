package com.powerboot.service;

import com.powerboot.base.BaseResponse;
import com.powerboot.common.JsonUtils;
import com.powerboot.config.BaseException;
import com.powerboot.consts.CacheConsts;
import com.powerboot.consts.DictConsts;
import com.powerboot.consts.I18nEnum;
import com.powerboot.consts.TipConsts;
import com.powerboot.dao.PayDao;
import com.powerboot.domain.*;
import com.powerboot.enums.*;
import com.powerboot.request.LoanDetailRequest;
import com.powerboot.request.payment.CreatePayInOrder;
import com.powerboot.request.payment.QueryPayInParam;
import com.powerboot.request.payment.QueryPayOutParam;
import com.powerboot.response.PayVO;
import com.powerboot.response.pay.PaymentResult;
import com.powerboot.service.impl.PayStackPayServiceImpl;
import com.powerboot.utils.DateUtils;
import com.powerboot.utils.RedisUtils;
import com.powerboot.utils.SpringContextUtils;
import com.powerboot.utils.StringRandom;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

@Service
public class PayService {

    private static Logger logger = LoggerFactory.getLogger(PayService.class);
    @Autowired
    WithdrawalRecordService withdrawalRecordService;
    @Autowired
    PayService payService;
    @Autowired
    private PayDao payDao;
    @Autowired
    private DictService dictService;
    @Autowired
    private UserService userService;
    @Autowired
    private BalanceService balanceService;
    @Autowired
    private WeGamePayService weGamePayService;
    @Resource(name = "commonExecutor")
    private ExecutorService commonExecutor;
    @Autowired
    private InviteLogService inviteLogService;

    public PayDO get(Long id) {
        return payDao.get(id);
    }

    public List<PayDO> list(Map<String, Object> map) {
        return payDao.list(map);
    }

    public List<PayDO> getPayinOrder() {
        return payDao.getPayinOrder();
    }

    public List<PayDO> getPayOutOrder() {
        return payDao.getPayOutOrder();
    }

    public int count(Map<String, Object> map) {
        return payDao.count(map);
    }

    public int save(PayDO pay) {
        if (pay.getSaleId() == null) {
            pay.setSaleId(1L);
        }
        return payDao.save(pay);
    }

    public int update(PayDO pay) {
        return payDao.update(pay);
    }

    public int updatePay(PayDO pay) {
        return payDao.updatePay(pay);
    }

    public int remove(Long id) {
        return payDao.remove(id);
    }

    public int batchRemove(Long[] ids) {
        return payDao.batchRemove(ids);
    }

    /**
     * 获取充值和提现 记录
     */
    public List<PayDO> getRechargeByUserId(Long userId) {
        List<PayDO> result = payDao.getRechargeByUserId(userId);
        if (CollectionUtils.isNotEmpty(result)) {
            result.forEach(o -> {
                o.setTypeDesc(PayEnums.PayTypeEnum.getENDescByCode(o.getType()));
                o.setStatusDesc(StatusTypeEnum.getENDescByCode(o.getStatus()));
            });
        }
        return result;
    }

    /**
     * 获取充值和提现 记录
     */
    public List<PayDO> getWithdrawByUserId(Long userId) {
        List<PayDO> result = payDao.getWithdrawByUserId(userId);
        if (CollectionUtils.isNotEmpty(result)) {
            result.forEach(o -> {
                o.setTypeDesc(PayEnums.PayTypeEnum.getENDescByCode(o.getType()));
                o.setStatusDesc(StatusTypeEnum.getENDescByCode(o.getStatus()));
            });
        }
        return result;
    }

    public PayDO getByOutNo(String outNo) {
        return payDao.getByOutNo(outNo);
    }

    public PayDO getOrderNo(String orderNo) {
        PayDO payDO = payDao.getByOrderNo(orderNo);
        if (payDO == null) {
            throw new BaseException(I18nEnum.ORDER_NOT_FOUND_FAIL.getMsg());
        }
        return payDO;
    }

    @Transactional
    public BaseResponse<PayDO> getByOrderNo(String orderNo) {
        logger.info("获取第三方支付状态,param:{}", orderNo);
        PayDO payDO = payDao.getByOrderNo(orderNo);
        if (PayEnums.PayTypeEnum.WITHDRAW.getCode().equals(payDO.getType())) {
            return getByPayOutOrder(orderNo);
        }
        String payFailDesc = "";
        int success = 0;
        if (payDO == null) {
            return BaseResponse.fail("this order not found");
        }
        if (!PayEnums.PayTypeEnum.RECHARGE.getCode().equals(payDO.getType())
            && !PayEnums.PayTypeEnum.PAY_VIP2.getCode().equals(payDO.getType())
            && !PayEnums.PayTypeEnum.PAY_VIP3.getCode().equals(payDO.getType())
            && !PayEnums.PayTypeEnum.PAY_VIP4.getCode().equals(payDO.getType())
            && !PayEnums.PayTypeEnum.PAY_VIP5.getCode().equals(payDO.getType())
            && !PayEnums.PayTypeEnum.WITHDRAW.getCode().equals(payDO.getType())) {
            return BaseResponse.fail(I18nEnum.UNKNOWN_PAY_FAIL.getMsg());
        }

        //获取提现状态
        if (PayEnums.PayTypeEnum.WITHDRAW.getCode().equals(payDO.getType())) {
            //            payService.refreshPayout(payDO);
            return BaseResponse.fail(I18nEnum.UNKNOWN_PAY_FAIL.getMsg());
        }

        if (!PayEnums.PayStatusEnum.PAYING.getCode().equals(payDO.getStatus()) && !PayEnums.PayStatusEnum.TIMEOUT
            .getCode().equals(payDO.getStatus())) {
            return BaseResponse.success(payDO);
        }
        //获取支付bean
        String payBeanName = PaymentServiceEnum.getBeanNameByCode(payDO.getPayChannelBranch());
        if (PaymentChannelEnum.PAY_FOUR.getCode().equals(payDO.getPayChannel())) {
            //找到支付实现
            PaymentService paymentService = SpringContextUtils.getBean(payBeanName);
            QueryPayInParam queryPayInParam = new QueryPayInParam();
            queryPayInParam.setLocalOrderNo(payDO.getOrderNo());
            queryPayInParam.setThirdOrderNo(payDO.getThirdNo());
            BaseResponse<PaymentResult> payInOrder = paymentService.getPayInOrder(queryPayInParam);
            if (payInOrder == null || payInOrder.getResultData() == null) {
                return BaseResponse.fail(I18nEnum.ORDER_NOT_FOUND_FAIL.getMsg());
            }
            PaymentResult resultData = payInOrder.getResultData();
            payDO.setThirdNo(resultData.getThirdOrderNo());
            payDO.setThirdStatus(resultData.getThirdOrderStatus());
            payDO.setStatus(resultData.getStatus());
            payFailDesc = resultData.getDescription();
            payDO.setThirdCallbackTime(new Date());
            success = updatePay(payDO);
        }

        if (success > 0) {
            payInComplete(payDO);
            logger.info("**** {}-{} ****  用户id:{}, {} {},金额:  {}   {}", payBeanName,
                    PayEnums.PayStatusEnum.getDescByCode(payDO.getStatus()), payDO.getUserId(),
                    PayEnums.PayStatusEnum.getDescByCode(payDO.getStatus()),
                    PayEnums.PayTypeEnum.getDescByCode(payDO.getType()), payDO.getAmount(), payFailDesc);
        }
        return BaseResponse.success(payDO);
    }

    /**
     * 支付完成
     * @param payDO
     */
    public void payInComplete(PayDO payDO) {
        BigDecimal relPayAmount = payDO.getAmount() == null ? new BigDecimal("0.00") : payDO.getAmount();
        //支付成功
        if (payDO.getStatus().equals(PayEnums.PayStatusEnum.PAID.getCode())) {
            //调用更新用户支付成功状态
            if (PayEnums.PayTypeEnum.RECHARGE.getCode().equals(payDO.getType())) {
                logger.info("充值用户增加余额,userid:" + payDO.getUserId() + " 订单号:" + payDO.getOrderNo() + " 订单状态:" + payDO
                        .getStatus() + " 新增余额:" + relPayAmount);
                //充值成功
                Date now = new Date();
                BalanceDO balanceDO = new BalanceDO();
                balanceDO.setAmount(relPayAmount);
                balanceDO.setType(BalanceTypeEnum.F.getCode());
                balanceDO.setUserId(payDO.getUserId());
                balanceDO.setWithdrawAmount(BigDecimal.ZERO);
                balanceDO.setServiceFee(BigDecimal.ZERO);
                balanceDO.setStatus(StatusTypeEnum.SUCCESS.getCode());
                balanceDO.setCreateTime(now);
                balanceDO.setUpdateTime(now);
                balanceDO.setOrderNo(payDO.getOrderNo());
                balanceService.addBalanceDetail(balanceDO);
                //判断是否首冲
                UserDO userDO = userService.get(payDO.getUserId());
                if (userDO != null && userDO.getFirstRecharge() == 0
                        && relPayAmount.compareTo(new BigDecimal("300")) >= 0
                        && userDO.getParentId() != null) {
                    //给上级返现
                    addParentBalance(1, userDO, now, payDO.getOrderNo());
                    //更新邀请记录
                    commonExecutor.execute(()->{
                        InviteLogDO inviteLog = new InviteLogDO();
                        inviteLog.setNewUserId(payDO.getUserId().intValue());
                        inviteLog.setFirstRechargeDate(now);
                        inviteLog.setNewUserStatus(InviteUserStatusEnum.RECHARGE.getCode());
                        BigDecimal parentAmount = new BigDecimal(RedisUtils.getString(DictConsts.FIRST_RECHARGE_PARENT_AMOUNT));
                        inviteLog.setInviteAmount(parentAmount);
                        inviteLogService.updateByNewUserId(inviteLog);
                    });
                }
                userService.updateFirstRechargeById(userDO.getId());

                Long timeOut = DateUtils.getEndOfDay(DateUtils.now()).getTime() - DateUtils.now().getTime();
                String rechargeKey = CacheConsts.getTodayRechargeKey(userDO.getId());
                RedisUtils.increment(rechargeKey, timeOut.intValue() / 1000);
            } else if (PayEnums.PayTypeEnum.PAY_VIP2.getCode().equals(payDO.getType())
                    || PayEnums.PayTypeEnum.PAY_VIP3.getCode().equals(payDO.getType())
                    || PayEnums.PayTypeEnum.PAY_VIP4.getCode().equals(payDO.getType())
                    || PayEnums.PayTypeEnum.PAY_VIP5.getCode().equals(payDO.getType())) {
                //购买会员成功
                userService.updateUserVIP(payDO.getUserId(), payDO.getType());
            } else {
                throw new BaseException("未知支付");
            }
        } else if (payDO.getStatus().equals(PayEnums.PayStatusEnum.FAIL.getCode())) {
            //支付失败
            if (PayEnums.PayTypeEnum.RECHARGE.getCode().equals(payDO.getType())) {
                Long timeOut = DateUtils.getEndOfDay(DateUtils.now()).getTime() - DateUtils.now().getTime();
                String rechargeKey = CacheConsts.getTodayRechargeKey(payDO.getUserId());
                Integer rechargeCount = RedisUtils.getValue(rechargeKey, Integer.class);
                if (rechargeCount != null && rechargeCount > 0) {
                    RedisUtils.increment(rechargeKey, -1L, timeOut.intValue() / 1000);
                }
            }
        }
    }

    /**
     * 添加上级返现
     * @param parentLevel
     * @param userDO
     * @param now
     * @param orderNo
     */
    private void addParentBalance(int parentLevel, UserDO userDO, Date now, String orderNo) {
        UserDO parentUser = userService.getUser(userDO.getParentId());
        if (null == parentUser || parentLevel == 4) {
            return;
        }
        BalanceDO parent = new BalanceDO();
        //获取首冲返现级别金额
        BigDecimal parentAmount = getParentAmount(parentLevel);
        parent.setAmount(parentAmount);
        parent.setType(BalanceTypeEnum.C.getCode());
        parent.setUserId(parentUser.getId());
        parent.setWithdrawAmount(BigDecimal.ZERO);
        parent.setServiceFee(BigDecimal.ZERO);
        parent.setStatus(StatusTypeEnum.SUCCESS.getCode());
        parent.setCreateTime(now);
        parent.setUpdateTime(now);
        parent.setOrderNo(orderNo);
        balanceService.addBalanceDetail(parent);
        parentLevel++;
        addParentBalance(parentLevel, parentUser, now, orderNo);
    }

    /**
     * 获取首冲返现级别金额
     * @param parentLevel
     * @return
     */
    private BigDecimal getParentAmount(int parentLevel) {
        BigDecimal parentAmount = null;
        if (1 == parentLevel) {
            parentAmount = new BigDecimal(RedisUtils.getString(DictConsts.FIRST_RECHARGE_PARENT_AMOUNT));
        } else if (2 == parentLevel) {
            parentAmount = new BigDecimal(RedisUtils.getString(DictConsts.FIRST_RECHARGE_PARENT_AMOUNT_LEVEL2));
        } else if (3 == parentLevel) {
            parentAmount = new BigDecimal(RedisUtils.getString(DictConsts.FIRST_RECHARGE_PARENT_AMOUNT_LEVEL3));
        }
        return parentAmount;
    }

    public BaseResponse<PayDO> handelOrderStatus(Map<String, Object> requestJson) {
        if (requestJson == null) {
            return BaseResponse.fail(I18nEnum.PARAMS_FAIL.getMsg());
        }
        String orderNo = requestJson.get("orderNo").toString();
        String checkoutRequestID = requestJson.get("CheckoutRequestID").toString();
        PayDO payDO = getOrderNo(orderNo);
        payDO.setThirdNo(checkoutRequestID);
        updatePay(payDO);
        return BaseResponse.success();
    }


    /**
     * 订单状态
     */
    public BaseResponse<Integer> getStatusByOrderNo(String orderNo) {
        PayDO payDO = payDao.getByOrderNo(orderNo);
        if (payDO != null && payDO.getStatus() != null) {
            return BaseResponse.success(payDO.getStatus());
        }
        return BaseResponse.fail(orderNo + "---" + I18nEnum.ORDER_NOT_FOUND_FAIL.getMsg());
    }

    //创建订单
    @Transactional(rollbackFor = Exception.class)
    public BaseResponse<PayDO> createOrder(LoanDetailRequest param, UserDO userDO) {
        logger.info("创建订单,参数:{}", param);
        logger.info("用户id:{},准备创建订单!", param.getUserId());
        //生成随机订单号
        String orderFirstNo = param.getUserId() + "p";
        String orderNo = orderFirstNo + StringRandom.getNumberAndLetterRandom(12 - orderFirstNo.length());
        //初始化支付参数
        //支付渠道分支 1-wegame 2-paystax
        String payChannelBranch = RedisUtils.getValue(DictConsts.PAY_CHANNEL_BRANCH, String.class);
        PayDO payDO = new PayDO();
        payDO.setOrderNo(orderNo);
        payDO.setType(param.getType());
        payDO.setUserId(param.getUserId());
        payDO.setAmount(param.getPayAmount());
        payDO.setStatus(PayEnums.PayStatusEnum.PAYING.getCode());
        payDO.setPayChannel("1");
        payDO.setPayChannelBranch(payChannelBranch);
        payDO.setSaleId(userDO.getSaleId());
        payDO.setApplyStatus(PayEnums.PayApplyStatusEnum.PASS.getCode());
        if (StringUtils.isEmpty(param.getRefNo())) {
            //生成随机凭证号
            String refNo = "unit_ref_" + param.getUserId() + "_" + StringRandom.getNumberAndLetterRandom(10);
            payDO.setRefNo(refNo);
        } else {
            payDO.setRefNo(param.getRefNo());
        }

        int saveSuccess = save(payDO);
        if (saveSuccess <= 0) {
            return BaseResponse.fail(I18nEnum.PAYMENT_FAIL.getMsg());
        }
        //获取支付bean
        String payBeanName = PaymentServiceEnum.getBeanNameByCode(payChannelBranch);
        //找到支付实现
        PaymentService paymentService = SpringContextUtils.getBean(payBeanName);
        CreatePayInOrder createPayInOrder = new CreatePayInOrder();
        createPayInOrder.setPayDO(payDO);
        createPayInOrder.setUserDO(userDO);
        BaseResponse<PaymentResult> paymentResultResp = paymentService.payIn(createPayInOrder);
        PaymentResult paymentResult = paymentResultResp.getResultData();
        payDO.setThirdUrl(paymentResult.getThirdPayUrl());
        payDO.setThirdNo(paymentResult.getThirdOrderNo());
        logger.info("支付渠道:{} 用户id:{},创建支付订单成功,内部订单号:{},第三方URL:{}", "daraja", param.getUserId(), orderNo,
            payDO.getThirdUrl());
        update(payDO);
        return BaseResponse.success(payDO);
    }

    //创建订单
    @Transactional(rollbackFor = Exception.class)
    public BaseResponse<PayDO> createOrderForBalance(LoanDetailRequest param, UserDO userDO) {
        logger.info("创建订单,参数:{}", param);
        logger.info("用户id:{},准备创建订单!", param.getUserId());
        //生成随机订单号
        String orderFirstNo = param.getUserId() + "p";
        String orderNo = orderFirstNo + StringRandom.getNumberAndLetterRandom(12 - orderFirstNo.length());
        //初始化支付参数
        PayDO payDO = new PayDO();
        payDO.setOrderNo(orderNo);
        payDO.setType(param.getType());
        payDO.setUserId(param.getUserId());
        payDO.setAmount(param.getPayAmount());
        payDO.setStatus(PayEnums.PayStatusEnum.PAID.getCode());
        payDO.setPayChannel("1");
        String payChannelBranch = RedisUtils.getValue(DictConsts.PAY_CHANNEL_BRANCH, String.class);
        payDO.setPayChannelBranch(payChannelBranch);
        payDO.setSaleId(userDO.getSaleId());
        payDO.setApplyStatus(PayEnums.PayApplyStatusEnum.PASS.getCode());
        if (StringUtils.isEmpty(param.getRefNo())) {
            //生成随机凭证号
            String refNo = "unit_ref_" + param.getUserId() + "_" + StringRandom.getNumberAndLetterRandom(10);
            payDO.setRefNo(refNo);
        } else {
            payDO.setRefNo(param.getRefNo());
        }

        int saveSuccess = save(payDO);
        if (saveSuccess <= 0) {
            return BaseResponse.fail(I18nEnum.PAYMENT_FAIL.getMsg());
        }
        UserDO tempUserDO = userService.get(userDO.getId());
        BigDecimal subBalance = tempUserDO.getBalance().subtract(param.getPayAmount());
        if (subBalance.doubleValue() < 0) {
            return BaseResponse.fail(I18nEnum.CREATE_ORDER_BALANCE_FAIL.getMsg());
        }
        //扣减余额
        boolean res = userService.reduceMoney(userDO.getId(), param.getPayAmount(), userDO.getVersion());
        if (!res) {
            return BaseResponse.fail(I18nEnum.CREATE_ORDER_BALANCE_FAIL.getMsg());
        }
        //购买会员成功
        userService.updateUserVIP(payDO.getUserId(), payDO.getType());
        logger.info("支付渠道:{} 用户id:{},创建支付订单成功,内部订单号:{}", "daraja", param.getUserId(), orderNo);
        return BaseResponse.success(payDO);
    }

    public String getThirdPayCallBackOrderNo(Map<String, Object> requestJson) {
        String orderNo = "";
        PaymentServiceEnum payChannelBranch = PaymentServiceEnum
            .getEnumByCode(requestJson.get("payChannelBranch").toString());
        requestJson.remove("payChannelBranch");
        JSONObject jsonObject = new JSONObject(JsonUtils.toJSONString(requestJson));
        switch (payChannelBranch) {
            case DARAJA:
                orderNo = jsonObject.getJSONObject("data").getString("unique_request_number");
                break;
            case FLUTTER_WAVE:
                orderNo = jsonObject.getString("thirdNo");
            default:
                break;
        }
        return orderNo;
    }

    /**
     * 提现订单状态同步
     * @param orderNo
     * @return
     */
    public BaseResponse<PayDO> getByPayOutOrder(String orderNo) {
        PayDO payDO = payDao.getByOrderNo(orderNo);
        if (payDO == null) {
            return BaseResponse.success();
        }
        //获取支付bean
        String payBeanName = PaymentServiceEnum.getBeanNameByCode(payDO.getPayChannelBranch());
        //找到支付实现
        PaymentService paymentService = (PaymentService) SpringContextUtils.getBean(payBeanName);
        QueryPayOutParam queryPayOutParam = new QueryPayOutParam();
        queryPayOutParam.setLocalOrderNo(orderNo);
        queryPayOutParam.setThirdOrderNo(payDO.getThirdNo());
        BaseResponse<PaymentResult> payoutOrder = paymentService.getPayoutOrder(queryPayOutParam);
        if (payoutOrder == null || payoutOrder.getResultData() == null) {
            return BaseResponse.success();
        }
        PaymentResult resultData = payoutOrder.getResultData();
        payDO.setThirdResponse(resultData.getDescription());
        payDO.setThirdStatus(resultData.getThirdOrderStatus());
        if (PayEnums.PayStatusEnum.PAID.getCode().equals(resultData.getStatus())) {
            //取现成功
            payDO.setStatus(PayEnums.PayStatusEnum.PAID.getCode());
            payService.payoutSuccess(payDO, payBeanName);
        } else if (PayEnums.PayStatusEnum.PAYING.getCode().equals(resultData.getStatus())
                && System.currentTimeMillis() - payDO.getCreateTime().getTime() < 86400000) {
            // do nothing
        } else {
            payDO.setStatus(PayEnums.PayStatusEnum.FAIL.getCode());
            payService.payoutFail(payDO, payBeanName);
        }
        return BaseResponse.success(payDO);
    }

    public BaseResponse callBackPayoutSuccessHandel(Map<String, Object> requestJson) {
        if (requestJson == null) {
            throw new BaseException("error params");
        }
        String orderNo = getThirdPayCallBackOrderNo(requestJson);
        PayDO payDO = payDao.getByOrderNo(orderNo);
        if (payDO == null) {
            return BaseResponse.success();
        }
        //获取支付bean
        String payBeanName = PaymentServiceEnum.getBeanNameByCode(payDO.getPayChannelBranch());
        //找到支付实现
        PaymentService paymentService = (PaymentService) SpringContextUtils.getBean(payBeanName);
        QueryPayOutParam queryPayOutParam = new QueryPayOutParam();
        queryPayOutParam.setLocalOrderNo(orderNo);
        BaseResponse<PaymentResult> payoutOrder = paymentService.getPayoutOrder(queryPayOutParam);
        if (payoutOrder == null || payoutOrder.getResultData() == null) {
            return BaseResponse.success();
        }
        PaymentResult resultData = payoutOrder.getResultData();

        if (!PayEnums.PayStatusEnum.PAID.getCode().equals(resultData.getStatus())) {
            return BaseResponse.success();
        }

        if (PayEnums.PayStatusEnum.PAID.getCode().equals(payDO.getStatus()) || PayEnums.PayStatusEnum.FAIL.getCode()
            .equals(payDO.getStatus())) {
            return BaseResponse.success();
        }
        //取现成功
        payService.payoutSuccess(payDO, payBeanName);
        return BaseResponse.success();
    }

    public BaseResponse callBackPayoutFailHandel(Map<String, Object> requestJson) {
        if (requestJson == null) {
            throw new BaseException(I18nEnum.PARAMS_FAIL.getMsg());
        }
        String orderNo = getThirdPayCallBackOrderNo(requestJson);
        //四方提现失败回调
        PayDO payDO = payDao.getByOrderNo(orderNo);
        if (payDO == null) {
            return BaseResponse.success();
        }
        if (PayEnums.PayStatusEnum.PAID.getCode().equals(payDO.getStatus()) || PayEnums.PayStatusEnum.FAIL.getCode()
            .equals(payDO.getStatus())) {
            return BaseResponse.success();
        }
        //获取支付bean
        String payBeanName = PaymentServiceEnum.getBeanNameByCode(payDO.getPayChannelBranch());
        //找到支付实现
        PaymentService paymentService = (PaymentService) SpringContextUtils.getBean(payBeanName);
        QueryPayOutParam queryPayOutParam = new QueryPayOutParam();
        queryPayOutParam.setLocalOrderNo(orderNo);
        BaseResponse<PaymentResult> payoutOrder = paymentService.getPayoutOrder(queryPayOutParam);
        if (payoutOrder == null || payoutOrder.getResultData() == null) {
            return BaseResponse.success();
        }
        PaymentResult resultData = payoutOrder.getResultData();
        if (!PayEnums.PayStatusEnum.FAIL.getCode().equals(resultData.getStatus())) {
            return BaseResponse.success();
        }
        payService.payoutFail(payDO, payBeanName);
        //        payService.payoutFail(payDO, resultData, payBeanName);
        return BaseResponse.success();
    }

    public PayVO getCountByTypeStatus(List<Integer> typeList, Integer status, LocalDate startDate, LocalDate endDate) {
        return getCountByTypeStatus(typeList, status, null, startDate, endDate);
    }

    public PayVO getCountByTypeStatus(List<Integer> typeList, Integer status, Long userId, LocalDate startDate,
        LocalDate endDate) {
        PayVO payVO = payDao.getCountByTypeStatus(typeList, status, userId, startDate, endDate);
        if (payVO == null) {
            return new PayVO();
        }
        return payVO;
    }

    public PayVO getCountByParams(Map<String, Object> params) {
        PayVO payVO = payDao.getCountByParams(params);
        if (payVO == null) {
            return new PayVO();
        }
        return payVO;
    }

    public void timeout() {
        Date yesterday = DateUtils.addDays(new Date(), -1);
        payDao.timeout(yesterday);
    }

    @Transactional
    public void payoutSuccess(PayDO payDO, String payBeanName) {
        //取现成功
        //更新订单状态
        //更新流水状态
        payDO.setThirdCallbackTime(new Date());
        updatePay(payDO);
        balanceService.updateStatusByOrderNo(payDO.getOrderNo(), StatusTypeEnum.SUCCESS.getCode());
        logger.info(
            "**** " + payBeanName + "提现成功通知 ****" + "用户id:" + payDO.getUserId() + "支付类型--" + PayEnums.PayTypeEnum
                .getDescByCode(payDO.getType()) + ",支付金额: " + payDO.getAmount());
    }

    @Transactional
    public void payoutFail(PayDO payDO, String payBeanName) {
        //取现失败
        //获取当前用户今日历史提现信息
        WithdrawalRecordDO localDateUserInfo = withdrawalRecordService.getLocalDateInfoByUserId(payDO.getUserId());
        if (localDateUserInfo != null && localDateUserInfo.getCount().compareTo(0) > 0) {
            //回退提现次数
            withdrawalRecordService.rollbackCountById(localDateUserInfo.getId());
        }
        //更新订单状态
        //更新流水状态
        payDO.setThirdCallbackTime(new Date());
        updatePay(payDO);
        balanceService.updateStatusByOrderNo(payDO.getOrderNo(), StatusTypeEnum.FAIL.getCode());
        //金额返还
        BalanceDO oldBalance = balanceService.getByOrderNo(payDO.getOrderNo());
        BalanceDO parent = new BalanceDO();
        parent.setAmount(oldBalance.getAmount().abs());
        parent.setType(BalanceTypeEnum.J.getCode());
        parent.setUserId(payDO.getUserId());
        parent.setWithdrawAmount(BigDecimal.ZERO);
        parent.setServiceFee(BigDecimal.ZERO);
        parent.setStatus(StatusTypeEnum.SUCCESS.getCode());
        parent.setCreateTime(new Date());
        parent.setUpdateTime(new Date());
        parent.setOrderNo(payDO.getOrderNo());
        balanceService.addBalanceDetail(parent);
        logger.info(
            "**** " + payBeanName + "提现失败通知 ****" + "用户id:" + payDO.getUserId() + "支付类型--" + PayEnums.PayTypeEnum
                .getDescByCode(payDO.getType()) + ",支付金额: " + payDO.getAmount());
    }

    @Transactional
    public void applyReject(PayDO payDO) {
        //取现失败
        //获取当前用户今日历史提现信息
        WithdrawalRecordDO localDateUserInfo = withdrawalRecordService.getLocalDateInfoByUserId(payDO.getUserId());
        if (localDateUserInfo != null && localDateUserInfo.getCount().compareTo(0) > 0) {
            //回退提现次数
            withdrawalRecordService.rollbackCountById(localDateUserInfo.getId());
        }
        //更新订单状态
        //更新流水状态
        updatePay(payDO);
        balanceService.updateStatusByOrderNo(payDO.getOrderNo(), StatusTypeEnum.FAIL.getCode());
        //金额返还
        BalanceDO oldBalance = balanceService.getByOrderNo(payDO.getOrderNo());
        BalanceDO parent = new BalanceDO();
        parent.setAmount(oldBalance.getAmount().abs());
        parent.setType(BalanceTypeEnum.J.getCode());
        parent.setUserId(payDO.getUserId());
        parent.setWithdrawAmount(BigDecimal.ZERO);
        parent.setServiceFee(BigDecimal.ZERO);
        parent.setStatus(StatusTypeEnum.SUCCESS.getCode());
        parent.setCreateTime(new Date());
        parent.setUpdateTime(new Date());
        parent.setOrderNo(payDO.getOrderNo());
        balanceService.addBalanceDetail(parent);
        logger.info("**** 审批驳回通知 ****" + "用户id:" + payDO.getUserId() + "支付类型--" + PayEnums.PayTypeEnum
            .getDescByCode(payDO.getType()) + ",支付金额: ₹" + payDO.getAmount() + ", 驳回原因" + payDO.getRemark());
    }
}
