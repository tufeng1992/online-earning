package com.powerboot.service;

import com.powerboot.config.BaseException;
import com.powerboot.dao.BalanceDao;
import com.powerboot.dao.UserDao;
import com.powerboot.domain.BalanceDO;
import com.powerboot.domain.PayDO;
import com.powerboot.domain.UserDO;
import com.powerboot.enums.BalanceTypeEnum;
import com.powerboot.enums.StatusTypeEnum;
import com.powerboot.response.PayVO;
import com.powerboot.utils.DateUtils;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class BalanceService {
    @Autowired
    private BalanceDao balanceDao;
    @Autowired
    private UserDao userDao;
    @Autowired
    private PayService payService;

    //增加余额流水记录，并修改余额，如遇是减少余额的操作，金额为负数
    @Transactional(rollbackFor = Exception.class)
    public int addBalanceDetail(BalanceDO balance) {
        UserDO userDO = userDao.get(balance.getUserId());
        if(userDO==null){
            throw new BaseException("login timeout, please login first");
        }
        balance.setSaleId(userDO.getSaleId()==null?1L:userDO.getSaleId());

        int count = balanceDao.save(balance);
        if (count == 1) {
            return userDao.updateMoney(balance.getUserId(), balance.getAmount());
        }
        return 0;
    }

    public BalanceDO get(Long id) {
        return balanceDao.get(id);
    }

    public BalanceDO getByOrderNo(String orderNo) {
        return balanceDao.getByOrderNo(orderNo);
    }

    public int updateStatusByOrderNo(String orderNo, Integer status) {
        return balanceDao.updateStatusByOrderNo(orderNo, status);
    }

    public List<BalanceDO> list(Map<String, Object> map) {
        return balanceDao.list(map);
    }

    public List<BalanceDO> getLastDayList(BalanceTypeEnum type) {
        Map<String, Object> map = new HashMap<>();
        Date lastDay = DateUtils.parseDate(DateUtils.addDays(new Date(), -1), DateUtils.SIMPLE_DATEFORMAT_YMD + " 00:00:00");
        Date endDay = DateUtils.parseDate(DateUtils.addDays(new Date(), 0), DateUtils.SIMPLE_DATEFORMAT_YMD + " 00:00:00");
        map.put("createTime", lastDay);
        map.put("endTime", endDay);
        map.put("type", type.getCode());
        return balanceDao.listByDate(map);
    }


    public List<BalanceDO> listByTypeAndUserId(Long userId, Long type) {
        List<BalanceDO> balanceDOS = balanceDao.listByTypeAndUserId(userId, type);

        List<PayDO> payDOList = payService.getWithdrawByUserId(userId);
        List<PayDO> withdrawUser = payDOList.stream().filter(i-> "-1".equals(i.getPayChannel())).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(withdrawUser)) {
            if (CollectionUtils.isEmpty(balanceDOS)) {
                balanceDOS = new ArrayList<>();
            }
            for (PayDO payDo: withdrawUser) {
                BalanceDO balanceDO = new BalanceDO();
                balanceDO.setOrderNo(payDo.getOrderNo());
                balanceDO.setUserId(userId);
                balanceDO.setStatus(payDo.getStatus());
                balanceDO.setCreateTime(payDo.getCreateTime());
                balanceDO.setWithdrawAmount(BigDecimal.ZERO.subtract(payDo.getAmount()));
                balanceDO.setAmount(balanceDO.getWithdrawAmount().divide(new BigDecimal("0.82")));
                balanceDO.setServiceFee(balanceDO.getAmount().multiply(new BigDecimal("0.18")));
                balanceDOS.add(balanceDO);
            }
        }

        if (CollectionUtils.isNotEmpty(balanceDOS)) {
            balanceDOS.forEach(o -> {
                o.setStatusDesc(StatusTypeEnum.getENDescByCode(o.getStatus()));
            });
        }

        return balanceDOS;
    }

    public PayVO getCountByTypeStatus(List<Integer> typeList, Integer status, LocalDate startDate, LocalDate endDate){
        PayVO result =  balanceDao.getCountByTypeStatus(typeList, status, startDate, endDate);
        if (result == null){
            return new PayVO();
        }
        return result;
    }

    public int count(Map<String, Object> map) {
        return balanceDao.count(map);
    }

    public int save(BalanceDO balance) {
        return balanceDao.save(balance);
    }

    public int update(BalanceDO balance) {
        return balanceDao.update(balance);
    }

    public int remove(Long id) {
        return balanceDao.remove(id);
    }

    public int batchRemove(Long[] ids) {
        return balanceDao.batchRemove(ids);
    }

}
