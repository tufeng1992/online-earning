package com.powerboot.service;

import com.powerboot.config.BaseException;
import com.powerboot.consts.I18nEnum;
import com.powerboot.consts.TipConsts;
import com.powerboot.dao.WithdrawalRecordDao;
import com.powerboot.domain.WithdrawalRecordDO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

/**
 * Create  2020 - 11 - 03 8:32 下午
 */
@Service
public class WithdrawalRecordService {
    @Autowired
    WithdrawalRecordDao withdrawalRecordDao;

    /**
     * 获取当天系统提现信息
     *
     * @return
     */
    public WithdrawalRecordDO getLocalDateSysInfo() {
        return withdrawalRecordDao.getLocalDateSysInfo();
    }

    /**
     * 获去指定时间的心态提现信息
     *
     * @param localDate
     * @return
     */
    public WithdrawalRecordDO getSysInfoByDate(LocalDate localDate) {
        return withdrawalRecordDao.getSysInfoByDate(localDate);
    }

    /**
     * 根据用户id获取当日提现信息
     *
     * @param userId
     * @return
     */
    public WithdrawalRecordDO getLocalDateInfoByUserId(Long userId) {
        return withdrawalRecordDao.getLocalDateInfoByUserId(userId);
    }

    /**
     * 根据用户id和时间获取提现信息
     *
     * @param userId
     * @param localDate
     * @return
     */
    public WithdrawalRecordDO getInfoByUserIdAndDate(Long userId, LocalDate localDate) {
        return withdrawalRecordDao.getInfoByUserIdAndDate(userId, localDate);
    }

    /**
     * 金额变动
     *
     * @param withdrawalRecordDO
     * @return
     */
    @Transactional
    public int changeAmount(WithdrawalRecordDO withdrawalRecordDO) {
        if (withdrawalRecordDao.changeAmount(withdrawalRecordDO) > 0) {
            return 1;
        } else {
            throw new BaseException(I18nEnum.OPERATION_FAST.getMsg());
        }
    }

    /**
     * 初始化当天系统提现记录
     *
     * @param withdrawalRecordDO
     * @return
     */
    @Transactional
    public int initSysData(WithdrawalRecordDO withdrawalRecordDO) {
        if (withdrawalRecordDao.initSysData(withdrawalRecordDO) > 0) {
            return 1;
        } else {
            throw new BaseException(I18nEnum.OPERATION_FAST.getMsg());
        }
    }

    /**
     * 初始化当天用户提现记录
     *
     * @param withdrawalRecordDO
     * @return
     */
    @Transactional
    public int initUserData(WithdrawalRecordDO withdrawalRecordDO) {
        if (withdrawalRecordDao.initUserData(withdrawalRecordDO) > 0) {
            return 1;
        } else {
            throw new BaseException(I18nEnum.OPERATION_FAST.getMsg());
        }
    }

    @Transactional
    public int rollbackCountById(Integer id){
        return withdrawalRecordDao.rollbackCountById(id);
    }
}
