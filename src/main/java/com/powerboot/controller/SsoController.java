package com.powerboot.controller;

import com.google.common.collect.Maps;
import com.powerboot.base.BaseResponse;
import com.powerboot.consts.*;
import com.powerboot.domain.BalanceDO;
import com.powerboot.domain.InviteLogDO;
import com.powerboot.domain.LoginLogDO;
import com.powerboot.domain.UserDO;
import com.powerboot.enums.BalanceTypeEnum;
import com.powerboot.enums.InviteUserStatusEnum;
import com.powerboot.enums.StatusTypeEnum;
import com.powerboot.enums.UserRoleEnum;
import com.powerboot.request.LoginRequest;
import com.powerboot.request.LogoutRequest;
import com.powerboot.request.ModifyPasswordRequest;
import com.powerboot.request.RegisterRequest;
import com.powerboot.response.SsoResponse;
import com.powerboot.service.*;
import com.powerboot.utils.CryptoUtils;
import com.powerboot.utils.MobileUtil;
import com.powerboot.utils.RedisUtils;
import com.powerboot.utils.StringRandom;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "登录/注册/登出/修改密码")
@RestController
@RequestMapping("/sso")
public class SsoController extends BaseController {

    private static Logger logger = LoggerFactory.getLogger(SsoController.class);
    @Autowired
    private UserService userService;
    @Autowired
    private DictService dictService;
    @Autowired
    private LoginLogService loginLogService;

    @Autowired
    private BalanceService balanceService;

    @Resource(name = "commonExecutor")
    private ExecutorService commonExecutor;
    @Autowired
    private InviteLogService inviteLogService;

    @Autowired
    private BlackUserLogService blackUserLogService;

    @ApiOperation("登录")
    @PostMapping("/login")
    @Transactional(rollbackFor = Exception.class)
    public BaseResponse<SsoResponse> login(@RequestBody @ApiParam(name = "登录条件") LoginRequest request,
                                           HttpServletRequest httpServletRequest) {
        SsoResponse ssoResponse = new SsoResponse();
        String ip = this.getIp(httpServletRequest);
        //白名单用户不验证手机正确性直接校验账号密码
        if (!checkWhitePhone(request.getMobile(), null)) {
            //手机校验
            if (StringUtils.isBlank(request.getMobile())) {
                return BaseResponse.fail(I18nEnum.MOBILE_NOT_EMPTY.getMsg());
            }
            if (!MobileUtil.isValidMobile(request.getMobile())) {
                return BaseResponse.fail(I18nEnum.MOBILE_NUMBER_FAIL.getMsg());
            }
            request.setMobile(MobileUtil.replaceValidMobile(request.getMobile()));
        }
        //校验用户是否已注册
        UserDO user = userService.getByMobileAndAppId(request.getMobile(), request.getAppId());
        if (user == null) {
            return BaseResponse.fail(I18nEnum.NO_REGISTER.getMsg());
        }
        //是否允许登录
        if (0 == user.getLoginFlag()) {
            return BaseResponse.fail(RedisUtils.getString(DictConsts.RISK_USER_BLACK_TIP));
        }
        if (1 == user.getBlackFlag()) {
            return BaseResponse.fail(I18nEnum.BLACK_LOGIN_FAIL.getMsg());
        }
        if (!user.getPassword().equals(request.getPassword())) {
            return BaseResponse.fail(I18nEnum.PASSWORD_ERROR.getMsg());
        }

        //验证白名单
        if (!checkWhitePhone(request.getMobile(), user)) {
            //设备号风控校验
            if (StringUtils.isNotBlank(request.getDeviceNumber()) && !checkWhiteDevice(request.getDeviceNumber(), user)) {
                logger.info("deviceNumber valid, mobile:{},  deviceNumber:{}", request.getMobile(), request.getDeviceNumber());
                Map<String, Object> deviceMap = Maps.newHashMap();
                deviceMap.put("deviceNumber", request.getDeviceNumber());
                deviceMap.put("blackFlag", 0);
                List<UserDO> limit = userService.list(deviceMap);
                Integer userLoginDeviceLimitCount = RedisUtils.getValue(DictConsts.USER_LOGIN_DEVICE_LIMIT_COUNT, Integer.class);
                if (limit.size() > userLoginDeviceLimitCount) {
                    //如果超过限制，截取出最早登录的用户
                    List<UserDO> tempList = limit.subList(0, userLoginDeviceLimitCount);
                    //如果当前用户不在截取出来的用户列表中，则拉黑当前用户
                    UserDO temp = tempList.stream().filter(u -> u.getId().equals(user.getId())).findFirst().orElse(null);
                    if (null == temp) {
                        userService.blockedUser(user);
                        blackUserLogService.addBlackUserLog(user.getId(), "设备号重复：" + request.getDeviceNumber(), user.getSaleId());
                        return BaseResponse.fail(I18nEnum.REPETITION_DEVICE_LOGIN_FAIL.getMsg());
                    }
                }
                if (StringUtils.isBlank(user.getDeviceNumber()) || !user.getDeviceNumber().equals(request.getDeviceNumber())) {
                    user.setDeviceNumber(request.getDeviceNumber());
                    userService.updateByIdAndVersion(user);
                }
            }
            //ip风控校验
            if (StringUtils.isNotBlank(ip) && !checkWhiteIp(ip, user)) {
                logger.info("ip valid, mobile:{},  ip:{}", request.getMobile(), ip);
                Map<String, Object> ipMap = Maps.newHashMap();
                ipMap.put("registerIp", ip);
                ipMap.put("blackFlag", 0);
                List<UserDO> limit = userService.list(ipMap);
                Integer userLoginIpLimitCount = RedisUtils.getValue(DictConsts.USER_LOGIN_IP_LIMIT_COUNT, Integer.class);
                if (limit.size() > userLoginIpLimitCount) {
                    //如果超过限制，截取出最早登录的用户
                    List<UserDO> tempList = limit.subList(0, userLoginIpLimitCount);
                    //如果当前用户不在截取出来的用户列表中，则拉黑当前用户
                    UserDO temp = tempList.stream().filter(u -> u.getId().equals(user.getId())).findFirst().orElse(null);
                    if (null == temp) {
                        userService.blockedUser(user);
                        blackUserLogService.addBlackUserLog(user.getId(), "IP重复：" + ip, user.getSaleId());
                        return BaseResponse.fail(I18nEnum.REPETITION_DEVICE_LOGIN_FAIL.getMsg());
                    }
                }
            }
        }

        String ssoKey = String.format(CacheConsts.SSO_USER, user.getId());

        if (!RedisUtils.hasKey(ssoKey)) {
            Map<String, Object> loginLogParams = Maps.newHashMap();
            loginLogParams.put("userId", user.getId());
            int loginCount = loginLogService.count(loginLogParams);
            if (loginCount == 0) {
                //判断是否第一次登陆，给上级返现
                UserDO parentUser = userService.getUser(user.getParentId());
                if (null != parentUser) {
                    //用户邀请奖励
                    userInvite(user);

                    String firstLoginParentSwitch = RedisUtils.getString(DictConsts.FIRST_LOGIN_PARENT_SWITCH);
                    if (StringUtils.isNotBlank(firstLoginParentSwitch)
                            && "true".equalsIgnoreCase(firstLoginParentSwitch)) {
                        //生成随机订单号
                        String orderFirstNo = parentUser.getId() + "p";
                        String orderNo = orderFirstNo + StringRandom.getNumberAndLetterRandom(12 - orderFirstNo.length());
                        Date now = new Date();
                        BalanceDO parent = new BalanceDO();
                        //获取首冲返现级别金额
                        String balanceInfo = RedisUtils.getString(DictConsts.FIRST_LOGIN_PARENT_AMOUNT);
                        String[] infoArray = balanceInfo.split("\\|");
                        BigDecimal parentAmount = new BigDecimal(infoArray[parentUser.getMemberLevel() - 1]);
                        parent.setAmount(parentAmount);
                        parent.setType(BalanceTypeEnum.N.getCode());
                        parent.setUserId(parentUser.getId());
                        parent.setRelationUserId(user.getId());
                        parent.setWithdrawAmount(BigDecimal.ZERO);
                        parent.setServiceFee(BigDecimal.ZERO);
                        parent.setStatus(StatusTypeEnum.SUCCESS.getCode());
                        parent.setCreateTime(now);
                        parent.setUpdateTime(now);
                        parent.setOrderNo(orderNo);
                        balanceService.addBalanceDetail(parent);
                    }
                }
            }
        }

        String sign = CryptoUtils.encode(user.getId().toString());
        ssoResponse.setUser(user);
        ssoResponse.setSign(sign);
        ssoResponse.setNewUserFlag(false);

        //插入登录日志
        try {
            LoginLogDO loginLogDO = new LoginLogDO();
            loginLogDO.setUserId(user.getId());
            loginLogDO.setMobile(request.getMobile());
            loginLogDO.setAppId(request.getAppId());
            loginLogDO.setDeviceNumber(request.getDeviceNumber());
            loginLogDO.setIp(ip);
            loginLogDO.setFirstLogin(ssoResponse.getNewUserFlag() ? 1 : 2);
            loginLogDO.setCreateTime(new Date());
            loginLogDO.setUpdateTime(new Date());
            loginLogService.saveAsyn(loginLogDO);
        } catch (Exception e) {
            logger.error("error", e);
        }
        RedisUtils.setValue(ssoKey, user.getId().toString());
        if (StringUtils.isNotBlank(request.getAdid())) {
            RedisUtils.setValue(String.format(CacheConsts.ADID_USER, user.getId()), request.getAdid());
        }
        return BaseResponse.success(ssoResponse);
    }

    @ApiOperation("登出")
    @PostMapping("/logout")
    public BaseResponse<Boolean> logout(@RequestBody @ApiParam(name = "登出条件") LogoutRequest request) {
        Long userId = getUserId(request);
        //删除登录缓存
        String ssoKey = String.format(CacheConsts.SSO_USER, userId);
        RedisUtils.remove(ssoKey);

        return BaseResponse.success(true);
    }

    @ApiOperation("获取客户端IP")
    @GetMapping("/getIp")
    public BaseResponse<String> getClientIp(HttpServletRequest request) {
        return BaseResponse.success(super.getIp(request));
    }

    @ApiOperation("注册")
    @PostMapping("/register")
    public BaseResponse register(@Valid @RequestBody RegisterRequest registerRequest,
        HttpServletRequest httpServletRequest) {
        //手机号风控
        if (BlackHelper.blackMobile(registerRequest.getMobile())) {
            return BaseResponse.fail(RedisUtils.getString(DictConsts.RISK_USER_BLACK_TIP));
        }
        registerRequest.setMobile(registerRequest.getMobile().trim());
        registerRequest.setPassword(registerRequest.getPassword().trim());
        registerRequest.setEmail(registerRequest.getEmail().trim());
        return userService.register(registerRequest, super.getIp(httpServletRequest));
    }

    @ApiOperation("修改密码")
    @PostMapping("/modify")
    public BaseResponse modifyPassword(@Valid @RequestBody ModifyPasswordRequest request) {
        String modifySwitch = RedisUtils.getString(DictConsts.MODIFY_PASSWORD_SWITCH);
        if (StringUtils.isNotBlank(modifySwitch) && "false".equalsIgnoreCase(modifySwitch)) {
            return BaseResponse.fail(I18nEnum.MODIFY_PASSWORD_NOT_SUPPORT.getMsg());
        }
        return userService.modifyPassword(request);
    }

    /**
     * 用户邀请奖励
     * @param user
     */
    private void userInvite(UserDO user) {
        if (user.getParentId() != null) {
            //更新邀请记录
            commonExecutor.execute(() -> {
                //邀请奖励次数限制
                Integer rewardCountLimit = RedisUtils.getInteger(DictConsts.INVITE_REWARD_COUNT_LIMIT);
                //用户已获邀请奖励次数
                Integer userInviteRewardCount = RedisUtils.getInteger(String.format(CacheConsts.USER_INVITE_REWARD_COUNT, user.getParentId()));
                if (null == userInviteRewardCount) {
                    userInviteRewardCount = 0;
                }
                if (userInviteRewardCount >= rewardCountLimit) {
                    return;
                }
                //用户已邀请数量
                Integer userInviteCount = RedisUtils.getInteger(String.format(CacheConsts.USER_INVITE_COUNT, user.getParentId()));
                if (null == userInviteCount) {
                    userInviteCount = 0;
                }
                userInviteCount++;
                //邀请奖励频率
                Integer rewardFrequencyLimit = RedisUtils.getInteger(DictConsts.INVITE_FREQUENCY_LIMIT);
                RedisUtils.setInteger(String.format(CacheConsts.USER_INVITE_COUNT, user.getParentId()), userInviteCount);
                if (userInviteCount % rewardFrequencyLimit != 0) {
                    return;
                }
                userInviteRewardCount++;
                RedisUtils.setInteger(String.format(CacheConsts.USER_INVITE_REWARD_COUNT, user.getParentId()), userInviteRewardCount);
                //邀请奖励金额
                BigDecimal rewardAmount = new BigDecimal(RedisUtils.getString(DictConsts.INVITE_REWARD_AMOUNT));

                //生成随机订单号
                String orderFirstNo = user.getParentId() + "p";
                String orderNo = orderFirstNo + StringRandom.getNumberAndLetterRandom(12 - orderFirstNo.length());
                Date now = new Date();
                BalanceDO parent = new BalanceDO();
                parent.setAmount(rewardAmount);
                parent.setType(BalanceTypeEnum.N.getCode());
                parent.setUserId(user.getParentId());
                parent.setRelationUserId(user.getId());
                parent.setWithdrawAmount(BigDecimal.ZERO);
                parent.setServiceFee(BigDecimal.ZERO);
                parent.setStatus(StatusTypeEnum.SUCCESS.getCode());
                parent.setCreateTime(now);
                parent.setUpdateTime(now);
                parent.setOrderNo(orderNo);
                balanceService.addBalanceDetail(parent);
            });
        }
    }

    /**
     * 验证白名单
     * @param mobile
     * @param userDO
     * @return
     */
    private boolean checkWhitePhone(String mobile, UserDO userDO) {
        logger.info("checkWhitePhone mobile:{}", mobile);
        //如果是客服则直接返回true
        if (null != userDO && UserRoleEnum.SALE.getCode().equals(userDO.getRole())) {
            logger.info("checkWhitePhone mobile:{} match SALE", mobile);
            return true;
        }
        //否则校验白名单配置
        String whitePhone = RedisUtils.getValue(DictConsts.WHITE_PHONE, String.class);
        if (StringUtils.isNotBlank(whitePhone) &&
                Arrays.asList(whitePhone.split(",")).contains(mobile)) {
            logger.info("checkWhitePhone mobile:{} match WHITE_PHONE", mobile);
            return true;
        }
        logger.info("checkWhitePhone mobile:{} not match", mobile);
        return false;
    }

    /**
     * 验证白名单
     * @param device
     * @param userDO
     * @return
     */
    private boolean checkWhiteDevice(String device, UserDO userDO) {
        //如果是客服则直接返回true
        if (null != userDO && UserRoleEnum.SALE.getCode().equals(userDO.getRole())) {
            return true;
        }
        //否则校验白名单配置
        String whiteDevice = RedisUtils.getValue(DictConsts.WHITE_DEVICE, String.class);
        if (StringUtils.isNotBlank(whiteDevice) &&
                Arrays.asList(whiteDevice.split(",")).contains(device)) {
            return true;
        }
        return false;
    }

    /**
     * 验证白名单
     * @param device
     * @param userDO
     * @return
     */
    private boolean checkWhiteIp(String ip, UserDO userDO) {
        //如果是客服则直接返回true
        if (null != userDO && UserRoleEnum.SALE.getCode().equals(userDO.getRole())) {
            return true;
        }
        //否则校验白名单配置
        String whiteIp = RedisUtils.getValue(DictConsts.WHITE_IP, String.class);
        if (StringUtils.isNotBlank(whiteIp) &&
                Arrays.asList(whiteIp.split(",")).contains(ip)) {
            return true;
        }
        return false;
    }
}
