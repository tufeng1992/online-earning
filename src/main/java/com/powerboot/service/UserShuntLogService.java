package com.powerboot.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.powerboot.consts.CacheConsts;
import com.powerboot.dao.UserShuntLogDao;
import com.powerboot.domain.SaleShuntConfigDO;
import com.powerboot.domain.UserDO;
import com.powerboot.domain.UserShuntLogDO;
import com.powerboot.utils.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class UserShuntLogService extends ServiceImpl<UserShuntLogDao, UserShuntLogDO> {

    @Autowired
    private SaleShuntConfigService saleShuntConfigService;

    @Autowired
    private UserShuntLogDao userShuntLogDao;

    /**
     * 添加用户分流记录
     * @param userDO
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public int addUserShuntLog(UserDO userDO) {
        if (null == userDO) {
            return 0;
        }
        UserShuntLogDO userShuntLogDO = new UserShuntLogDO();
        userShuntLogDO.setUserId(userDO.getId());
        if (null == userDO.getSaleId() || 1L == userDO.getSaleId()) {
            userShuntLogDO.setShuntType(1);
            userShuntLogDO.setSaleId(getBalanceSaleId());
        } else {
            userShuntLogDO.setShuntType(3);
            userShuntLogDO.setSaleId(userDO.getSaleId());
        }
        return userShuntLogDao.insert(userShuntLogDO);
    }

    /**
     * 获取分流销售人员id
     * @return
     */
    private Long getBalanceSaleId() {
        /**
         * 轮询
         */
        Integer balanceIndex = RedisUtils.getInteger(CacheConsts.SHUNT_INDEX);
        if (null == balanceIndex) {
            balanceIndex = 0;
        }
        EntityWrapper<SaleShuntConfigDO> condition = new EntityWrapper<>();
        condition.and()
                .eq("shunt_switch", 1);
        condition.orderBy("shunt_order");
        List<SaleShuntConfigDO> saleShuntConfigDOList = saleShuntConfigService.selectList(condition);
        if (CollectionUtils.isEmpty(saleShuntConfigDOList)) {
            return 1L;
        }
        if (balanceIndex >= saleShuntConfigDOList.size()) {
            balanceIndex = 0;
        } else {
            balanceIndex++;
        }
        RedisUtils.setValue(CacheConsts.SHUNT_INDEX, balanceIndex.toString());
        SaleShuntConfigDO saleShuntConfigDO = saleShuntConfigDOList.get(balanceIndex);
        return saleShuntConfigDO.getSaleId();
    }

}
