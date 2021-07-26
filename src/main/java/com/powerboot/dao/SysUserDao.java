package com.powerboot.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.powerboot.domain.DictDO;
import com.powerboot.domain.SysUserDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 系统用户表
 */
@Mapper
public interface SysUserDao extends BaseMapper<SysUserDO> {
}
