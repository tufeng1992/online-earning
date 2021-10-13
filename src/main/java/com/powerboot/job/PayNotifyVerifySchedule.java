package com.powerboot.job;

import com.baomidou.mybatisplus.mapper.Condition;
import com.powerboot.dao.PayDao;
import com.powerboot.domain.PayCertDO;
import com.powerboot.domain.PayDO;
import com.powerboot.domain.PayNotifyDO;
import com.powerboot.enums.PayEnums;
import com.powerboot.service.PayCertService;
import com.powerboot.service.PayNotifyService;
import com.powerboot.service.PayService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 支付通知验证任务
 */
@Component
@Slf4j
public class PayNotifyVerifySchedule {

    @Autowired
    private PayNotifyService payNotifyService;

    @Scheduled(cron = "0 */10 * * * ?")
    public void verifyPayNotify() {

        Condition condition = new Condition();
        condition.and().eq("notify_status", 0);
        List<PayNotifyDO> payNotifyDOList = payNotifyService.selectList(condition);
        if (CollectionUtils.isEmpty(payNotifyDOList)) {
            return;
        }
        payNotifyDOList.forEach(payNotifyDO -> payNotifyService.checkNotifyAndCert(payNotifyDO));
    }

}
