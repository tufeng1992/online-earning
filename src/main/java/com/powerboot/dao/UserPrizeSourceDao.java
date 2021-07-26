package com.powerboot.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.powerboot.domain.UserPrizeSourceDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserPrizeSourceDao extends BaseMapper<UserPrizeSourceDO> {

    List<Long> getNoPrizeInviteUser(@Param("userId") Long userId);
}
