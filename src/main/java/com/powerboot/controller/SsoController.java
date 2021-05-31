package com.powerboot.controller;

import com.google.common.collect.Maps;
import com.powerboot.base.BaseResponse;
import com.powerboot.consts.*;
import com.powerboot.domain.BalanceDO;
import com.powerboot.domain.LoginLogDO;
import com.powerboot.domain.UserDO;
import com.powerboot.enums.BalanceTypeEnum;
import com.powerboot.enums.StatusTypeEnum;
import com.powerboot.request.LoginRequest;
import com.powerboot.request.LogoutRequest;
import com.powerboot.request.ModifyPasswordRequest;
import com.powerboot.request.RegisterRequest;
import com.powerboot.response.SsoResponse;
import com.powerboot.service.BalanceService;
import com.powerboot.service.DictService;
import com.powerboot.service.LoginLogService;
import com.powerboot.service.UserService;
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
import java.util.Map;
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

    @ApiOperation("登录")
    @PostMapping("/login")
    @Transactional(rollbackFor = Exception.class)
    public BaseResponse<SsoResponse> login(HttpServletRequest httpServletRequest,
        @RequestBody @ApiParam(name = "登录条件") LoginRequest request) {
        SsoResponse ssoResponse = new SsoResponse();
        String whitePhone = RedisUtils.getValue(DictConsts.WHITE_PHONE, String.class);
        //白名单用户不验证手机正确性直接校验账号密码
        if (StringUtils.isBlank(whitePhone) ||
            !Arrays.asList(whitePhone.split(",")).contains(request.getMobile())) {
            //手机校验
            if (StringUtils.isBlank(request.getMobile())) {
                return BaseResponse.fail(I18nEnum.MOBILE_NOT_EMPTY.getMsg());
            }
            int index = request.getMobile().length();
            if (!MobileUtil.isValidMobile(request.getMobile())) {
                return BaseResponse.fail(I18nEnum.MOBILE_NUMBER_FAIL.getMsg());
            }
            request.setMobile(MobileUtil.THAILAND_MOBILE_PREFIX + request.getMobile().substring(index - 10));

        }
        //校验用户是否已注册
        UserDO user = userService.getByMobileAndAppId(request.getMobile(), request.getAppId());
        if (user == null) {
            return BaseResponse.fail(I18nEnum.NO_REGISTER.getMsg());
        }
        if (StringUtils.isNotBlank(request.getDeviceNumber())) {
            Map<String, Object> deviceMap = Maps.newHashMap();
            deviceMap.put("deviceNumber", request.getDeviceNumber());
            int limit = userService.count(deviceMap);
            if (limit > 2) {
                return BaseResponse.fail(I18nEnum.REPETITION_DEVICE_LOGIN_FAIL.getMsg());
            }
            if (StringUtils.isBlank(user.getDeviceNumber()) || !user.getDeviceNumber().equals(request.getDeviceNumber())) {
                user.setDeviceNumber(request.getDeviceNumber());
                userService.updateByIdAndVersion(user);
            }
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

        String ssoKey = String.format(CacheConsts.SSO_USER, request.getDeviceNumber());
        String firstLoginParentSwitch = RedisUtils.getString(DictConsts.FIRST_LOGIN_PARENT_SWITCH);
        if (!RedisUtils.hasKey(ssoKey) && StringUtils.isNotBlank(firstLoginParentSwitch)
                && "true".equalsIgnoreCase(firstLoginParentSwitch)) {
            Map<String, Object> loginLogParams = Maps.newHashMap();
            loginLogParams.put("userId", user.getId());
            int loginCount = loginLogService.count(loginLogParams);
            if (loginCount == 0) {
                //判断是否第一次登陆，给上级返现
                UserDO parentUser = userService.getUser(user.getParentId());
                if (null != parentUser) {
                    //生成随机订单号
                    String orderFirstNo = parentUser.getId() + "p";
                    String orderNo = orderFirstNo + StringRandom.getNumberAndLetterRandom(12 - orderFirstNo.length());
                    Date now = new Date();
                    BalanceDO parent = new BalanceDO();
                    //获取首冲返现级别金额
                    BigDecimal parentAmount = new BigDecimal(RedisUtils.getString(DictConsts.FIRST_LOGIN_PARENT_AMOUNT));
                    parent.setAmount(parentAmount);
                    parent.setType(BalanceTypeEnum.M.getCode());
                    parent.setUserId(parentUser.getId());
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
            loginLogDO.setIp(this.getIp(httpServletRequest));
            loginLogDO.setFirstLogin(ssoResponse.getNewUserFlag() ? 1 : 2);
            loginLogDO.setCreateTime(new Date());
            loginLogDO.setUpdateTime(new Date());
            loginLogService.saveAsyn(loginLogDO);
        } catch (Exception e) {
            logger.error("error", e);
        }
        RedisUtils.setValue(ssoKey, user.getId().toString());

        return BaseResponse.success(ssoResponse);
    }

    @ApiOperation("登出")
    @PostMapping("/logout")
    public BaseResponse<Boolean> logout(@RequestBody @ApiParam(name = "登出条件") LogoutRequest request) {

        //删除登录缓存
        String ssoKey = String.format(CacheConsts.SSO_USER, request.getDeviceNumber());
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
}
