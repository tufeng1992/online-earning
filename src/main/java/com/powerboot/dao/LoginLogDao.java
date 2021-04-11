package com.powerboot.dao;

import com.powerboot.domain.LoginLogDO;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

/**
 * 登录日志表
 */
@Mapper
public interface LoginLogDao {

	LoginLogDO get(Long id);
	
	List<LoginLogDO> list(Map<String, Object> map);
	
	int count(Map<String, Object> map);
	
	int save(LoginLogDO log);
	
	int update(LoginLogDO log);
	
	int remove(Long id);
	
	int batchRemove(Long[] ids);
}
