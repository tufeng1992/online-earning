package com.powerboot.service;

import com.powerboot.base.BaseResponse;
import com.powerboot.config.BaseException;
import com.powerboot.consts.CacheConsts;
import com.powerboot.consts.I18nEnum;
import com.powerboot.consts.TipConsts;
import com.powerboot.domain.BalanceDO;
import com.powerboot.domain.PayDO;
import com.powerboot.domain.UserDO;
import com.powerboot.domain.WithdrawalRecordDO;
import com.powerboot.enums.BalanceTypeEnum;
import com.powerboot.enums.PayEnums;
import com.powerboot.enums.PaymentServiceEnum;
import com.powerboot.enums.StatusTypeEnum;
import com.powerboot.response.pay.PaymentResult;
import com.powerboot.utils.DateUtils;
import com.powerboot.utils.RedisUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Create  2021 - 01 - 22 3:34 下午
 */
@Service
public class CallBackService {

    private static Logger logger = LoggerFactory.getLogger(CallBackService.class);

    @Autowired
    PayService payService;
    @Autowired
    private UserService userService;
    @Autowired
    private BalanceService balanceService;
    @Autowired
    WithdrawalRecordService withdrawalRecordService;

    @Transactional
    public BaseResponse paySuccess(String orderNo, PaymentResult resultData) {
        PayDO payDO = payService.getOrderNo(orderNo);
        String payBeanName = PaymentServiceEnum.getBeanNameByCode(payDO.getPayChannelBranch());
        if (!PayEnums.PayTypeEnum.RECHARGE.getCode().equals(payDO.getType())
                && !PayEnums.PayTypeEnum.PAY_VIP2.getCode().equals(payDO.getType())
                && !PayEnums.PayTypeEnum.PAY_VIP3.getCode().equals(payDO.getType())
                && !PayEnums.PayTypeEnum.PAY_VIP4.getCode().equals(payDO.getType())
                && !PayEnums.PayTypeEnum.PAY_VIP5.getCode().equals(payDO.getType())) {
            throw new BaseException(I18nEnum.UNKNOWN_PAY_FAIL.getMsg());
        }
        if (!PayEnums.PayStatusEnum.PAYING.getCode().equals(payDO.getStatus()) && !PayEnums.PayStatusEnum.TIMEOUT.getCode().equals(payDO.getStatus())) {
            return BaseResponse.success();
        }
        payDO.setThirdNo(resultData.getThirdOrderNo());
        payDO.setThirdStatus(resultData.getThirdOrderStatus());
        payDO.setStatus(resultData.getStatus());
        String payFailDesc = resultData.getDescription();
        payDO.setThirdCallbackTime(new Date());
        int success = payService.updatePay(payDO);
        BigDecimal relPayAmount = payDO.getAmount() == null ? new BigDecimal("0.00") : payDO.getAmount();
        if (success > 0 && payDO.getStatus().equals(PayEnums.PayStatusEnum.PAID.getCode())) {
            //调用更新用户支付成功状态
            if (PayEnums.PayTypeEnum.RECHARGE.getCode().equals(payDO.getType())) {
                logger.info("充值用户增加余额,userid:" + payDO.getUserId() + " 订单号:" + payDO.getOrderNo() + " 订单状态:" + payDO.getStatus() + " 新增余额:" + relPayAmount);
                //充值成功
                BalanceDO balanceDO = new BalanceDO();
                balanceDO.setAmount(relPayAmount);
                balanceDO.setType(BalanceTypeEnum.F.getCode());
                balanceDO.setUserId(payDO.getUserId());
                balanceDO.setWithdrawAmount(BigDecimal.ZERO);
                balanceDO.setServiceFee(BigDecimal.ZERO);
                balanceDO.setStatus(StatusTypeEnum.SUCCESS.getCode());
                balanceDO.setCreateTime(new Date());
                balanceDO.setUpdateTime(new Date());
                balanceDO.setOrderNo(payDO.getOrderNo());
                balanceService.addBalanceDetail(balanceDO);
                //判断是否首冲
                UserDO userDO = userService.get(payDO.getUserId());
                if (userDO != null && userDO.getFirstRecharge() == 0 && relPayAmount.compareTo(new BigDecimal("300")) >= 0
                        && userDO.getParentId() != null) {
                    //给上级返现50
                    BalanceDO parent = new BalanceDO();
                    parent.setAmount(new BigDecimal("50"));
                    parent.setType(BalanceTypeEnum.C.getCode());
                    parent.setUserId(userDO.getParentId());
                    parent.setWithdrawAmount(BigDecimal.ZERO);
                    parent.setServiceFee(BigDecimal.ZERO);
                    parent.setStatus(StatusTypeEnum.SUCCESS.getCode());
                    parent.setCreateTime(new Date());
                    parent.setUpdateTime(new Date());
                    parent.setOrderNo(payDO.getOrderNo());
                    balanceService.addBalanceDetail(parent);
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
                throw new BaseException(I18nEnum.UNKNOWN_PAY_FAIL.getMsg());
            }
            logger.info("**** {}支付{} ****  用户id:{},支付{} {},金额: ₹ {}   {}", payBeanName, PayEnums.PayStatusEnum.getDescByCode(payDO.getStatus()), payDO.getUserId(), PayEnums.PayStatusEnum.getDescByCode(payDO.getStatus()), PayEnums.PayTypeEnum.getDescByCode(payDO.getType()), relPayAmount,payFailDesc);
        } else {
            throw new BaseException("error");
        }
        return BaseResponse.success();
    }

    @Transactional
    public BaseResponse payFail(String orderNo, PaymentResult resultData) {
        PayDO payDO = payService.getOrderNo(orderNo);
        BigDecimal relPayAmount = payDO.getAmount() == null ? new BigDecimal("0.00") : payDO.getAmount();
        String payBeanName = PaymentServiceEnum.getBeanNameByCode(payDO.getPayChannelBranch());
        if (!PayEnums.PayTypeEnum.RECHARGE.getCode().equals(payDO.getType())
                && !PayEnums.PayTypeEnum.PAY_VIP2.getCode().equals(payDO.getType())
                && !PayEnums.PayTypeEnum.PAY_VIP3.getCode().equals(payDO.getType())
                && !PayEnums.PayTypeEnum.PAY_VIP4.getCode().equals(payDO.getType())
                && !PayEnums.PayTypeEnum.PAY_VIP5.getCode().equals(payDO.getType())) {
            throw new BaseException(I18nEnum.UNKNOWN_PAY_FAIL.getMsg());
        }
        if (!PayEnums.PayStatusEnum.PAYING.getCode().equals(payDO.getStatus()) && !PayEnums.PayStatusEnum.TIMEOUT.getCode().equals(payDO.getStatus())) {
            return BaseResponse.success();
        }
        payDO.setThirdNo(resultData.getThirdOrderNo());
        payDO.setThirdStatus(resultData.getThirdOrderStatus());
        payDO.setStatus(resultData.getStatus());
        String payFailDesc = resultData.getDescription();
        payDO.setThirdCallbackTime(new Date());
        payService.updatePay(payDO);
        if (payDO.getStatus().equals(PayEnums.PayStatusEnum.FAIL.getCode())) {
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
        logger.info("**** {}支付{} ****  用户id:{},支付{} {},金额: ₹ {}   {}", payBeanName, PayEnums.PayStatusEnum.getDescByCode(payDO.getStatus()), payDO.getUserId(), PayEnums.PayStatusEnum.getDescByCode(payDO.getStatus()), PayEnums.PayTypeEnum.getDescByCode(payDO.getType()), relPayAmount,payFailDesc);
        return BaseResponse.success();
    }

    @Transactional
    public BaseResponse payoutSuccess(String orderNo, PaymentResult resultData) {
        PayDO payDO = payService.getOrderNo(orderNo);
        if (payDO == null) {
            return BaseResponse.success();
        }
        String payBeanName = PaymentServiceEnum.getBeanNameByCode(payDO.getPayChannelBranch());

        if (!PayEnums.PayStatusEnum.PAID.getCode().equals(resultData.getStatus())) {
            return BaseResponse.success();
        }
        if (PayEnums.PayStatusEnum.PAID.getCode().equals(payDO.getStatus()) || PayEnums.PayStatusEnum.FAIL.getCode().equals(payDO.getStatus())) {
            return BaseResponse.success();
        }
        //取现成功
        //更新订单状态
        //更新流水状态
        payDO.setStatus(resultData.getStatus());
        payDO.setThirdCallbackTime(new Date());
        payDO.setThirdStatus(resultData.getThirdOrderStatus());
        payDO.setThirdNo(resultData.getThirdOrderNo());
        payService.updatePay(payDO);
        balanceService.updateStatusByOrderNo(payDO.getOrderNo(), StatusTypeEnum.SUCCESS.getCode());
        logger.info("**** " + payBeanName + "提现成功通知 ****" + "用户id:" + payDO.getUserId() + "支付类型--" + PayEnums.PayTypeEnum.getDescByCode(payDO.getType()) + ",支付金额: ₹" + payDO.getAmount());
        return BaseResponse.success();
    }

    @Transactional
    public BaseResponse payoutFail(String orderNo, PaymentResult resultData) {
        //四方提现失败回调
        PayDO payDO = payService.getOrderNo(orderNo);
        if (payDO == null) {
            return BaseResponse.success();
        }
        if (PayEnums.PayStatusEnum.PAID.getCode().equals(payDO.getStatus()) || PayEnums.PayStatusEnum.FAIL.getCode().equals(payDO.getStatus())) {
            return BaseResponse.success();
        }
        String payBeanName = PaymentServiceEnum.getBeanNameByCode(payDO.getPayChannelBranch());
        if (!PayEnums.PayStatusEnum.FAIL.getCode().equals(resultData.getStatus())) {
            return BaseResponse.success();
        }
        //取现失败
        //获取当前用户今日历史提现信息
        WithdrawalRecordDO localDateUserInfo = withdrawalRecordService.getLocalDateInfoByUserId(payDO.getUserId());
        if (localDateUserInfo != null && localDateUserInfo.getCount().compareTo(0) > 0) {
            //回退提现次数
            withdrawalRecordService.rollbackCountById(localDateUserInfo.getId());
        }
        //更新订单状态
        //更新流水状态
        payDO.setStatus(resultData.getStatus());
        payDO.setThirdCallbackTime(new Date());
        payDO.setThirdStatus(resultData.getThirdOrderStatus());
        payDO.setThirdNo(resultData.getThirdOrderNo());
        payService.updatePay(payDO);
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
        logger.info("**** " + payBeanName + "提现失败通知 ****" + "用户id:" + payDO.getUserId() + "支付类型--" + PayEnums.PayTypeEnum.getDescByCode(payDO.getType()) + ",支付金额: ₹" + payDO.getAmount());

        return BaseResponse.success();
    }
}
