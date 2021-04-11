package com.powerboot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.powerboot.dao.FinancialOrderDao;
import com.powerboot.domain.FinancialOrderDO;
import com.powerboot.param.FinancialOrderParam;
import org.springframework.transaction.annotation.Transactional;


@Service
public class FinancialOrderService {

	@Autowired
	private FinancialOrderDao financialOrderDao;

	public List<Integer> getExpiredOrder(){
		return financialOrderDao.getExpiredOrder();
	}

	public FinancialOrderDO getById(Integer id){
		return financialOrderDao.getById(id);
	}

	public List<FinancialOrderDO> getByParams(FinancialOrderParam param){
		return financialOrderDao.getByParams(param);
	}

	public int count(FinancialOrderParam param){
		return financialOrderDao.count(param);
	}

    @Transactional(rollbackFor = Exception.class)
	public int save(FinancialOrderDO financialOrder){
		if(financialOrder.getSaleId()==null){
			financialOrder.setSaleId(1L);
		}
		return financialOrderDao.save(financialOrder);
	}

    @Transactional(rollbackFor = Exception.class)
    public int saveBatch(List<FinancialOrderDO> list){
        return financialOrderDao.saveBatch(list);
    }

    @Transactional(rollbackFor = Exception.class)
	public int update(FinancialOrderDO entity,FinancialOrderParam param){
		return financialOrderDao.update(entity,param);
	}

	public BigDecimal getInAmountSum(Map<String, Object> map){
		return financialOrderDao.getInAmountSum(map);
	}
}
