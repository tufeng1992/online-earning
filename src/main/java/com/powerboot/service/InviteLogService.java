package com.powerboot.service;

import com.powerboot.dao.InviteLogDao;
import com.powerboot.domain.InviteLogDO;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class InviteLogService {

    @Autowired
    private InviteLogDao inviteLogDao;

    public InviteLogDO get(Integer id) {
        return inviteLogDao.get(id);
    }

    public List<InviteLogDO> list(Map<String, Object> map) {
        return inviteLogDao.list(map);
    }

    public List<InviteLogDO>  count( Integer inviteUserId,Date firstRechargeDate) {
        return inviteLogDao.count(inviteUserId,firstRechargeDate);
    }

    public int save(InviteLogDO inviteLog) {
        return inviteLogDao.save(inviteLog);
    }

    public int update(InviteLogDO inviteLog) {
        return inviteLogDao.update(inviteLog);
    }

    public int updateByNewUserId(InviteLogDO inviteLog) {
        return inviteLogDao.updateByNewUserId(inviteLog);
    }

    public int remove(Integer id) {
        return inviteLogDao.remove(id);
    }

    public int batchRemove(Integer[] ids) {
        return inviteLogDao.batchRemove(ids);
    }

}
