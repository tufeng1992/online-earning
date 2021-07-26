package com.powerboot.service;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.powerboot.dao.UserPrizeSourceDao;
import com.powerboot.domain.UserPrizeSourceDO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class UserPrizeSourceService extends ServiceImpl<UserPrizeSourceDao, UserPrizeSourceDO> {

    @Autowired
    private UserPrizeSourceDao userPrizeSourceDao;

    /**
     * 查询用户邀请用户（没有获得邀请奖励的用户）
     * @param userId
     * @return
     */
    public List<Long> getNoPrizeInviteUser(Long userId) {
        return userPrizeSourceDao.getNoPrizeInviteUser(userId);
    }

    /**
     * 添加用户奖励源信息
     * @param userId
     * @param prizeSourceUserId
     * @param userPrizeId
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public int addUserPrizeSource(Long userId, Long prizeSourceUserId, Long userPrizeId) {
        UserPrizeSourceDO userPrizeSourceDO = new UserPrizeSourceDO();
        userPrizeSourceDO.setUserId(userId);
        userPrizeSourceDO.setPrizeSourceUserId(prizeSourceUserId);
        userPrizeSourceDO.setUserPrizeId(userPrizeId);
        return baseMapper.insert(userPrizeSourceDO);
    }


}
