package com.powerboot.job;

import com.powerboot.domain.PayDO;
import com.powerboot.service.PayService;
import com.powerboot.utils.HostUtil;
import java.util.List;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


@Component
public class PaymentTimeoutJob {

    @Autowired
    PayService payService;
    private Logger logger = LoggerFactory.getLogger(PaymentTimeoutJob.class);

    @Scheduled(cron = "0 5 0 * * ?")
    public void doTimeout() {
        logger.info("订单超过一天未支付 start");
        payService.timeout();
        logger.info("订单超过一天未支付 end");
    }

    @Scheduled(fixedRate = 600 * 1000, initialDelay = 10 * 1000)
    public void doGetOrderStatus() {
        String ip = HostUtil.getHostIP();
        logger.info("订单支付批量处理");
        List<PayDO> list = payService.getPayinOrder();
        if (CollectionUtils.isNotEmpty(list)) {
            for (PayDO payDO : list) {
                try {
                    payService.getByOrderNo(payDO.getOrderNo());
                } catch (Exception e) {
                    logger.error("订单支付批量异常,订单号:" + payDO.getOrderNo() + " 异常原因:" + e.getMessage());
                }
            }
        }
    }
}
