package com.powerboot.utils.payful.core;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.digest.DigestAlgorithm;
import cn.hutool.crypto.digest.Digester;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import com.powerboot.consts.DictConsts;
import com.powerboot.utils.DateUtils;
import com.powerboot.utils.RedisUtils;
import com.powerboot.utils.qeapay.domain.QeaPayCreatePayRes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;
import java.util.TreeMap;

@Component
@Slf4j
public class PayfulClient {

    private String getMerchant() {
//        return "Ja1dDwL2QWq";
        return RedisUtils.getString(DictConsts.PAYFUL_MERCHANT);
    }

    private String getApiKey() {
        return "yryrP9VOmL1Z4xDEqUY8a4aSkNYKfmT9";
//        return RedisUtils.getString(DictConsts.PAYFUL_APIKEY);
    }

    private String getTo() {
        return "3CfAmGHRAVfs92niqrbDDJDiRGj4QgJojd";
//        return RedisUtils.getString(DictConsts.PAYFUL_TO);
    }

    private String getBaseUrl() {
        //https://paxful.com
        return RedisUtils.getString(DictConsts.PAYFUL_BASE_URL);
    }

    /**
     * 创建支付
     * @param orderNo
     * @param amount
     * @return
     */
    public String createPay(String orderNo, BigDecimal amount) {
        String trackId = RandomUtil.randomString(100);
        String params = "merchant=" + getMerchant() +
        "&apikey=" + getApiKey() + "&nonce=" + System.currentTimeMillis() +
                "&to=" + getTo() + "&fiat_amount=" + amount.toString() + "&saveaddress=1&fiat_currency=GHS&track_id=" + trackId;
        String apiSeal = generatorApiSeal(params);
        String url = getBaseUrl() + "/wallet/pay?" + params + "&apiseal=" + apiSeal;
        log.info("payful createPay url:" + url);
//        HttpResponse httpResponse = HttpUtil.createGet(url).execute();
        return url;
    }

    /**
     * 生成apiseal
     * @param params
     * @return
     */
    private String generatorApiSeal(String params) {
        Digester shaDig = new Digester(DigestAlgorithm.SHA256);
        return shaDig.digestHex(params);
    }


}
