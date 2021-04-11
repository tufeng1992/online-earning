package com.powerboot.dao;

import com.powerboot.domain.ProductDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * 刷单商品表
 * @author system
 */
@Mapper
public interface ProductDao {

	ProductDO get(Long id);
	
	List<ProductDO> list(Map<String,Object> map);
	
	int count(Map<String,Object> map);
	
	int save(ProductDO product);
	
	int update(ProductDO product);
	
	int remove(Long id);
	
	int batchRemove(Long[] ids);
}
