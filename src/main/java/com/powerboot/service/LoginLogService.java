package com.powerboot.service;

import com.powerboot.common.JsonUtils;
import com.powerboot.dao.LoginLogDao;
import com.powerboot.domain.LoginLogDO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class LoginLogService {

	private static Logger logger = LoggerFactory.getLogger(LoginLogService.class);


	public static ExecutorService executorService = Executors.newSingleThreadExecutor();

	@Autowired
	private LoginLogDao logDao;
	
	public LoginLogDO get(Long id){
		return logDao.get(id);
	}
	
	public List<LoginLogDO> list(Map<String, Object> map){
		return logDao.list(map);
	}
	
	public int count(Map<String, Object> map){
		return logDao.count(map);
	}

	public void saveAsyn(LoginLogDO log){
		executorService.execute(() -> {
            int count = logDao.save(log);
            if(count==0){
                logger.info("插入登录日志失败，json={}", JsonUtils.toJSONString(log));
            }
        });
	}
	
	public int save(LoginLogDO log){
		return logDao.save(log);
	}
	
	public int update(LoginLogDO log){
		return logDao.update(log);
	}
	
	public int remove(Long id){
		return logDao.remove(id);
	}
	
	public int batchRemove(Long[] ids){
		return logDao.batchRemove(ids);
	}
	
}
