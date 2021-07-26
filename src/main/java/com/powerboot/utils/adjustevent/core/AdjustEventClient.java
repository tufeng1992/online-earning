package com.powerboot.utils.adjustevent.core;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.powerboot.consts.DictConsts;
import com.powerboot.utils.RedisUtils;
import com.powerboot.utils.StringUtils;
import com.powerboot.utils.opay.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * adjust事件埋点上报
 */
@Component
@Slf4j
public class AdjustEventClient {

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    private String getAuthorization() {
        return "Bearer " + RedisUtils.getValue(DictConsts.ADJUST_EVENT_AUTH, String.class);
    }

    private String getBaseUrl() {
        return RedisUtils.getValue(DictConsts.ADJUST_EVENT_URL, String.class);
    }

    private String getAppToken() {
        return RedisUtils.getValue(DictConsts.ADJUST_EVENT_APP_TOKEN, String.class);
    }

    private String getRechargeSuccess() {
        return RedisUtils.getValue(DictConsts.ADJUST_EVENT_RECHARGE_SUCCESS, String.class);
    }

    private String getVipSuccess() {
        return RedisUtils.getValue(DictConsts.ADJUST_EVENT_VIP_SUCCESS, String.class);
    }

    private Map<String, String> header = Maps.newHashMap();

    private Map<String, String> getHeader() {
        if (header.isEmpty()) {
            synchronized (this) {
                if (header.isEmpty()) {
                    header.put("Authorization", getAuthorization());
                }
            }
        }
        return header;
    }

    /**
     * 提交上报事件
     * @param eventToken
     * @param adid
     */
    public void submitEvent(String eventToken, String adid) {
        JSONObject body = new JSONObject();
        String url = getBaseUrl() + "/event?s2s=1&app_token=" + getAppToken() + "&event_token="
                + eventToken + "&adid=" + adid;
        log.info("submit event url:{}", url);
        JSONObject res = HttpUtil.post4JsonObj(url, getHeader(), body).orElseThrow(() -> new RuntimeException("请求响应为空"));
        log.info("submit event url:{}, res:{}", url, res.toJSONString());
    }

    /**
     * 提交充值成功事件
     * @param adid
     */
    public void rechargeSuccess(String adid) {
        if (StringUtils.isBlank(adid)) {
            return;
        }
        executorService.execute(() -> submitEvent(getRechargeSuccess(), adid));
    }

    /**
     * 提交购买vip成功事件
     * @param adid
     */
    public void vipSuccess(String adid) {
        if (StringUtils.isBlank(adid)) {
            return;
        }
        executorService.execute(() -> submitEvent(getVipSuccess(), adid));
    }

}
