package com.powerboot.service;

import com.baomidou.mybatisplus.mapper.Condition;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.powerboot.config.BaseException;
import com.powerboot.dao.PayCertDao;
import com.powerboot.domain.PayCertDO;
import com.powerboot.domain.PayDO;
import com.powerboot.enums.PayEnums;
import com.powerboot.request.SubmitPayCertReq;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.concurrent.ExecutorService;

@Service
@Slf4j
public class PayCertService extends ServiceImpl<PayCertDao, PayCertDO> {

    @Autowired
    private PayCertDao payCertDao;

    @Resource(name = "commonExecutor")
    private ExecutorService commonExecutor;

    @Autowired
    private PayService payService;

    @Autowired
    private PayNotifyService payNotifyService;

    /**
     * 保存用户支付凭证
     * @param submitPayCertReq
     * @param userId
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public int savePayCert(SubmitPayCertReq submitPayCertReq, Long userId) {
        log.info("savePayCert submitPayCertReq:{}", submitPayCertReq);
        PayDO payDO = payService.getOrderNo(submitPayCertReq.getOrderNo());
        if (payDO.getStatus().equals(PayEnums.PayStatusEnum.PAID.getCode())) {
            throw new BaseException("order is paid");
        } else if (payDO.getStatus().equals(PayEnums.PayStatusEnum.PAYING.getCode())) {
            int count = 0;
            Condition condition;
            if ("MTN".equalsIgnoreCase(submitPayCertReq.getBankName())) {
                condition = new Condition();
                condition.and()
                        .eq("transction_id", submitPayCertReq.getTransctionId());
                count = payCertDao.selectCount(condition);
            }
            if (count > 0) {
                throw new BaseException("pay cert has been submitted");
            }
            condition = new Condition();
            condition.and()
                    .eq("order_no", submitPayCertReq.getOrderNo());
            count = payCertDao.selectCount(condition);
            if (count > 0) {
                throw new BaseException("pay cert has been submitted");
            }
        }

        PayCertDO payCertDO = new PayCertDO();
        BeanUtils.copyProperties(submitPayCertReq, payCertDO);
        payCertDO.setUserId(userId);
        try {
            return payCertDao.insert(payCertDO);
        } finally {
            commonExecutor.execute(() -> payNotifyService.checkNotifyAndCert(payCertDO.getTransctionId(), payCertDO));
        }
    }

}
