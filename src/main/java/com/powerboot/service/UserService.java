package com.powerboot.service;

import com.baomidou.mybatisplus.mapper.Condition;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.powerboot.base.BaseResponse;
import com.powerboot.common.ErrorEnums;
import com.powerboot.config.BaseException;
import com.powerboot.consts.CacheConsts;
import com.powerboot.consts.DictConsts;
import com.powerboot.consts.I18nEnum;
import com.powerboot.consts.TipConsts;
import com.powerboot.dao.BalanceDao;
import com.powerboot.dao.UserDao;
import com.powerboot.domain.BalanceDO;
import com.powerboot.domain.InviteLogDO;
import com.powerboot.domain.UserDO;
import com.powerboot.enums.BalanceTypeEnum;
import com.powerboot.enums.InviteUserStatusEnum;
import com.powerboot.enums.StatusTypeEnum;
import com.powerboot.enums.UserRoleEnum;
import com.powerboot.request.ModifyPasswordRequest;
import com.powerboot.request.RegisterRequest;
import com.powerboot.utils.MobileUtil;
import com.powerboot.utils.RedisUtils;
import com.powerboot.utils.StringRandom;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private static Logger logger = LoggerFactory.getLogger(UserService.class);
    @Autowired
    private UserDao userDao;
    @Autowired
    private BalanceDao balanceDao;
    @Autowired
    private InviteLogService inviteLogService;
    @Resource(name = "commonExecutor")
    private ExecutorService commonExecutor;

    public UserDO getUser(Long parentId) {
        return userDao.get(parentId);
    }

    public BigDecimal getUserRechargeAmount(Long id) {
        return userDao.getUserRechargeAmount(id);
    }

    public Integer getInviteUserCount(Long parentId) {
        return userDao.getInviteUserCount(parentId);
    }

    public List<UserDO> getUserByParentId(Long parentId) {
        return userDao.getUserByParentId(parentId);
    }

    public List<UserDO> getUserByParentIdByPage(Map<String, Object> map) {
        return userDao.getUserByParentIdByPage(map);
    }

    public List<UserDO> getUserList(List<Long> list) {
        return userDao.getUserList(list);
    }

    public List<UserDO> getUserListByPage(List<Long> list, Integer offset, Integer limit) {
        return userDao.getUserListByPage(list, offset, limit);
    }


    public UserDO get(Long id) {
        UserDO userDO = userDao.get(id);
        if (userDO == null) {
            throw new BaseException(I18nEnum.LOGIN_TIMEOUT_FAIL.getMsg());
        }
        return userDO;
    }

    public List<UserDO> getUserByAccountNumber(String accountNumber) {
        return userDao.getUserByAccountNumber(accountNumber);
    }

    public UserDO getByMobileAndAppId(String mobile, String appId) {
        EntityWrapper<UserDO> condition = new EntityWrapper();
        condition.and()
                .eq("mobile", mobile)
                .eq("app_id", appId);
        List<UserDO> userDOList = userDao.selectList(condition);
        return CollectionUtils.isEmpty(userDOList) ? null : userDOList.get(0);
    }

    public UserDO getByMobile(String mobile) {
        EntityWrapper<UserDO> condition = new EntityWrapper();
        condition.and()
                .eq("mobile", mobile);
        List<UserDO> userDOList = userDao.selectList(condition);
        return CollectionUtils.isEmpty(userDOList) ? null : userDOList.get(0);
    }

    public int updateByIdAndVersion(UserDO user) {
        return userDao.update(user);
    }

    public int updateClose(Long userId) {
        return userDao.updateClose(userId);
    }

    public int updateFirstRechargeById(Long id) {
        return userDao.updateFirstRechargeById(id);
    }

    /**
     * 添加销售账号
     *
     * @param count 个数
     * @param mobileStart 开始账号
     */
    public void insertSale(Integer count, Integer mobileStart) {
        for (int i = 0; i < count; i++) {
            UserDO userDO = new UserDO();
            userDO.setNikeName("运营" + mobileStart);
            userDO.setAppId("com.cn.aww");
            userDO.setMobile(mobileStart.toString());
            userDO.setPassword("12345");
            //生成自己的邀请码
            String numberAndLetterRandom = StringRandom.getNumberAndLetterRandom(8);
            while (userDao.getByReferralCode(numberAndLetterRandom) != null) {
                numberAndLetterRandom = StringRandom.getNumberAndLetterRandom(8);
            }
            userDO.setReferralCode(numberAndLetterRandom);
            userDao.insertSale(userDO);
            mobileStart++;
        }
    }

    public Integer getRegisterIpCount(String registerIp) {
        return userDao.getRegisterIpCount(registerIp);
    }

    @Transactional(rollbackFor = Exception.class)
    public BaseResponse register(RegisterRequest registerRequest, String ip) {

        Integer ipRegisterMaxCount = RedisUtils.getValue(DictConsts.IP_REGISTER_MAX_COUNT, Integer.class);
        if (ipRegisterMaxCount == null) {
            ipRegisterMaxCount = 8;
        }
        if (ipRegisterMaxCount.compareTo(getRegisterIpCount(ip)) < 0) {
            return BaseResponse.fail(I18nEnum.IP_REGISTER_COUNT.getMsg());
        }
        String verificationCode = registerRequest.getVerificationCode();
        //验证码校验
        if (org.apache.commons.lang.StringUtils.isBlank(registerRequest.getMobile())) {
            return BaseResponse.fail(I18nEnum.MOBILE_NOT_EMPTY.getMsg());
        }
        int index = registerRequest.getMobile().length();

        if (!MobileUtil.isValidMobile(registerRequest.getMobile())) {
            return BaseResponse.fail(I18nEnum.MOBILE_NUMBER_FAIL.getMsg());
        }
        registerRequest.setMobile(MobileUtil.replaceValidMobile(registerRequest.getMobile()));
        String mobile = registerRequest.getMobile();
        if (!MobileUtil.isValidMobile(mobile)) {
            return BaseResponse.fail(I18nEnum.MOBILE_ERROR.getMsg());
        }
        //判断手机号是否已注册
        UserDO existence = userDao.getByMobileAndAppId(mobile, registerRequest.getAppId());
        if (existence != null) {
            return BaseResponse.fail(ErrorEnums.MOBILE_REGISTERED.getCode(), ErrorEnums.MOBILE_REGISTERED.getMsg());
        }

        String appId = registerRequest.getAppId();
        String codeKey = String.format(CacheConsts.VER_CODE, mobile, appId);
        String value = RedisUtils.getValue(codeKey, String.class);
        //校验验证码正确性
        if (!verificationCode.equals(value)) {
            return BaseResponse.fail(I18nEnum.OTP_MISMATCH.getMsg());
        }

        UserDO userDO = new UserDO();
        userDO.setAppId(registerRequest.getAppId());
        userDO.setDeviceNumber(registerRequest.getDeviceNumber());
        userDO.setRegisterIp(ip);
        userDO.setMobile(mobile);
        userDO.setPassword(registerRequest.getPassword());
        userDO.setEmail(registerRequest.getEmail());
        userDO.setSdkType(registerRequest.getSdkType());
        //生成自己的邀请码
        String numberAndLetterRandom = StringRandom.getNumberAndLetterRandom(8);
        while (userDao.getByReferralCode(numberAndLetterRandom) != null) {
            numberAndLetterRandom = StringRandom.getNumberAndLetterRandom(8);
        }
        userDO.setReferralCode(numberAndLetterRandom);

        if (StringUtils.isNotBlank(registerRequest.getReferralCode())) {
            UserDO referralUser = userDao.getByReferralCode(registerRequest.getReferralCode());
            if (referralUser != null) {
                //如果邀请人已被禁止分享
                if (0 == referralUser.getShareFlag()) {
                    return BaseResponse.fail(RedisUtils.getString(DictConsts.RISK_USER_BLACK_TIP));
                }
                //如果邀请人为黑名单
                if (referralUser.getBlackFlag() == 1) {
                    return BaseResponse.fail(RedisUtils.getString(DictConsts.RISK_USER_BLACK_TIP));
                }
                if (UserRoleEnum.SALE.getCode().equals(referralUser.getRole())) {
                    //销售邀请
                    userDO.setSaleId(referralUser.getId());
                    userDO.setUserLevel(1);
                    userDO.setTeamFlag(referralUser.getTeamFlag());
                } else {
                    //用户推荐
                    userDO.setSaleId(referralUser.getSaleId());
                    userDO.setParentId(referralUser.getId());
                    Integer userLevel = referralUser.getUserLevel() + 1;
                    userDO.setUserLevel(userLevel);
                    userDO.setTeamFlag(referralUser.getTeamFlag());
                }
            } else {
                //推广找不到的人
                userDO.setSaleId(1L);
                userDO.setUserLevel(1);
                userDO.setTeamFlag(getTeamFlag());
            }
        } else {
            //没有被推广的人
            userDO.setSaleId(1L);
            userDO.setUserLevel(1);
            userDO.setTeamFlag(getTeamFlag());
        }
        //送余额
        String giveSwitch = RedisUtils.getString(DictConsts.REG_GIVE_MONEY_SWITCH);
        String giveMoney = RedisUtils.getString(DictConsts.REG_GIVE_MONEY);
        if ("1".equals(giveSwitch)) {
            userDO.setBalance(new BigDecimal(giveMoney));
        } else {
            userDO.setBalance(BigDecimal.ONE);
        }
        int saveSuccess = userDao.insert(userDO);
        if (saveSuccess <= 0) {
            return BaseResponse.fail(I18nEnum.REGISTER_FAIL.getMsg());
        }
        commonExecutor.execute(()->{
            Date now = new Date();
            //添加赠送余额
            if ("1".equals(giveSwitch)) {
                BalanceDO balance = new BalanceDO();
                balance.setAmount(new BigDecimal(giveMoney));
                balance.setType(BalanceTypeEnum.M.getCode());
                balance.setUserId(userDO.getId());
                balance.setSaleId(userDO.getSaleId());
                balance.setWithdrawAmount(BigDecimal.ZERO);
                balance.setServiceFee(BigDecimal.ZERO);
                balance.setStatus(StatusTypeEnum.SUCCESS.getCode());
                balance.setCreateTime(now);
                balance.setUpdateTime(now);
                balance.setOrderNo("reg-" + userDO.getId());
                balanceDao.save(balance);
            }
            //用户邀请记录
            if (userDO.getParentId() != null && userDO.getParentId() > 0) {
                InviteLogDO inviteLog = new InviteLogDO();
                inviteLog.setInviteUserId(userDO.getParentId().intValue());
                inviteLog.setNewUserId(userDO.getId().intValue());
                inviteLog.setNewUserStatus(InviteUserStatusEnum.REG.getCode());
                inviteLog.setRegDate(now);
                inviteLogService.save(inviteLog);
            }
        });
        //验证码校验完删除缓存验证码
        RedisUtils.remove(codeKey);
        return BaseResponse.success();
    }

    private String getTeamFlag() {
        String key = "teamFlagPolling";
        Long value = RedisUtils.increment(key, 1);
        return ((value % 2) == 0) ? "hb" : "sx";
    }

    public BaseResponse modifyPassword(ModifyPasswordRequest request) {
        //校验验证码正确性
        String verificationCode = request.getVerificationCode();
        String appId = request.getAppId();
        int index = request.getMobile().length();
        if (!MobileUtil.isValidMobile(request.getMobile())){
            return BaseResponse.fail(I18nEnum.MOBILE_NUMBER_FAIL.getMsg());
        }
        request.setMobile(MobileUtil.replaceValidMobile(request.getMobile()));
        String mobile = request.getMobile();

        String codeKey = String.format(CacheConsts.VER_CODE, mobile, appId);
        String value = RedisUtils.getValue(codeKey, String.class);
        if (!verificationCode.equals(value)) {
            return BaseResponse.fail(I18nEnum.OTP_MISMATCH.getMsg());
        }
        UserDO userDO = userDao.getByMobileAndAppId(mobile, appId);
        if (userDO == null) {
            return BaseResponse.fail(I18nEnum.OTP_MISMATCH.getMsg());
        }
        userDO.setPassword(request.getPassword());
        if (userDao.update(userDO) <= 0) {
            return BaseResponse.fail(I18nEnum.MODIFY_PASSWORD_FAIL.getMsg());
        }
        //验证码校验完删除缓存验证码
        RedisUtils.remove(codeKey);
        return BaseResponse.success();
    }

    /**
     * 用户余额更新
     *
     * @return 扣减失败为余额不足, 或者并发扣钱
     */
    public int updateMoney(Long userId, BigDecimal amount) {
        return userDao.updateMoney(userId, amount);
    }

    /**
     * 用户余额加钱
     *
     * @param version 版本号
     * @return 加钱失败为并发加钱
     */
    public Boolean addMoney(Long userId, BigDecimal amount, Integer version) {
        return userDao.addMoney(userId, amount, version) > 0 ? true : false;
    }

    /**
     * 用户余额减钱
     *
     * @param version 版本号
     * @return 扣减失败为余额不足, 或者并发扣钱
     */
    public Boolean reduceMoney(Long userId, BigDecimal amount, Integer version) {
        return userDao.reduceMoney(userId, amount, version) > 0 ? true : false;
    }

    /**
     * 更新VIP等级
     */
    public int updateUserVIP(Long userId, Integer memberLevel) {
        return userDao.updateUserVIP(userId, memberLevel);
    }

    /**
     * 获取下级用户数量
     */
    public List<UserDO> getTotalPeople(Long userId) {
        return userDao.getTotalPeople(userId);
    }

    /**
     * 获取下级用户数量-count数值
     */
    public Integer getCountPeople(Long userId) {
        return userDao.getCountPeople(userId);
    }

    /**
     * 获取所有VIP
     */
    public List<UserDO> getAllVIP() {
        return userDao.getAllVIP();
    }

    /**
     * 根据saleId获取所有VIP
     * @param map
     * @return
     */
    public List<UserDO> getAllVIPBySale(Map<String, Object> map) {
        return userDao.getAllVIPBySale(map);
    }

    /**
     * 根据saleId获取所有VIP
     * @return
     */
    public List<UserDO> getAllSaleId() {
        return userDao.getAllSaleId();
    }


    public Integer getUserCount(Integer role, LocalDate startDate, LocalDate endDate, Long saleId) {
        return userDao.getUserCount(role, startDate, endDate, null, null, saleId);
    }

    public Integer count(Map<String, Object> params) {
        return userDao.count(params);
    }

    public List<UserDO> list(Map<String, Object> params) {
        return userDao.list(params);
    }

    public Integer getUserReferral(Integer role, LocalDate startDate, LocalDate endDate, Long saleId) {
        return userDao.getUserCount(role, startDate, endDate, 1, null, saleId);
    }

    public Integer getSaleReferral(Integer role, LocalDate startDate, LocalDate endDate, Long saleId) {
        return userDao.getUserCount(role, startDate, endDate, null, 1, saleId);
    }

    /**
     * 获取客户下所有的3级内用户
     */
    public List<UserDO> getAllByParentId(Long parentId) {
        List<UserDO> list = new ArrayList<>();
        List<UserDO> listOne = userDao.getUserByParentId(parentId);
        list.addAll(listOne);
        if (CollectionUtils.isNotEmpty(listOne)) {
            List<UserDO> listTwo = userDao
                .getUserList(listOne.stream().map(UserDO::getId).collect(Collectors.toList()));
            list.addAll(listTwo);
            if (CollectionUtils.isNotEmpty(listTwo)) {
                List<UserDO> listThree = userDao
                    .getUserList(listTwo.stream().map(UserDO::getId).collect(Collectors.toList()));
                list.addAll(listThree);
            }
        }
        return list;
    }

    /**
     * 获取客户下直接邀请用户充值的集合数
     */
    public Integer getVipRedisList(Long parentId) {
        String countStr = RedisUtils.getValue("VIP_USER_COUNT" + parentId, String.class);
        if (StringUtils.isNotBlank(countStr)) {
            return Integer.valueOf(countStr);
        }
        long start = System.currentTimeMillis();
        List<UserDO> list = getUserByParentId(parentId);
        List<UserDO> vipList = list.stream().filter(o -> o.getFirstRecharge().equals(1)).collect(Collectors.toList());
        int expireTime = 15;

        RedisUtils.setValue("VIP_USER_COUNT" + parentId, String.valueOf(vipList.size()), expireTime);
        long end = System.currentTimeMillis();
        logger.info("查询邀请人充值数耗时:" + (end - start) + " userId:" + parentId + "数量:" + vipList.size());
        return vipList.size();
    }

    /**
     * 根据银行账户查询
     * @param accountNumber
     * @return
     */
    public List<UserDO> selectByAccountNumber(String accountNumber) {
        if (StringUtils.isEmpty(accountNumber)) {
            return Lists.newArrayList();
        }
        return userDao.getUserByAccountNumber(accountNumber);
    }

    /**
     * 封禁用户
     * @param user
     * @return
     */
    public int blockedUser(UserDO user) {
        user.setLoginFlag(0);
        user.setBlackFlag(1);
        logger.info("blockedUser userId:{}", user.getId());
        return updateByIdAndVersion(user);
    }
}
