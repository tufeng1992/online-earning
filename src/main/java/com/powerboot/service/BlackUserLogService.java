package com.powerboot.service;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.powerboot.dao.BlackUserLogDao;
import com.powerboot.dao.SysUserDao;
import com.powerboot.domain.BlackUserLogDO;
import com.powerboot.domain.SysUserDO;
import com.powerboot.domain.UserDO;
import com.powerboot.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class BlackUserLogService extends ServiceImpl<BlackUserLogDao, BlackUserLogDO> {

    @Autowired
    private BlackUserLogDao blackUserLogDao;

    /**
     * 添加拉黑记录
     * @param userId
     * @param blackReason
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public int addBlackUserLog(Long userId, String blackReason, Long saleId) {
        BlackUserLogDO blackUserLogDO = new BlackUserLogDO();
        blackUserLogDO.setBlackUserId(userId);
        blackUserLogDO.setBlackReason(blackReason);
        blackUserLogDO.setOperUser("SYSTEM");
        blackUserLogDO.setSaleId(saleId);
        return blackUserLogDao.insert(blackUserLogDO);
    }



}
