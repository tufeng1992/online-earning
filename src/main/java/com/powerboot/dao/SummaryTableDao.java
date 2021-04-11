package com.powerboot.dao;

import com.powerboot.domain.SummaryTableDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;


@Mapper
public interface SummaryTableDao {

    List<SummaryTableDO> list();

    int save(SummaryTableDO model);

    int updateByDate(@Param("userReferral") Integer userReferral,
                     @Param("saleReferral") Integer saleReferral,
                     @Param("id") Integer id);

    int updateById(@Param("withdrawAmount") BigDecimal withdrawAmount,
                     @Param("vipPayAmount") BigDecimal vipPayAmount,
                     @Param("id") Integer id);

}
