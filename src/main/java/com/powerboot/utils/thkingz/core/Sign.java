package com.powerboot.utils.thkingz.core;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.powerboot.utils.gms.core.SignUtil;
import com.powerboot.utils.thkingz.model.ThkingzBaseRes;
import com.powerboot.utils.thkingz.model.ThkingzQueryPayRes;

import java.math.BigInteger;
import java.net.URISyntaxException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;

public class Sign {
//    public static void main(String[] args) throws URISyntaxException {
//        SortedMap<Object,Object> params = new TreeMap<Object,Object> ();
//
//        params.put("id","123");
//
//        params.put("name","123");
//
//        params.put("age","123");
//
//
//        System.out.println(">>>>>>>"+signForInspiry(params,"123456"));
//
//    }


    /**
     * 生成签名；
     *
     * @param params
     * @return
     */
    static public String signForInspiry(Map params, String key) {

        StringBuffer sbkey = new StringBuffer();
        sbkey.append(SignUtil.sortData(params));

        sbkey = sbkey.append("&key=" + key);
        System.out.println("当前key:"+sbkey);
        //MD5加密,结果转换为大写字符
        String sign = MD5(sbkey.toString()).toUpperCase();
        return sign;
    }

    /**
     * 对字符串进行MD5加密
     *
     * @param str 需要加密的字符串
     * @return 小写MD5字符串 32位
     */
    static public String MD5(String str) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(str.getBytes("utf-8"));
            byte[] b = md.digest();

            StringBuffer buf = new StringBuffer("");
            for (int offset = 0; offset < b.length; offset++) {
                int i = b[offset];
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }

            return buf.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
//        System.out.println(Sign.MD5("appid=1059943&out_trade_no=33pxjdqjwdma&key=2SOqWM9r3bXi3SzeHa5XxVoh1xByUQ1P"));
        String json = " {\"code\":1,\"msg\":\"success\",\"data\":{\"id\":817,\"order_no\":\"E606583504116595\"," +
                "\"out_trade_no\":\"7o26mrvk5m27\",\"amount\":\"172.200\",\"new_amount\":\"2671.700\"," +
                "\"account_number\":\"5900387217\",\"account_name\":\"Manatsakorn Saelee\"," +
                "\"old_amount\":\"2845.622\",\"callback_url\":\"http:\\/\\/3.16.84.30\\/pay\\/callback\\/thkingz\\/payOut\"," +
                "\"apply_time\":\"2021-06-06 13:45:50\",\"deal_time\":\"\",\"bank_id\":\"BBL\",\"status\":\"1\",\"bank_type\":\"银行卡\"}}";

        ThkingzBaseRes res = JSONObject.parseObject(json, ThkingzBaseRes.class);
        if (null != res && "1".equalsIgnoreCase(res.getCode())) {
            ThkingzQueryPayRes queryPayRes = ((com.alibaba.fastjson.JSONObject) res.getData()).toJavaObject(ThkingzQueryPayRes.class);
            System.out.println(queryPayRes);
        }
    }

}
