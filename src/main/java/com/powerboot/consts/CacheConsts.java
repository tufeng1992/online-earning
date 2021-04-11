package com.powerboot.consts;

import com.powerboot.utils.DateUtils;
import com.powerboot.utils.RedisUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;

public class CacheConsts {

    public static final String HOME_BANNER_CACHE_KEY = "HOME_BANNER:%s";
    public static final Integer HOME_BANNER_CACHE_LIVE_TIME = 60*60*24*30;
    public static final String RESULT_BANNER_CACHE_KEY = "RESULT_BANNER:%s";
    public static final Integer RESULT_BANNER_CACHE_LIVE_TIME = 60*60*24*30;
    public static final String MENU_ICON_CACHE_KEY = "MENU_ICON:%s";
    public static final Integer MENU_ICON_CACHE_LIVE_TIME = 60*60*24*30;

    //验证码
    public static final String VER_CODE = "ver_code:%s:%s";
    //ip
    public static final String IP_CODE = "ip_code:%s:%s";
    //手机号
    public static final String PHONE_CODE = "phone_code:%s:%s";

    public static final String SSO_USER = "sso:%s";

    //借款列表
    public static final String LOAN_LIST = "loan_list";

    //邀请url前缀
    public static final String INVITATION_PRE_URL = "INVITATION_PRE_URL";

    //封盘提现
    public static final String ST_WITHDRAWAL = "ST_WITHDRAWAL:%s";

    //用户每日提现key
    public static String getTodayRechargeKey(Long userId){
        return DateUtils.format(new Date(),DateUtils.SIMPLE_DATEFORMAT_YMD) + userId;
    }

}
