package com.powerboot.service;

import com.baomidou.mybatisplus.mapper.Condition;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.powerboot.config.BaseException;
import com.powerboot.dao.PayDao;
import com.powerboot.dao.PayNotifyDao;
import com.powerboot.domain.PayCertDO;
import com.powerboot.domain.PayDO;
import com.powerboot.domain.PayNotifyDO;
import com.powerboot.enums.PayEnums;
import com.powerboot.enums.PaymentServiceEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * 支付回调处理
 */
@Service
@Slf4j
public class PayNotifyService extends ServiceImpl<PayNotifyDao, PayNotifyDO> {

    @Autowired
    private PayNotifyDao payNotifyDao;

    @Autowired
    private PayService payService;

    @Autowired
    private PayDao payDao;

    @Autowired
    private PayCertService payCertService;

    /**
     * 检查回调验证格式并存储
     * @param notifyStr
     * @param phone
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public int checkAndSaveNotify(String notifyStr, String phone) {
        PayNotifyDO payNotifyDO = new PayNotifyDO();
        payNotifyDO.setPayNotifyStr(notifyStr);
        try {
            payNotifyDO.setPhone(parsePhone(notifyStr));
            if (StringUtils.isNotBlank(payNotifyDO.getPhone()) && "MobileMoney".equalsIgnoreCase(payNotifyDO.getPhone())) {
                payNotifyDO.setAmount(parseMobileMoneyAmount(notifyStr));
                payNotifyDO.setTransctionId(parseMobileMoneyTransactionId(notifyStr));
            } else {
                payNotifyDO.setAmount(parseAmount(notifyStr));
                payNotifyDO.setTransctionId(parseTransactionId(notifyStr));
            }
        } catch (Exception e) {
            log.error("解析回调格式异常", e);
            payNotifyDO.setNotifyStatus(4);
        }
        Condition condition = new Condition();
        condition.and().eq("pay_notify_str", notifyStr);
        int count = selectCount(condition);
        if (count > 0) {
            return 0;
        }
        if (StringUtils.isNotBlank(payNotifyDO.getTransctionId())) {
            condition = new Condition();
            condition.and().eq("transction_id", payNotifyDO.getTransctionId());
            count = selectCount(condition);
            if (count > 0) {
                return 0;
            }
        }
        return payNotifyDao.insert(payNotifyDO);
    }

    /**
     * 解析手机号
     * @param notifyStr
     * @return
     */
    private String parsePhone(String notifyStr) {
        int ghsIndex = notifyStr.toLowerCase().indexOf("\"phone\":\"");
        String ghsReplace = notifyStr.substring(ghsIndex + 9);
        String ghsAmount = ghsReplace.substring(0, ghsReplace.indexOf("\""));
        return ghsAmount.trim();
    }

    /**
     * 解析金额
     * @param notifyStr
     * @return
     */
    private BigDecimal parseAmount(String notifyStr) {
        int ghsIndex = notifyStr.toLowerCase().indexOf("ghs");
        String ghsReplace = notifyStr.substring(ghsIndex + 4);
        String ghsAmount = ghsReplace.substring(0, ghsReplace.indexOf(" "));
        return new BigDecimal(ghsAmount.trim());
    }

    /**
     * 解析金额
     * @param notifyStr
     * @return
     */
    private BigDecimal parseMobileMoneyAmount(String notifyStr) {
        int ghsIndex = notifyStr.toLowerCase().indexOf("received for");
        String ghsReplace = notifyStr.substring(ghsIndex + 12);
        String ghsReplace2 = ghsReplace.substring(ghsReplace.toLowerCase().indexOf("ghs") + 4);
        String ghsAmount = ghsReplace2.substring(0, ghsReplace2.indexOf(" "));
        return new BigDecimal(ghsAmount.trim());
    }

    /**
     * 解析事务id
     * @param notifyStr
     * @return
     */
    private String parseTransactionId(String notifyStr) {
        int rrnIndex = notifyStr.toLowerCase().indexOf(" rrn:");
        String rrnReplace = notifyStr.substring(rrnIndex + 6);
        String tid = rrnReplace.substring(0, rrnReplace.indexOf(" ")).trim();
        if (StringUtils.isNotBlank(tid) && tid.length() == 12) {
            return tid.substring(0, 11);
        }
        return tid;
    }

    /**
     * 解析事务id
     * @param notifyStr
     * @return
     */
    private String parseMobileMoneyTransactionId(String notifyStr) {
        int rrnIndex = notifyStr.toLowerCase().indexOf(" transaction id:");
        String rrnReplace = notifyStr.substring(rrnIndex + 16);
        return rrnReplace.substring(0, rrnReplace.indexOf(".")).trim();
    }

    /**
     * 校验通知和凭证
     * @param payNotifyDO
     * @return
     */
    public int checkNotifyAndCert(PayNotifyDO payNotifyDO) {
        log.info("checkNotifyAndCert  payNotifyDO:{}", payNotifyDO);
        try {
            //验证次数超过2次就直接失败
            if (payNotifyDO.getVerifyCount() >= 2) {
                payNotifyDO.setNotifyStatus(2);
            } else {
                payNotifyDO.setVerifyCount(payNotifyDO.getVerifyCount() + 1);

                Condition certCondition = new Condition();
                certCondition.and().eq("transction_id", payNotifyDO.getTransctionId());
                List<PayCertDO> payCertDOList = payCertService.selectList(certCondition);
                if (!CollectionUtils.isEmpty(payCertDOList)) {
                    PayCertDO payCert = payCertDOList.get(0);
                    if (StringUtils.isBlank(payCert.getBankName()) || !"mtn".equalsIgnoreCase(payCert.getBankName())) {
                        throw new BaseException("only support mtn bank");
                    }
                    PayDO payDO = payDao.getByOrderNo(payCert.getOrderNo());
                    if (null != payDO) {
                        if (!payDO.getAmount().equals(payNotifyDO.getAmount())) {
                            payNotifyDO.setNotifyStatus(3);
                        } else {
                            payDO.setThirdResponse("offline 回调成功");
                            completePay(payDO);
                            payNotifyDO.setNotifyStatus(1);
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("支付回调验证失败 verifyPayNotify ", e);
            payNotifyDO.setNotifyStatus(3);
        }
        return baseMapper.updateById(payNotifyDO);
    }


    /**
     * 校验通知和凭证
     * @param transctionId
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public int checkNotifyAndCert(String transctionId, PayCertDO payCertDO) {
        log.info("checkNotifyAndCert  payCertDO:{}", payCertDO);
        try {
            if (null == payCertDO || StringUtils.isBlank(transctionId)) {
                return 0;
            }
            if (StringUtils.isBlank(payCertDO.getBankName()) || !"mtn".equalsIgnoreCase(payCertDO.getBankName())) {
                return 0;
            }
            Condition condition = new Condition();
            condition.and()
                    .eq("transction_id", transctionId);
            List<PayNotifyDO> payNotifyDOList = selectList(condition);
            if (CollectionUtils.isEmpty(payNotifyDOList)) {
                return 0;
            }
            PayNotifyDO payNotifyDO = payNotifyDOList.get(0);
            if (2 == payNotifyDO.getNotifyStatus()) {
                payNotifyDO.setNotifyStatus(3);
                return baseMapper.updateById(payNotifyDO);
            } else if (3 == payNotifyDO.getNotifyStatus()) {
                return 0;
            } else if (1 == payNotifyDO.getNotifyStatus()) {
                return 1;
            }
            payNotifyDO.setVerifyCount(payNotifyDO.getVerifyCount() + 1);
            PayDO payDO = payDao.getByOrderNo(payCertDO.getOrderNo());
            if (null != payDO) {
                if (!payDO.getAmount().equals(payNotifyDO.getAmount())) {
                    payNotifyDO.setNotifyStatus(3);
                } else {
                    payDO.setThirdResponse("offline 回调成功");
                    completePay(payDO);
                    payNotifyDO.setNotifyStatus(1);
                }
                return baseMapper.updateById(payNotifyDO);
            }
        } catch (Exception e) {
            log.error("支付回调验证失败 verifyPayNotify ", e);
        }
        return 0;
    }

    /**
     * 完成订单
     * @param payDO
     */
    @Transactional(rollbackFor = Exception.class)
    public void completePay(PayDO payDO) {
        if (!PayEnums.PayStatusEnum.PAYING.getCode().equals(payDO.getStatus())) {
            return;
        }
        payDO.setStatus(PayEnums.PayStatusEnum.PAID.getCode());
        payService.update(payDO);
        if (PayEnums.PayTypeEnum.WITHDRAW.getCode().equals(payDO.getType())) {
            //获取支付bean
            String payBeanName = PaymentServiceEnum.getBeanNameByCode(payDO.getPayChannelBranch());
            payService.payoutSuccess(payDO, payBeanName);
        } else {
            payService.payInComplete(payDO);
        }
    }


    public static void main(String[] args) {
        String s = "{\"notifyStr\":\"Payment received for GHS 1.50 from Shadrack Frimpong Current Balance: GHS 4.00 . Available Balance: GHS 4.00. Reference: cisco. Transaction ID: 14114668636. TRANSACTION FEE: 0.00\",\"phone\":\"13127903999\",\"type\":\"1\"}";

    }
}
