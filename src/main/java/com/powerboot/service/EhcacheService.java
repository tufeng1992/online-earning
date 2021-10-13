package com.powerboot.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.Condition;
import com.google.common.base.Splitter;
import com.powerboot.consts.DictAccount;
import com.powerboot.dao.MemberInfoDao;
import com.powerboot.domain.MemberInfoDO;
import com.powerboot.utils.RedisUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


@Service
public class EhcacheService {

    @Autowired
    private MemberInfoDao memberInfoDao;

//    /**
//     * 获取余额等下相关配置信息
//     *
//     * @return
//     */
//    public HashMap<Integer, List<Integer>> getBalanceInfo() {
//        HashMap<Integer, List<Integer>> hashMap = new HashMap<>();
//        String balanceInfo = RedisUtils.getValue(DictAccount.BALANCE_INFO, String.class);
//        String[] infoArray = balanceInfo.split("\\|");
//
//        for (int i = 0; i < infoArray.length; i++) {
//            List<Integer> list = new ArrayList<>();
//            String[] op = infoArray[i].split(",");
//            for (int j = 0; j < op.length; j++) {
//                list.add(Integer.valueOf(op[j]));
//            }
//            hashMap.put(i + 1, list);
//        }
//
//        return hashMap;
//    }
//
//    public HashMap<Integer, List<Integer>> getPriceSection() {
//        HashMap<Integer, List<Integer>> hashMap = new HashMap<>();
//        String balanceInfo = RedisUtils.getValue(DictAccount.PRICE_SECTION, String.class);
//        String[] infoArray = balanceInfo.split("\\|");
//
//        for (int i = 0; i < infoArray.length; i++) {
//            List<Integer> list = new ArrayList<>();
//            String[] op = infoArray[i].split(",");
//            for (int j = 0; j < op.length; j++) {
//                list.add(Integer.valueOf(op[j]));
//            }
//            hashMap.put(i + 1, list);
//        }
//
//        return hashMap;
//    }

    /**
     * 获取VIP配置信息
     *
     * @return
     */
    public HashMap<Integer, List<Integer>> getVipInfo() {
        String vipPowerCache = RedisUtils.getString(DictAccount.VIP_POWER);
        if (StringUtils.isNotBlank(vipPowerCache)) {
            return JSONObject.parseObject(vipPowerCache, HashMap.class);
        }
        Condition condition = new Condition();
        List<MemberInfoDO> memberInfoDOList = memberInfoDao.selectList(condition);
        HashMap<Integer, List<Integer>> hashMap = new HashMap<>();
        memberInfoDOList.forEach(memberInfoDO -> {
            String[] info = memberInfoDO.getVipPower().split(",");
            List<Integer> list = new ArrayList<>();
            for (int j = 0; j < info.length; j++) {
                list.add(Integer.valueOf(info[j]));
            }
            hashMap.put(memberInfoDO.getType(), list);
        });
        RedisUtils.setValue(DictAccount.VIP_POWER, JSONObject.toJSONString(hashMap));
        return hashMap;
    }

}
