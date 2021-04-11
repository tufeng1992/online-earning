package com.powerboot.consts;

import com.powerboot.utils.RedisUtils;
import org.apache.commons.lang3.StringUtils;

public class BlackHelper {

    public static final String BLACK_USER_FEISHU_TITLE = "触发风控黑名单";

    //风控黑名单
    public static final String BLACK_USER_ID = "BLACK_USER:ID:%s";
    public static final String BLACK_USER_NAME = "BLACK_USER:NAME:%s";
    public static final String BLACK_USER_MOBILE = "BLACK_USER:MOBILE:%s";
    public static final String BLACK_USER_EMAIL = "BLACK_USER:EMAIL:%s";
    public static final String BLACK_USER_IFSC = "BLACK_USER:IFSC:%s";

    public static void setBlackId(String id) {
        String key = String.format(BLACK_USER_ID, id);
        RedisUtils.setValue(key, "1", DictConsts.DICT_CACHE_LIVE_TIME);
    }

    public static boolean blackId(String id) {
        String key = String.format(BLACK_USER_ID, id);
        String value = RedisUtils.getString(key);
        return StringUtils.isNotBlank(value);
    }

    public static boolean blackMobile(String mobile) {
        String key = String.format(BLACK_USER_MOBILE, mobile);
        String value = RedisUtils.getString(key);
        return StringUtils.isNotBlank(value);
    }

    public static boolean blackName(String name) {
        String key = String.format(BLACK_USER_NAME, name);
        String value = RedisUtils.getString(key);
        return StringUtils.isNotBlank(value);
    }

    public static boolean blackEmail(String email) {
        String key = String.format(BLACK_USER_EMAIL, email);
        String value = RedisUtils.getString(key);
        return StringUtils.isNotBlank(value);
    }

    public static boolean blackIfsc(String ifsc) {
        String key = String.format(BLACK_USER_IFSC, ifsc);
        String value = RedisUtils.getString(key);
        return StringUtils.isNotBlank(value);
    }
}
