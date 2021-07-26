package com.powerboot.service;

import com.powerboot.common.JsonUtils;
import com.powerboot.consts.DictAccount;
import com.powerboot.dao.BalanceDao;
import com.powerboot.domain.BalanceDO;
import com.powerboot.domain.UserDO;
import com.powerboot.enums.BalanceTypeEnum;
import com.powerboot.enums.StatusTypeEnum;
import com.powerboot.response.TeamMemberDto;
import com.powerboot.response.TodayResultDto;
import com.powerboot.utils.DateUtils;
import com.powerboot.utils.RedisUtils;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

import com.powerboot.dao.OrderDao;
import com.powerboot.domain.OrderDO;
import org.springframework.util.StopWatch;
import org.springframework.util.StringUtils;


@Service
public class OrderService {
    public static Logger logger = LoggerFactory.getLogger(OrderService.class);
    @Autowired
    private OrderDao orderDao;
    @Autowired
    private BalanceDao balanceDao;
    @Autowired
    private UserService userService;
    @Autowired
    EhcacheService ehcacheService;

    @Autowired
    private UserTaskOrderMissionService userTaskOrderMissionService;

    //获取今日已刷单列表
    public List<OrderDO> getTodayList(Long userId) {
        Date today = DateUtils.parseDate(new Date(), DateUtils.SIMPLE_DATEFORMAT_YMD + " 00:00:00");
        HashMap<String, Object> todayMap = new HashMap<>();
        todayMap.put("userId", userId);
        todayMap.put("createTime", today);
        List<OrderDO> orderDOList=list(todayMap);
        if(CollectionUtils.isEmpty(orderDOList)){
            return  new ArrayList<>();
        }
        return orderDOList;
    }

    //获取大于等于昨日已刷单列表
    public List<OrderDO> getYesterdayList(Long userId) {
        Date day = DateUtils.parseDate(DateUtils.addDays(new Date(), -1), DateUtils.SIMPLE_DATEFORMAT_YMD + " 00:00:00");
        HashMap<String, Object> todayMap = new HashMap<>();
        todayMap.put("userId", userId);
        todayMap.put("createTime", day);
        List<OrderDO> orderDOList=list(todayMap);

        if(CollectionUtils.isEmpty(orderDOList)){
            return  new ArrayList<>();
        }
        return orderDOList;
    }

    //获取今日团队已刷单列表
    public List<OrderDO> getTodayTeamList() {
        Date day = DateUtils.parseDate(new Date(), DateUtils.SIMPLE_DATEFORMAT_YMD + " 00:00:00");
        String idList = RedisUtils.getValue("TodayTeamList" + day, String.class);
        List<Long> ids = JsonUtils.parseArray(idList, Long.class);
        if (CollectionUtils.isEmpty(ids)) {
            ids = new ArrayList<>();
            ids.add(0L);
        }

        return orderDao.getByIds(ids);
    }

    //获取大于等于昨日团队已刷单列表
    public List<OrderDO> getYesterdayTeamList() {
        Date day = DateUtils.parseDate(DateUtils.addDays(new Date(), -1), DateUtils.SIMPLE_DATEFORMAT_YMD + " 00:00:00");
        String idList = RedisUtils.getValue("YesterdayTeamList" + day, String.class);
        List<Long> ids = JsonUtils.parseArray(idList, Long.class);
        if (CollectionUtils.isEmpty(ids)) {
            ids = new ArrayList<>();
            ids.add(0L);
        }
        return orderDao.getByIds(ids);
    }

    //获取今日详情
    public TodayResultDto getTodayResult(UserDO userDO) {
        TodayResultDto todayResultDto = new TodayResultDto();

        todayResultDto.setTotalAssets(userDO.getBalance());
        List<OrderDO> todayList = getTodayList(userDO.getId());
        List<OrderDO> yesterdayList = getYesterdayList(userDO.getId());

        todayResultDto.setTeamBenefitsEarnings(BigDecimal.ZERO);

//        String teamBenefits = RedisUtils.getValue("today" + day + userDO.getId().toString(), String.class);
//        if (teamBenefits != null) {
//            todayResultDto.setTeamBenefitsEarnings(new BigDecimal(teamBenefits));
//        } else {
//            List<OrderDO> todayTeamList = getTodayTeamList();
//            BigDecimal todayTeamAmount = BigDecimal.ZERO;
//            todayTeamList.forEach(o -> {
//                if (userDO.getId().equals(o.getOneId())) {
//                    todayTeamAmount.add(o.getOneAmount());
//                }
//                if (userDO.getId().equals(o.getTwoId())) {
//                    todayTeamAmount.add(o.getTwoAmount());
//                }
//                if (userDO.getId().equals(o.getThreeId())) {
//                    todayTeamAmount.add(o.getThreeAmount());
//                }
//            });
//            todayResultDto.setTeamBenefitsEarnings(todayTeamAmount);
//            RedisUtils.setValue("today" + day + userDO.getId().toString(), todayResultDto.getTeamBenefitsEarnings().toString(), 3600);
//        }


        todayResultDto.setYesterdayTeamEarnings(BigDecimal.ZERO);



        HashMap<Integer, List<Integer>> vipMap = ehcacheService.getVipInfo();

        yesterdayList.removeAll(todayList);
        //获取今日已刷单数量

        if(CollectionUtils.isNotEmpty(todayList)){
            logger.info("checkNull vipMap:"+ vipMap.get(userDO.getMemberLevel()));
            todayResultDto.setTotalOrder(todayList.size() + "/" + vipMap.get(userDO.getMemberLevel()).get(3));
        }else {
            logger.info("checkNull userId:"+userDO.getId());
            todayResultDto.setTotalOrder(0 + "/" + vipMap.get(userDO.getMemberLevel()).get(3));
        }
        todayResultDto.setTotalEarnings(todayList.stream().map(OrderDO::getProductCommission).reduce(BigDecimal.ZERO, BigDecimal::add));
        todayResultDto.setYesterdayEarnings(yesterdayList.stream().map(OrderDO::getProductCommission).reduce(BigDecimal.ZERO, BigDecimal::add));

        return todayResultDto;
    }

    //根据用户id获取收益详情
    public TeamMemberDto getDetailByUserId(UserDO userDO, Integer lv, Long upUserid) {
        TeamMemberDto teamMemberDto = new TeamMemberDto();
        teamMemberDto.setName(userDO.getNikeName());
        teamMemberDto.setCreateTime(userDO.getCreateTime());
        List<UserDO> oneUserList = userService.getUserByParentId(userDO.getId());
        teamMemberDto.setNumber(oneUserList.size());

        HashMap<String, Object> map = new HashMap<>();
        map.put("userId", userDO.getId());
        map.put("type", BalanceTypeEnum.F.getCode());
        map.put("status", StatusTypeEnum.SUCCESS.getCode());
        List<BalanceDO> balanceDOList = balanceDao.list(map);
        teamMemberDto.setRecharge(balanceDOList.stream().map(BalanceDO::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add));

        HashMap<String, Object> map3 = new HashMap<>();
        map3.put("userId", upUserid);
        map3.put("relationUserId", userDO.getId());
        map3.put("status", StatusTypeEnum.SUCCESS.getCode());
        map3.put("type", BalanceTypeEnum.N.getCode());
        List<BalanceDO> balanceDOList3 = balanceDao.list(map3);
        teamMemberDto.setRegisterAmount(balanceDOList3.stream().map(BalanceDO::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add));

        map.put("type", BalanceTypeEnum.G.getCode());
        List<BalanceDO> balanceDOList2 = balanceDao.list(map);
        teamMemberDto.setWithdraw(balanceDOList2.stream().map(BalanceDO::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add).multiply(new BigDecimal(-1)));

        HashMap<String, Object> mapOrder = new HashMap<>();
        mapOrder.put("userId", userDO.getId());
        List<OrderDO> orderDOList = list(mapOrder);
        if (lv.equals(1) && CollectionUtils.isNotEmpty(orderDOList)) {
            teamMemberDto.setContribution(orderDOList.stream().filter(o -> o.getOneAmount() != null).map(OrderDO::getOneAmount).reduce(BigDecimal.ZERO, BigDecimal::add));
        } else if (lv.equals(2) && CollectionUtils.isNotEmpty(orderDOList)) {
            teamMemberDto.setContribution(orderDOList.stream().filter(o -> o.getTwoAmount() != null).map(OrderDO::getTwoAmount).reduce(BigDecimal.ZERO, BigDecimal::add));
        } else if (lv.equals(3) && CollectionUtils.isNotEmpty(orderDOList)) {
            teamMemberDto.setContribution(orderDOList.stream().filter(o -> o.getThreeAmount() != null).map(OrderDO::getThreeAmount).reduce(BigDecimal.ZERO, BigDecimal::add));
        } else {
            Integer tmpLv = 0;
            UserDO oneUser = userService.get(userDO.getParentId());
            if (userDO.getParentId() != null && userDO.getParentId().equals(upUserid)) {
                tmpLv = 1;
            }
            if (oneUser != null && oneUser.getParentId() != null) {
                if (oneUser.getParentId().equals(upUserid)) {
                    tmpLv = 2;
                }
                UserDO twoUser = userService.get(oneUser.getParentId());
                if (twoUser != null) {
                    if (twoUser.getParentId() != null && twoUser.getParentId().equals(upUserid)) {
                        tmpLv = 3;
                    }
                }
            }


            if (tmpLv.equals(1) && CollectionUtils.isNotEmpty(orderDOList)) {
                teamMemberDto.setContribution(orderDOList.stream().filter(o -> o.getOneAmount() != null).map(OrderDO::getOneAmount).reduce(BigDecimal.ZERO, BigDecimal::add));
            } else if (tmpLv.equals(2) && CollectionUtils.isNotEmpty(orderDOList)) {
                teamMemberDto.setContribution(orderDOList.stream().filter(o -> o.getTwoAmount() != null).map(OrderDO::getTwoAmount).reduce(BigDecimal.ZERO, BigDecimal::add));
            } else if (tmpLv.equals(3) && CollectionUtils.isNotEmpty(orderDOList)) {
                teamMemberDto.setContribution(orderDOList.stream().filter(o -> o.getThreeAmount() != null).map(OrderDO::getThreeAmount).reduce(BigDecimal.ZERO, BigDecimal::add));
            } else {
                teamMemberDto.setContribution(BigDecimal.ZERO);
            }
        }
        return teamMemberDto;

    }


    public OrderDO get(Long id) {
        return orderDao.get(id);
    }

    public BigDecimal sumAmount(Map<String, Object> map) {
        return orderDao.sumAmount(map);
    }

    public List<OrderDO> list(Map<String, Object> map) {
        return orderDao.list(map);
    }

    public List<OrderDO> teamList(Map<String, Object> map) {
        return orderDao.teamList(map);
    }

    public int count(Map<String, Object> map) {
        return orderDao.count(map);
    }

    public int save(OrderDO order) {
        return orderDao.save(order);
    }

    public int update(OrderDO order) {
        return orderDao.update(order);
    }

    public int remove(Long id) {
        return orderDao.remove(id);
    }

    public int batchRemove(Long[] ids) {
        return orderDao.batchRemove(ids);
    }


}
