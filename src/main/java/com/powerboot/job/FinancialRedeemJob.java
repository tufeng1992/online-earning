package com.powerboot.job;

import com.powerboot.common.JsonUtils;
import com.powerboot.consts.DictConsts;
import com.powerboot.service.FinancialOrderService;
import com.powerboot.service.FinancialProductService;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class FinancialRedeemJob {

    private Logger logger = LoggerFactory.getLogger(FinancialRedeemJob.class);

    @Autowired
    private FinancialOrderService financialOrderService;
    @Autowired
    private FinancialProductService financialProductService;

    @Scheduled(cron = "1 0 0 * * ?")
    public void doFinancialRedeem() {
        logger.info("理财订单到期自动赎回 定时器 start");

        List<Integer> orderIdList = financialOrderService.getExpiredOrder();

        logger.info("理财订单到期自动赎回 本次到期订单数：{}", orderIdList.size());

        int successCount = 0;
        int failCount = 0;

        List<Integer> failOrderList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(orderIdList)) {
            for (Integer orderId : orderIdList) {
                boolean result = financialProductService.redeemOrder(orderId);
                if (result) {
                    logger.info("理财订单到期自动赎回成功 订单id：{}", orderId);
                    successCount = successCount + 1;
                } else {
                    failCount = failCount + 1;
                    logger.info("理财订单到期自动赎回失败 订单id：{}", orderId);
                    failOrderList.add(orderId);
                }
            }
        }
        String text = "今日到期：" + orderIdList.size() + " 笔，处理成功：" + successCount + " 笔，处理失败：" + failCount + " 笔";
        if (failCount != 0) {
            text = text + "，失败订单id：" + JsonUtils.toJSONString(failOrderList);
        }
        logger.info(text);

        logger.info("理财订单到期自动赎回 定时器 end");
    }

}
