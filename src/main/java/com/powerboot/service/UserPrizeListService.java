package com.powerboot.service;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.powerboot.dao.UserPrizeListDao;
import com.powerboot.domain.UserDO;
import com.powerboot.domain.UserPrizeListDO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@Slf4j
public class UserPrizeListService extends ServiceImpl<UserPrizeListDao, UserPrizeListDO> {

    @Autowired
    private UserService userService;

    /**
     * 添加奖励信息
     * @param userId
     * @param prizeSource
     * @param prizeBaseAmount
     * @param prizeRatio
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public UserPrizeListDO addUserPrize(Long userId, Integer prizeSource, BigDecimal prizeBaseAmount, String prizeRatio) {
        if (null == userId || userId.equals(1L)) {
            return null;
        }
        UserDO userDO = userService.get(userId);
        if (null == userDO) {
            return null;
        }
        UserPrizeListDO userPrizeListDO = new UserPrizeListDO();
        userPrizeListDO.setUserId(userId);
        userPrizeListDO.setPrizeSource(prizeSource);
        userPrizeListDO.setPrizeBaseAmount(prizeBaseAmount);
        userPrizeListDO.setPrizeRatio(prizeRatio);
        baseMapper.insert(userPrizeListDO);
        return userPrizeListDO;
    }



    /**
     * 消费奖励信息
     * @return
     */
    public UserPrizeListDO comsumerUserPrize() {
        return null;
    }

}
