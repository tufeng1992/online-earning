package com.powerboot.dao;

import com.powerboot.domain.DictDO;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 数据配置表
 */
@Mapper
public interface DictDao {

	DictDO get(Long id);
	
	List<DictDO> list(Map<String, Object> map);

	List<DictDO> listByKey(String key);

	List<DictDO> listByKeys(@Param("keys") List<String> keys);
	
	int count(Map<String, Object> map);
	
	int save(DictDO dict);
	
	int update(DictDO dict);
	
	int remove(Long id);
	
	int batchRemove(Long[] ids);

	DictDO selectOneByKey(String key);
}
