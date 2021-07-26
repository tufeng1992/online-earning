package com.powerboot.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.powerboot.domain.BlackUserLogDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 拉黑用户记录表
 */
@Mapper
public interface BlackUserLogDao extends BaseMapper<BlackUserLogDO> {
}
