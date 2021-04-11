package com.powerboot.dao;

import com.powerboot.domain.SmsDO;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 短信验证码表
 */
@Mapper
public interface SmsDao {

	SmsDO get(Long id);

	SmsDO getByMsgid(@Param("msgid") String msgid);
	
	List<SmsDO> list(Map<String, Object> map);
	
	int count(Map<String, Object> map);
	
	int save(SmsDO sms);
	
	int update(SmsDO sms);
	
	int remove(Long id);
	
	int batchRemove(Long[] ids);

	SmsDO getLastByAppTel(@Param("tel") String tel,@Param("appId") String appId);
}
