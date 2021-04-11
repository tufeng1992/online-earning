package com.powerboot.dao;

import com.powerboot.domain.FinancialProductDO;
import com.powerboot.param.FinancialProductParam;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 理财产品表
 *
 * @author auto_generator
 * @date 2020-10-30 23:10:02
 */
@Mapper
public interface FinancialProductDao {

	FinancialProductDO getById(@Param("id") Integer id);
	
	List<FinancialProductDO> getByParams(@Param("param") FinancialProductParam param);
	
	int count(@Param("param") FinancialProductParam param);
	
	int save(FinancialProductDO financialProduct);

	int saveBatch(@Param("list") List<FinancialProductDO> list);
	
	int update(@Param("entity") FinancialProductDO entity, @Param("param") FinancialProductParam param);
	
}
