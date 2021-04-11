package com.powerboot.service;

import com.powerboot.dao.SummaryTableDao;
import com.powerboot.domain.SummaryTableDO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class SummaryTableService {
    @Autowired
    private SummaryTableDao summaryTableDao;

    public int save(SummaryTableDO model){
        return summaryTableDao.save(model);
    }

    public List<SummaryTableDO> list(){
        return summaryTableDao.list();
    }

    public int updateByDate(Integer userReferral, Integer saleReferral, Integer id){
        return summaryTableDao.updateByDate(userReferral,saleReferral,id);
    }
}
