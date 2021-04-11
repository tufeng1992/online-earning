package com.powerboot.dao;

import com.powerboot.domain.FinancialOrderDO;
import com.powerboot.param.FinancialOrderParam;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 理财订单表
 *
 */
@Mapper
public interface FinancialOrderDao {

	List<Integer> getExpiredOrder();

	FinancialOrderDO getById(@Param("id") Integer id);
	
	List<FinancialOrderDO> getByParams(@Param("param") FinancialOrderParam param);
	
	int count(@Param("param") FinancialOrderParam param);
	
	int save(FinancialOrderDO financialOrder);

	int saveBatch(@Param("list") List<FinancialOrderDO> list);
	
	int update(@Param("entity") FinancialOrderDO entity, @Param("param") FinancialOrderParam param);

	BigDecimal getInAmountSum(Map<String, Object> map);
}
