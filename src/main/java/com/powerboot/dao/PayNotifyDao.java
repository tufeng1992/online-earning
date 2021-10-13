package com.powerboot.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.powerboot.domain.PayCertDO;
import com.powerboot.domain.PayDO;
import com.powerboot.domain.PayNotifyDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PayNotifyDao extends BaseMapper<PayNotifyDO> {
}
