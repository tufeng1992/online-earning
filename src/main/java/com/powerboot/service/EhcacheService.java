package com.powerboot.service;

import com.powerboot.consts.DictAccount;
import com.powerboot.utils.RedisUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


@Service
public class EhcacheService {

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
        HashMap<Integer, List<Integer>> hashMap = new HashMap<>();
        String vip1 = RedisUtils.getValue(DictAccount.VIP1, String.class);
        String vip2 = RedisUtils.getValue(DictAccount.VIP2, String.class);
        String vip3 = RedisUtils.getValue(DictAccount.VIP3, String.class);
        String vip4 = RedisUtils.getValue(DictAccount.VIP4, String.class);
        String[] infoArray1 = vip1.split(",");
        String[] infoArray2 = vip2.split(",");
        String[] infoArray3 = vip3.split(",");
        String[] infoArray4 = vip4.split(",");

        List<Integer> list1 = new ArrayList<>();
        for (int j = 0; j < infoArray1.length; j++) {
            list1.add(Integer.valueOf(infoArray1[j]));
        }
        hashMap.put(1, list1);

        List<Integer> list2  = new ArrayList<>();
        for (int j = 0; j < infoArray2.length; j++) {
            list2.add(Integer.valueOf(infoArray2[j]));
        }
        hashMap.put(2, list2);

        List<Integer> list3  = new ArrayList<>();
        for (int j = 0; j < infoArray3.length; j++) {
            list3.add(Integer.valueOf(infoArray3[j]));
        }
        hashMap.put(3, list3);

        List<Integer> list4  = new ArrayList<>();
        for (int j = 0; j < infoArray4.length; j++) {
            list4.add(Integer.valueOf(infoArray4[j]));
        }
        hashMap.put(4, list4);

        return hashMap;
    }

}
