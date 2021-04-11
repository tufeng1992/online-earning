package com.powerboot.dao;

import com.powerboot.domain.InviteLogDO;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 邀请记录表
 *
 */
@Mapper
public interface InviteLogDao {

    InviteLogDO get(Integer id);

    List<InviteLogDO> list(Map<String, Object> map);

    List<InviteLogDO> count( @Param("inviteUserId") Integer inviteUserId,@Param("firstRechargeDate") Date firstRechargeDate);

    int save(InviteLogDO inviteLog);

    int update(InviteLogDO inviteLog);

    int updateByNewUserId(InviteLogDO inviteLog);

    int remove(Integer id);

    int batchRemove(Integer[] ids);
}
