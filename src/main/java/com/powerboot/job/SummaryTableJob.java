package com.powerboot.job;

import com.powerboot.consts.AmountConstants;
import com.powerboot.consts.DictConsts;
import com.powerboot.domain.BalanceDO;
import com.powerboot.domain.FinancialOrderDO;
import com.powerboot.domain.SummaryTableDO;
import com.powerboot.domain.UserDO;
import com.powerboot.enums.BalanceTypeEnum;
import com.powerboot.param.FinancialOrderParam;
import com.powerboot.response.PayVO;
import com.powerboot.service.BalanceService;
import com.powerboot.service.FinancialOrderService;
import com.powerboot.service.PayService;
import com.powerboot.service.SummaryTableService;
import com.powerboot.service.UserService;
import com.powerboot.utils.DateUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import com.powerboot.utils.HostUtil;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class SummaryTableJob {
    private Logger logger = LoggerFactory.getLogger(SummaryTableJob.class);
    @Autowired
    private BalanceService balanceService;
    @Autowired
    private UserService userService;
    @Autowired
    private SummaryTableService summaryTableService;
    @Autowired
    private PayService payService;
    @Autowired
    private FinancialOrderService financialOrderService;

    @Scheduled(cron = "0 2 0 * * ?")
    public void doSummaryTableJob() {
        logger.info("----------------------开始运行每日总表统计数据---------------------");
        //生成上一天的记录
        LocalDate lastDate = LocalDate.now().plusDays(-1);
        //获取会员数据
        List<UserDO> allVIP = userService.getAllVIP();
        //数据生成日期
        LocalDate generatedDate = lastDate;
        //会员总数
        Integer vipCount = 0;
        //vip1数量
        Integer vip1Count = 0;
        //vip2数量
        Integer vip2Count = 0;

        //vip3数量
        Integer vip3Count = 0;

        //vip4数量
        Integer vip4Count = 0;

        //vip5数量
        Integer vip5Count = 0;

        //有效vip数量
        Integer vipValidCount = 0;

        PayVO rechargeVo = payService.getCountByTypeStatus(Arrays.asList(1), 2,lastDate,LocalDate.now());
        PayVO withdrawVo = payService.getCountByTypeStatus(Arrays.asList(99), 2,lastDate,LocalDate.now());
        PayVO vipVo = payService.getCountByTypeStatus(Arrays.asList(2,3,4,5), 2,lastDate,LocalDate.now());
        PayVO firstRechargeVo = balanceService.getCountByTypeStatus(Arrays.asList(3),2,lastDate,LocalDate.now());
        //新增用户
        Integer localUserCount = userService.getUserCount(2,lastDate,LocalDate.now());
        //用户裂变
        Integer userReferral = userService.getUserReferral(2,lastDate,LocalDate.now());
        //客服推广
        Integer saleReferral = userService.getSaleReferral(2,lastDate,LocalDate.now());
        //充值笔数
        Integer rechargeCount = rechargeVo.getCount();
        //提现笔数
        Integer withdrawCount = withdrawVo.getCount();
        //充值金额
        BigDecimal rechargeAmount = rechargeVo.getAmount().multiply(AmountConstants.RECHARGE_RATE);
        //提现金额
        BigDecimal withdrawAmount = withdrawVo.getAmount().multiply(AmountConstants.WITHDRAW_REAL_RATE);
        //购买vip单数
        Integer vipPayCount = vipVo.getCount();
        //购买vip金额
        BigDecimal vipPayAmount = vipVo.getAmount().multiply(AmountConstants.RECHARGE_RATE);
        //首冲奖励
        BigDecimal firstRechargeAmount = firstRechargeVo.getAmount();

        //用户总余额
        BigDecimal vipBalanceCount = BigDecimal.ZERO;
        //佣金总额
        BigDecimal commissionsAmount = BigDecimal.ZERO;

        //理财转入
        BigDecimal financialProfitInAmount = BigDecimal.ZERO;
        //理财转出
        BigDecimal financialProfitOutAmount = BigDecimal.ZERO;
        //理财总金额
        BigDecimal financialProfitCountAmount = BigDecimal.ZERO;

        //理财收益总额
        BigDecimal financialProfitAmount = BigDecimal.ZERO;
        if (CollectionUtils.isNotEmpty(allVIP)) {
            vipCount = allVIP.size();
            vip1Count = allVIP.stream().filter(o -> o.getMemberLevel() == 1).collect(Collectors.toList()).size();
            vip2Count = allVIP.stream().filter(o -> o.getMemberLevel() == 2).collect(Collectors.toList()).size();
            vip3Count = allVIP.stream().filter(o -> o.getMemberLevel() == 3).collect(Collectors.toList()).size();
            vip4Count = allVIP.stream().filter(o -> o.getMemberLevel() == 4).collect(Collectors.toList()).size();
            vip5Count = allVIP.stream().filter(o -> o.getMemberLevel() == 5).collect(Collectors.toList()).size();
            vipValidCount = allVIP.stream().filter(o -> (o.getFirstRecharge() == 1)).collect(Collectors.toList()).size();
            vipBalanceCount = allVIP.stream().map(UserDO::getBalance).reduce(BigDecimal.ZERO, BigDecimal::add);
        }
        SummaryTableDO model = new SummaryTableDO();
        model.setGeneratedDate(generatedDate);
        model.setVipCount(vipCount);
        model.setVip1Count(vip1Count);
        model.setVip2Count(vip2Count);
        model.setVip3Count(vip3Count);
        model.setVip4Count(vip4Count);
        model.setVip5Count(vip5Count);
        model.setVipValidCount(vipValidCount);
        model.setRechargeAmount(rechargeAmount);
        model.setVipBalanceCount(vipBalanceCount);

        model.setLocalUserCount(localUserCount);
        model.setUserReferral(userReferral);
        model.setSaleReferral(saleReferral);
        model.setRechargeCount(rechargeCount);
        model.setWithdrawAmount(withdrawAmount);
        model.setWithdrawCount(withdrawCount);
        model.setVipPayAmount(vipPayAmount);
        model.setVipPayCount(vipPayCount);
        model.setFirstRechargeAmount(firstRechargeAmount);


        BigDecimal inAmountSum = financialOrderService.getInAmountSum(null);
        model.setFinancialProfitCountAmount(inAmountSum == null ? BigDecimal.ZERO : inAmountSum);
        //昨日佣金总额计算
        List<BalanceDO> balanceDOList = balanceService.getLastDayList(BalanceTypeEnum.A);
        if (CollectionUtils.isNotEmpty(balanceDOList)) {
            commissionsAmount = balanceDOList.stream().map(BalanceDO::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        }
        model.setCommissionsAmount(commissionsAmount);

        //昨日理财收益计算
        Date now = new Date();
        Date todayStart = DateUtils.setDateHMS(now,0,0,0);
        Date yesterdayStart = DateUtils.addDays(todayStart,-1);
        Date yesterdayEnd = DateUtils.setDateHMS(yesterdayStart,23,59,59);
        Date beforeYesterdayEnd = DateUtils.addDays(yesterdayEnd,-1);
        FinancialOrderParam financialOrderParam = new FinancialOrderParam();
        List<Integer> orderStatusList = new ArrayList<>();
        orderStatusList.add(1);
        orderStatusList.add(2);
        financialOrderParam.setOrderStatusList(orderStatusList);
        financialOrderParam.setBuyDateEnd(todayStart);
        financialOrderParam.setLastDateStart(yesterdayStart);
        List<FinancialOrderDO> financialOrderList = financialOrderService.getByParams(financialOrderParam);
        if(CollectionUtils.isNotEmpty(financialOrderList)){
            for(FinancialOrderDO orderDO : financialOrderList){
                financialProfitAmount = financialProfitAmount.add(orderDO.getDayInterest());
            }
        }
        model.setFinancialProfitAmount(financialProfitAmount);

        //昨日转入
        FinancialOrderParam financialOrderParam2 = new FinancialOrderParam();
        financialOrderParam2.setBuyDateStart(yesterdayStart);
        financialOrderParam2.setBuyDateEnd(yesterdayEnd);
        financialOrderParam2.setMinUserId(108);
        List<FinancialOrderDO> financialOrderList2 = financialOrderService.getByParams(financialOrderParam2);
        if(CollectionUtils.isNotEmpty(financialOrderList2)){
            for(FinancialOrderDO orderDO : financialOrderList2){
                financialProfitInAmount = financialProfitInAmount.add(orderDO.getAmount());
            }
        }
        model.setFinancialProfitInAmount(financialProfitInAmount);

        //昨日转出
        FinancialOrderParam financialOrderParam3 = new FinancialOrderParam();
        financialOrderParam3.setOrderStatusList(orderStatusList);
        financialOrderParam3.setLastDate(beforeYesterdayEnd);
        financialOrderParam3.setMinUserId(108);
        List<FinancialOrderDO> financialOrderList3 = financialOrderService.getByParams(financialOrderParam3);
        if(CollectionUtils.isNotEmpty(financialOrderList3)){
            for(FinancialOrderDO orderDO : financialOrderList3){
                financialProfitOutAmount = financialProfitOutAmount.add(orderDO.getAmount().add(orderDO.getTotalInterest()));
            }
        }
        FinancialOrderParam financialOrderParam4 = new FinancialOrderParam();
        financialOrderParam4.setOrderStatus(3);
        financialOrderParam4.setCalledTimeStart(yesterdayStart);
        financialOrderParam4.setCalledTimeEnd(yesterdayEnd);
        financialOrderParam4.setMinUserId(108);
        List<FinancialOrderDO> financialOrderList4 = financialOrderService.getByParams(financialOrderParam4);
        if(CollectionUtils.isNotEmpty(financialOrderList4)){
            for(FinancialOrderDO orderDO : financialOrderList4){
                financialProfitOutAmount = financialProfitOutAmount.add(orderDO.getAmount().subtract(orderDO.getCalledAmount()));
            }
        }
        model.setFinancialProfitOutAmount(financialProfitOutAmount);
        summaryTableService.save(model);
        logger.info("----------------------每日总表统计数据运行完成---------------------");
    }

}
