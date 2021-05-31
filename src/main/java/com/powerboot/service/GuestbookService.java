package com.powerboot.service;

import com.powerboot.dao.GuestbookDao;
import com.powerboot.domain.GuestbookDO;
import com.powerboot.domain.UserDO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


@Service
public class GuestbookService {

	@Autowired
	private GuestbookDao guestbookDao;

	public GuestbookDO get(Long id){
		return guestbookDao.get(id);
	}
	
	
	public List<GuestbookDO> list(Map<String, Object> map){
		return guestbookDao.list(map);
	}

	public Integer selectUnreadCount(Map<String, Object> map) {
		return guestbookDao.selectUnreadCount(map);
	}
	
	
	public int count(Map<String, Object> map){
		return guestbookDao.count(map);
	}
	
	
	public int save(GuestbookDO guestbook){
		return guestbookDao.save(guestbook);
	}
	
	
	public int update(GuestbookDO guestbook){
		return guestbookDao.update(guestbook);
	}
	
	
	public int remove(Long id){
		return guestbookDao.remove(id);
	}
	
	
	public int batchRemove(Long[] ids){
		return guestbookDao.batchRemove(ids);
	}
	
}
