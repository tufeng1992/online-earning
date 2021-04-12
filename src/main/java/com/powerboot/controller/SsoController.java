package com.powerboot.controller;

import com.powerboot.base.BaseResponse;
import com.powerboot.consts.BlackHelper;
import com.powerboot.consts.CacheConsts;
import com.powerboot.consts.DictConsts;
import com.powerboot.consts.TipConsts;
import com.powerboot.domain.LoginLogDO;
import com.powerboot.domain.UserDO;
import com.powerboot.request.LoginRequest;
import com.powerboot.request.LogoutRequest;
import com.powerboot.request.ModifyPasswordRequest;
import com.powerboot.request.RegisterRequest;
import com.powerboot.response.SsoResponse;
import com.powerboot.service.DictService;
import com.powerboot.service.LoginLogService;
import com.powerboot.service.UserService;
import com.powerboot.utils.CryptoUtils;
import com.powerboot.utils.MobileUtil;
import com.powerboot.utils.RedisUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import java.util.Arrays;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

    @ApiOperation("登录")
    @PostMapping("/login")
    public BaseResponse<SsoResponse> login(HttpServletRequest httpServletRequest,
        @RequestBody @ApiParam(name = "登录条件") LoginRequest request) {
        SsoResponse ssoResponse = new SsoResponse();
        String whitePhone = RedisUtils.getValue(DictConsts.WHITE_PHONE, String.class);
        //白名单用户不验证手机正确性直接校验账号密码
        if (StringUtils.isBlank(whitePhone) ||
            !Arrays.asList(whitePhone.split(",")).contains(request.getMobile())) {
            //手机校验
            if (StringUtils.isBlank(request.getMobile())) {
                return BaseResponse.fail(TipConsts.MOBILE_NOT_EMPTY);
            }
            int index = request.getMobile().length();
            if (index != 10 &&
                (index != 13 || !request.getMobile().substring(0, 3).equals(MobileUtil.NIGERIA_MOBILE_PREFIX))) {
                return BaseResponse.fail("Wrong mobile number.");
            }
            request.setMobile(MobileUtil.NIGERIA_MOBILE_PREFIX + request.getMobile().substring(index - 10));

        }
        //校验用户是否已注册
        UserDO user = userService.getByMobileAndAppId(request.getMobile(), request.getAppId());
        if (user == null) {
            return BaseResponse.fail(TipConsts.NO_REGISTER);
        }
        //是否允许登录
        if (0 == user.getLoginFlag()) {
            return BaseResponse.fail(RedisUtils.getString(DictConsts.RISK_USER_BLACK_TIP));
        }

        if (!user.getPassword().equals(request.getPassword())) {
            return BaseResponse.fail(TipConsts.PASSWORD_ERROR);
        }

        //        String ssoKey = String.format(CacheConsts.SSO_USER,request.getDeviceNumber());
        //        RedisUtils.setValue(ssoKey,user.getId());

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
        }

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
        return userService.register(registerRequest, super.getIp(httpServletRequest));
    }

    @ApiOperation("修改密码")
    @PostMapping("/modify")
    public BaseResponse modifyPassword(@Valid @RequestBody ModifyPasswordRequest request) {
        return userService.modifyPassword(request);
    }
}
