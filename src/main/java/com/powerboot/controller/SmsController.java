package com.powerboot.controller;

import com.powerboot.base.BaseResponse;
import com.powerboot.consts.BlackHelper;
import com.powerboot.consts.DictConsts;
import com.powerboot.consts.I18nEnum;
import com.powerboot.consts.TipConsts;
import com.powerboot.domain.SmsDO;
import com.powerboot.request.BackSmsRequest;
import com.powerboot.request.SendSmsRequest;
import com.powerboot.service.SmsService;
import com.powerboot.service.UserService;
import com.powerboot.utils.MobileUtil;
import com.powerboot.utils.RedisUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * 短信验证码表
 *
 */

@RestController
@RequestMapping("/sms")
@Api(tags = "短信服务")
public class SmsController extends BaseController {
	private Logger logger = LoggerFactory.getLogger(SmsController.class);
	@Autowired
	private SmsService smsService;
	@Autowired
	private UserService userService;

	@GetMapping("ip")
	public BaseResponse sendVerCode(HttpServletRequest request){
		return BaseResponse.success(getIp(request));
	}

	@ApiOperation(value = "发送验证码")
	@PostMapping
	public BaseResponse<String> sendVerCode(HttpServletRequest request,@RequestBody @Valid SendSmsRequest param){
		if (!MobileUtil.isValidMobile(param.getTel())){
			return BaseResponse.fail(I18nEnum.MOBILE_NUMBER_FAIL.getMsg());
		}
		param.setTel(MobileUtil.replaceValidMobile(param.getTel()));
		String ip = getIp(request);

		//风控
		if (BlackHelper.blackMobile(param.getTel())) {
			return BaseResponse.fail(RedisUtils.getString(DictConsts.RISK_USER_BLACK_TIP));
		}

		//被禁止登陆
		String loginKey = String.format(DictConsts.USER_LOGIN_FLAG,param.getTel());
		if(RedisUtils.getString(loginKey)!=null){
            return BaseResponse.fail(RedisUtils.getString(DictConsts.RISK_USER_BLACK_TIP));
		}
		return smsService.sendVerCode(param,ip);
	}

	@ApiOperation(value = "短信发送回调接口", hidden = true)
	@GetMapping
	public String backSms(HttpServletRequest request,BackSmsRequest param){
		logger.info("[短信回调]----参数:{}",param);
		if (param == null){
			return "fail";
		}
		if (!"onepiece-himalayan".equals(param.getReceiver())){
			return "fail";
		}
		String ip = getIp(request);
		logger.info("[短信回调]----当前ip:{}",ip);
		SmsDO smsDO = smsService.getByMsgid(param.getMsgid());
		if (smsDO != null){
			smsDO.setStatus(param.getStatus());
			smsService.update(smsDO);
		}

		if (!"DELIVRD".equals(param.getStatus())){
			logger.info("发送短信失败 -------- 手机号:"+param.getMobile()+"----短信发送失败,msgid:"+param.getMsgid()+",----status:"+param.getStatus());
		}
		return "OK";
	}

}
