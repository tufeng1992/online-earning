package com.powerboot.service;

import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.pagination.PageHelper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.powerboot.config.BaseException;
import com.powerboot.consts.DictConsts;
import com.powerboot.consts.I18nEnum;
import com.powerboot.dao.UserPrizeListDao;
import com.powerboot.domain.BalanceDO;
import com.powerboot.domain.UserDO;
import com.powerboot.domain.UserPrizeListDO;
import com.powerboot.enums.BalanceTypeEnum;
import com.powerboot.enums.PrizeStatusEnum;
import com.powerboot.enums.StatusTypeEnum;
import com.powerboot.request.PrizeListRequest;
import com.powerboot.utils.RedisUtils;
import com.powerboot.utils.StringRandom;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class UserPrizeListService extends ServiceImpl<UserPrizeListDao, UserPrizeListDO> {

    @Autowired
    private UserService userService;

    @Autowired
    private BalanceService balanceService;

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
        userPrizeListDO.setPrizeStatus(PrizeStatusEnum.STATUS_INIT.getCode());
        userPrizeListDO.setPrizeSource(prizeSource);
        userPrizeListDO.setPrizeBaseAmount(prizeBaseAmount);
        userPrizeListDO.setPrizeRatio(prizeRatio);
        userPrizeListDO.setSaleId(userDO.getSaleId());
        baseMapper.insert(userPrizeListDO);
        return userPrizeListDO;
    }



    /**
     * 消费奖励信息
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public BigDecimal comsumerUserPrize(Long userId, Long prizeId) {
        EntityWrapper<UserPrizeListDO> condition = new EntityWrapper<>();
        condition.and()
                .eq("id", prizeId)
                .eq("user_id", userId);
        List<UserPrizeListDO> prizeListDOList = baseMapper.selectList(condition);
        if (CollectionUtils.isEmpty(prizeListDOList)) {
            throw new BaseException(I18nEnum.COMSUMER_PRIZE_INEXISTENCE.getMsg());
        }
        UserPrizeListDO userPrizeListDO = prizeListDOList.get(0);
        if (!PrizeStatusEnum.STATUS_INIT.getCode().equals(userPrizeListDO.getPrizeStatus())) {
            throw new BaseException(I18nEnum.PRIZE_COMSUMERD.getMsg());
        }
        BigDecimal baseAmount = userPrizeListDO.getPrizeBaseAmount();
        String[] range = userPrizeListDO.getPrizeRatio().split("-");
        BigDecimal min = baseAmount.multiply(new BigDecimal(range[0]));
        BigDecimal max = baseAmount.multiply(new BigDecimal(range[1]));
        BigDecimal randomAmount = RandomUtil.randomBigDecimal(min, max).setScale(2, BigDecimal.ROUND_DOWN);
        userPrizeListDO.setPrizeGetAmount(randomAmount);
        userPrizeListDO.setPrizeStatus(PrizeStatusEnum.STATUS_USED.getCode());
        userPrizeListDO.setPrizeGetTime(new Date());

        baseMapper.updateById(userPrizeListDO);
        addBalance(randomAmount, userId);
        return randomAmount;
    }

    /**
     * 添加余额
     * @param payAmount
     * @param userId
     */
    private void addBalance(BigDecimal payAmount, Long userId) {
        Date now = new Date();
        BalanceDO sendBalanceDO = new BalanceDO();
        sendBalanceDO.setAmount(payAmount);
        sendBalanceDO.setType(BalanceTypeEnum.P.getCode());
        sendBalanceDO.setUserId(userId);
        sendBalanceDO.setWithdrawAmount(BigDecimal.ZERO);
        sendBalanceDO.setServiceFee(BigDecimal.ZERO);
        sendBalanceDO.setStatus(StatusTypeEnum.SUCCESS.getCode());
        sendBalanceDO.setCreateTime(now);
        sendBalanceDO.setUpdateTime(now);
        //生成随机订单号
        String orderFirstNo = userId + "p";
        String orderNo = orderFirstNo + StringRandom.getNumberAndLetterRandom(12 - orderFirstNo.length());
        sendBalanceDO.setOrderNo(orderNo);
        balanceService.addBalanceDetail(sendBalanceDO);
    }

    /**
     * 查询奖励列表
     * @param userId
     * @param param
     * @return
     */
    public List<UserPrizeListDO> selectPrizeList(Long userId, PrizeListRequest param) {
        EntityWrapper<UserPrizeListDO> condition = new EntityWrapper<>();
        condition.and()
                .eq("user_id", userId);
        if (StringUtils.isNotBlank(param.getPrizeStatus())) {
            condition.eq("prize_status", param.getPrizeStatus());
        }
        List<UserPrizeListDO> list = baseMapper.selectList(condition);
        list.forEach(this::populateUserName);
        return list;
    }

    /**
     * 查询轮播奖励列表
     * @return
     */
    public List<UserPrizeListDO> selectBannerPrizeList() {
        EntityWrapper<UserPrizeListDO> condition = new EntityWrapper<>();
        condition.and()
                .eq("prize_status", PrizeStatusEnum.STATUS_USED.getCode());
        condition.orderBy("create_time", false);
        List<UserPrizeListDO> list = baseMapper.selectPage(new RowBounds(0, 10), condition);
        list.forEach(this::populateUserName);
        return list;
    }

    /**
     * 查询奖励列表数量
     * @param userId
     * @param param
     * @return
     */
    public Integer selectPrizeListCount(Long userId, PrizeListRequest param) {
        EntityWrapper<UserPrizeListDO> condition = new EntityWrapper<>();
        condition.and()
                .eq("user_id", userId);
        if (StringUtils.isNotBlank(param.getPrizeStatus())) {
            condition.eq("prize_status", param.getPrizeStatus());
        }
        return baseMapper.selectCount(condition);
    }

    /**
     * 填充用户姓名
     * @param userPrizeListDO
     */
    private void populateUserName(UserPrizeListDO userPrizeListDO) {
        UserDO userDO = userService.get(userPrizeListDO.getUserId());
        if (null != userDO) {
            String name = userDO.getName();
            if (StringUtils.isBlank(name)) {
                name = "***" + userDO.getMobile().substring(3, userDO.getMobile().length() - 4) + "***";
            }
            userPrizeListDO.setUserName(name);
        }
    }

}
