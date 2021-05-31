package com.powerboot.service;

import com.powerboot.base.BaseResponse;
import com.powerboot.common.StringCommonUtils;
import com.powerboot.config.BaseException;
import com.powerboot.config.SmsSendConfig;
import com.powerboot.consts.CacheConsts;
import com.powerboot.consts.DictConsts;
import com.powerboot.consts.I18nEnum;
import com.powerboot.consts.TipConsts;
import com.powerboot.dao.SmsDao;
import com.powerboot.dao.SmsSendResponse;
import com.powerboot.domain.SmsDO;
import com.powerboot.request.SendSmsRequest;
import com.powerboot.utils.DateUtils;
import com.powerboot.utils.RedisUtils;
import com.powerboot.utils.StringRandom;

import java.util.Arrays;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class SmsService {
    @Autowired
    private SmsDao smsDao;
    @Autowired
    private DictService dictService;
    @Autowired
    private SmsSendConfig smsSendConfig;

    public SmsDO get(Long id) {
        return smsDao.get(id);
    }

    public SmsDO getByMsgid(String msgid) {
        return smsDao.getByMsgid(msgid);
    }

    public List<SmsDO> list(Map<String, Object> map) {
        return smsDao.list(map);
    }

    public int count(Map<String, Object> map) {
        return smsDao.count(map);
    }

    public int save(SmsDO sms) {
        return smsDao.save(sms);
    }

    public int update(SmsDO sms) {
        return smsDao.update(sms);
    }

    public int remove(Long id) {
        return smsDao.remove(id);
    }

    public int batchRemove(Long[] ids) {
        return smsDao.batchRemove(ids);
    }

    public SmsDO getLastByAppTel(String tel, String appId) {
        return smsDao.getLastByAppTel(tel, appId);
    }

    @Transactional
    public BaseResponse<String> sendVerCode(SendSmsRequest param, String ip) {

        //白名单手机不发送验证码
        String whitePhone = RedisUtils.getValue(DictConsts.WHITE_PHONE, String.class);
        if (StringUtils.isNotBlank(whitePhone) &&
                Arrays.asList(whitePhone.split(",")).contains(param.getTel().substring(param.getTel().length() - 10))) {
            return BaseResponse.success("60");
        }

        String tel = param.getTel();
        String appId = param.getAppId();
        //本地缓存
        String codeKey = String.format(CacheConsts.VER_CODE, tel, appId);
        String ipKey = String.format(CacheConsts.IP_CODE, ip, appId);
        String phoneKey = String.format(CacheConsts.PHONE_CODE, tel, appId);
        Integer phoneCount = RedisUtils.getValue(phoneKey, Integer.class);
        Integer smsResendTime = 60;

        if (StringUtils.isNotBlank(RedisUtils.getValue(DictConsts.SMS_RESEND_TIME, String.class))) {
            smsResendTime = Integer.valueOf(Objects.requireNonNull(RedisUtils.getValue(DictConsts.SMS_RESEND_TIME, String.class)));
        }

        Integer ipCount = 0;
        if (RedisUtils.getValue(ipKey, Integer.class) != null) {
            ipCount = RedisUtils.getValue(ipKey, Integer.class);
        }

        if (phoneCount == null) {
            phoneCount = 0;
        }
        String ipCountString = RedisUtils.getValue(DictConsts.IP_SEND_MAX_COUNT, String.class);
        String phoneCountString = RedisUtils.getValue(DictConsts.PHONE_SEND_MAX_COUNT, String.class);
        String verCodeString = RedisUtils.getValue(DictConsts.VER_CODE_LENGTH, String.class);

        //验证相同ip短信发送次数
        Integer dictIpCount = ipCountString == null ? null : Integer.parseInt(ipCountString);
        if (dictIpCount != null && ipCount >= dictIpCount) {
            throw new BaseException(I18nEnum.REQUEST_LIMIT_FAIL.getMsg());
        }

        //验证相同手机号短信发送次数
        Integer dictPhoneCount = phoneCountString == null ? null : Integer.parseInt(phoneCountString);
        if (dictPhoneCount != null && phoneCount >= dictPhoneCount) {
            throw new BaseException(I18nEnum.REQUEST_LIMIT_FAIL.getMsg());
        }

        //验证码位数
        Integer codeNumber = verCodeString == null ? null : Integer.parseInt(verCodeString);
        String verCode = StringRandom.getStringRandom(codeNumber);
        //send sms
        String sendMsg = StringCommonUtils.buildString("[TaskP] your verification code is {}. To ensure information security, please do not tell others.", verCode);
        SmsDO lastTel = getLastByAppTel(tel, appId);
        Date now = new Date();
        //判断验证码时间不得小于间隔时间
        if (lastTel != null && now.compareTo(DateUtils.addSeconds(lastTel.getCreateTime(), smsResendTime)) < 0) {
            return BaseResponse.fail(I18nEnum.OPERATION_FAST.getMsg());
        }
        String sendSmsSwitch = RedisUtils.getString(DictConsts.SEND_SMS_SWITCH);
        boolean sendSmsSwitchFlag = StringUtils.isNotBlank(sendSmsSwitch) && "false".equalsIgnoreCase(sendSmsSwitch);
        BaseResponse<SmsSendResponse> result = null;
        if (sendSmsSwitchFlag) {
            SmsSendResponse response = new SmsSendResponse();
            response.setCode("0");
            result = BaseResponse.success(response);
        } else {
            result = smsSendConfig.sendVoiceMessage(tel, verCode);
        }
        SmsSendResponse smsSingleResponse = result.getResultData();
        String sendResult = "";
        if (smsSingleResponse != null && "0".equals(smsSingleResponse.getCode())) {
            sendResult = "send success";
        } else {
            sendResult = smsSingleResponse != null ? smsSingleResponse.getError() : "send msg fail";
        }
        SmsDO smsDO = new SmsDO();
        smsDO.setCreateTime(new Date());
        smsDO.setIp(ip);
        smsDO.setAppId(param.getAppId());
        smsDO.setMobile(tel);
        smsDO.setSendResult(sendResult);
        smsDO.setVerCode(verCode);
        smsDO.setCreateTime(new Date());
        smsDO.setUpdateTime(new Date());
        smsDO.setMsgid(smsSingleResponse != null ? smsSingleResponse.getMsgId() : "");
        int saveSuccess = save(smsDO);
        if (saveSuccess > 0) {
            String codeLiveString = RedisUtils.getValue(DictConsts.VER_CODE_LIVE_TIME, String.class);
            String ipLiveString = RedisUtils.getValue(DictConsts.IP_SEND_LIVE_TIME, String.class);
            String phoneLiveString = RedisUtils.getValue(DictConsts.PHONE_SEND_LIVE_TIME, String.class);
            //验证码失效时间
            Integer codeLive = codeLiveString == null ? null : Integer.parseInt(codeLiveString);
            //ip失效时间
            Integer ipLive = ipLiveString == null ? null : Integer.parseInt(ipLiveString);
            //手机号失效时间
            Integer phoneLive = phoneLiveString == null ? null : Integer.parseInt(phoneLiveString);
            RedisUtils.setValue(codeKey, verCode, codeLive == null ? 18000 : codeLive);

            RedisUtils.increment(ipKey, ipLive == null ? 36000 : ipLive);

            RedisUtils.increment(phoneKey, phoneLive == null ? 36000 : phoneLive);
            if (sendSmsSwitchFlag) {
                return BaseResponse.success(verCode);
            }
            return BaseResponse.success();
        } else {
            throw new BaseException(I18nEnum.SEND_MESSAGE_FAIL.getMsg());
        }
    }
}
