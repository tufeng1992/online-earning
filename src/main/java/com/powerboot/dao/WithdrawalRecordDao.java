package com.powerboot.dao;

import com.powerboot.domain.WithdrawalRecordDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;


@Mapper
public interface WithdrawalRecordDao {

    /**
     * 获取当天系统提现信息
     * @return
     */
    WithdrawalRecordDO getLocalDateSysInfo();

    /**
     * 获去指定时间的心态提现信息
     * @param localDate
     * @return
     */
    WithdrawalRecordDO getSysInfoByDate(@Param("date") LocalDate localDate);

    /**
     * 根据用户id获取当日提现信息
     * @param userId
     * @return
     */
    WithdrawalRecordDO getLocalDateInfoByUserId(@Param("userId") Long userId);

    /**
     * 根据用户id和时间获取提现信息
     * @param userId
     * @param localDate
     * @return
     */
    WithdrawalRecordDO getInfoByUserIdAndDate(@Param("userId") Long userId,@Param("date") LocalDate localDate);

    /**
     * 金额变动
     * @param withdrawalRecordDO
     * @return
     */
    int changeAmount(WithdrawalRecordDO withdrawalRecordDO);

    /**
     * 初始化当天系统提现记录
     * @param withdrawalRecordDO
     * @return
     */
    int initSysData(WithdrawalRecordDO withdrawalRecordDO);

    /**
     * 初始化当天用户提现记录
     * @param withdrawalRecordDO
     * @return
     */
    int initUserData(WithdrawalRecordDO withdrawalRecordDO);

    /**
     * count-1
     * @param id
     * @return
     */
    int rollbackCountById(@Param("id") Integer id);

}
