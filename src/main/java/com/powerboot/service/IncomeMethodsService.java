package com.powerboot.service;

import com.powerboot.consts.DictConsts;
import com.powerboot.dao.IncomeMethodsDao;
import com.powerboot.domain.IncomeMethodsDO;
import com.powerboot.utils.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IncomeMethodsService {

    @Autowired
    private IncomeMethodsDao incomeMethodsDao;

    /**
     * 获取功能简介
     */
    public List<IncomeMethodsDO> getContent() {
        String incomeMethodsKey = "IncomeMethods";
        return RedisUtils.setListIfNotExists(incomeMethodsKey, () -> {
                return incomeMethodsDao.getContent();
            },
            DictConsts.DICT_CACHE_LIVE_TIME, IncomeMethodsDO.class);
    }


}
