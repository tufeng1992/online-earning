package com.powerboot.service;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.powerboot.consts.DictConsts;
import com.powerboot.dao.UserTaskOrderMissionDao;
import com.powerboot.domain.UserDO;
import com.powerboot.domain.UserPrizeListDO;
import com.powerboot.domain.UserTaskOrderMissionDO;
import com.powerboot.enums.PrizeStatusEnum;
import com.powerboot.utils.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicReference;


@Service
@Slf4j
public class UserTaskOrderMissionService extends ServiceImpl<UserTaskOrderMissionDao, UserTaskOrderMissionDO> {

    @Autowired
    private UserService userService;

    @Resource(name = "commonExecutor")
    private ExecutorService commonExecutor;

    @Autowired
    private UserPrizeSourceService userPrizeSourceService;

    @Autowired
    private UserPrizeListService userPrizeListService;

    /**
     * 添加完成任务记录
     * @param userId
     * @param taskOrderNum
     * @param taskOrderAmount
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public int addMissionLog(Long userId, Integer taskOrderNum, BigDecimal taskOrderAmount) {
        UserTaskOrderMissionDO userTaskOrderMissionDO = new UserTaskOrderMissionDO();
        userTaskOrderMissionDO.setUserId(userId);
        userTaskOrderMissionDO.setTaskOrderNum(taskOrderNum);
        userTaskOrderMissionDO.setTaskOrderAmount(taskOrderAmount);
        int res = baseMapper.insert(userTaskOrderMissionDO);
        if (res > 0) {
            commonExecutor.execute(() -> {
                UserDO userDO = userService.get(userId);

                //添加上级邀请任务完成奖励
                addParentMissionPrize(userDO);

                //添加任务完成奖励
                addMissionPrize(userDO);
            });
        }
        return res;
    }

    /**
     * 添加任务完成奖励
     * 连续5日做满任务每日次数，可以获得一次抽奖机会。
     * 奖励机制：每次奖励金额为刷单金额的5%-8%。（该用户5日刷单总金额）
     * @param userDO
     */
    public void addMissionPrize(UserDO userDO) {
        if (null == userDO) {
            return;
        }
        //判断是否满足连续5天完成任务
        Set<Long> days = getContinuousDays(userDO.getId());
        if (days.size() > 4) {
            AtomicReference<BigDecimal> totalAmount = new AtomicReference<>(BigDecimal.ZERO);
            EntityWrapper<UserTaskOrderMissionDO> condition = new EntityWrapper<>();
            condition.and().in("id", days);
            List<UserTaskOrderMissionDO> list = baseMapper.selectList(condition);
            for (UserTaskOrderMissionDO taskOrderMissionDO : list) {
                totalAmount.set(totalAmount.get().add(taskOrderMissionDO.getTaskOrderAmount()));
            }
            //添加奖励信息
            userPrizeListService.addUserPrize(userDO.getId(),
                    PrizeStatusEnum.MISSION.getCode(), totalAmount.get(), RedisUtils.getString(DictConsts.PRIZE_RATIO_MISSION));
        }
    }

    /**
     * 添加上级邀请任务完成奖励
     * 每邀请好友10人，成功登录且连续完成3日任务次数后，可以获得一次抽奖机会。
     * 奖励机制：每次奖励计入规则的10位下级刷单金额总和的8%-10%（10位用户每天任务的总金额相加为刷单总金额）
     * @param userDO
     */
    public void addParentMissionPrize(UserDO userDO) {
        if (null == userDO || null == userDO.getParentId() || userDO.getParentId() == 1L) {
            return;
        }
        //查询上级的邀请用户人数
        List<Long> userIdList = userPrizeSourceService.getNoPrizeInviteUser(userDO.getParentId());
        if (userIdList.size() < 10) {
            return;
        }
        Map<Long, Set<Long>> completeUserList = Maps.newHashMap();
        userIdList.forEach(newUserId -> {
            //判断是否满足连续3天完成任务
            Set<Long> days = getContinuousDays(newUserId);
            if (days.size() > 2) {
                completeUserList.put(newUserId, days);
            }
        });
        if (completeUserList.size() < 10) {
            return;
        }
        AtomicReference<BigDecimal> totalAmount = new AtomicReference<>(BigDecimal.ZERO);
        //如果连续三天完成任务的用户数到10人，则获得抽奖机会
        completeUserList.values().forEach(days -> {
            EntityWrapper<UserTaskOrderMissionDO> condition = new EntityWrapper<>();
            condition.and().in("id", days);
            List<UserTaskOrderMissionDO> list = baseMapper.selectList(condition);
            for (UserTaskOrderMissionDO taskOrderMissionDO : list) {
                totalAmount.set(totalAmount.get().add(taskOrderMissionDO.getTaskOrderAmount()));
            }
        });
        //添加奖励信息
        UserPrizeListDO userPrizeListDO = userPrizeListService.addUserPrize(userDO.getParentId(),
                PrizeStatusEnum.INVITE.getCode(), totalAmount.get(), RedisUtils.getString(DictConsts.PRIZE_RATIO_INVITE));
        if (null != userPrizeListDO) {
            //添加用户奖励源信息
            completeUserList.keySet().forEach(newUserId -> userPrizeSourceService.addUserPrizeSource(userDO.getParentId(), newUserId, userPrizeListDO.getId()));
        }
    }

    /**
     * 获取用户连续完成任务的天数
     * @param userId
     * @return
     */
    public Set<Long> getContinuousDays(Long userId) {
        EntityWrapper<UserTaskOrderMissionDO> condition = new EntityWrapper<>();
        condition.and()
                .eq("user_id", userId);
        condition.orderBy("create_time", false);
        List<UserTaskOrderMissionDO> list = baseMapper.selectList(condition);
        Set<Long> res = Sets.newHashSet();
        if (list.size() <= 0) {
            return res;
        }
        for (int i = 0; i < list.size(); i++) {
            UserTaskOrderMissionDO current = list.get(i);
            int temp = i + 1;
            if (temp >= list.size()) {
                break;
            }
            UserTaskOrderMissionDO next = list.get(temp);
            long between = DateUtil.between(current.getCreateTime(), next.getCreateTime(), DateUnit.DAY);
            //如果间隔时间为1天表示时间连续，则连续天数加1，否则连续天数初始化回1天
            if (between == 1) {
                res.add(current.getId());
                res.add(next.getId());
            } else {
                res.clear();
                res.add(next.getId());
            }
        }
        return res;
    }


}
